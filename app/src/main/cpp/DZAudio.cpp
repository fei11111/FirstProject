//
// Created by huangjf on 2024/2/29.
//

#include "DZAudio.h"

DZAudio::DZAudio(DZJNICall *dzjniCall, JNIEnv *env, AVFormatContext *pFormatContext,
                 int audioIndex) {
    this->dzjniCall = dzjniCall;
    this->env = env;
    this->pFormatContext = pFormatContext;
    this->audioIndex = audioIndex;
    //todo 切换不同播放器
    this->type = TYPE_SLES;
}

void DZAudio::callPlayError(ThreadMode threadMode, int errCode, char *msg) {
    release();
    this->dzjniCall->callPlayerError(threadMode, errCode, msg);
}

void DZAudio::prepare(ThreadMode threadMode) {
    //查找解码器
    codecParameters = pFormatContext->streams[audioIndex]->codecpar;
    const AVCodec *avCodec = avcodec_find_decoder(codecParameters->codec_id);
    if (avCodec == NULL) {
        LOGE("find decoder error");
        callPlayError(threadMode, CODEC_FIND_DECODER_ERROR_CODE, "find decoder error");
        return;
    }

    //打开解码器
    //解码器上下文
    avCodecContext = avcodec_alloc_context3(avCodec);
    if (avCodecContext == NULL) {
        LOGE("alloc avcodec context3 error");
        callPlayError(threadMode, CODEC_ALLOC_CONTEXT_ERROR_CODE, "alloc avcodec context3 error");
        return;
    }
    //设置对应的参数
    int avCodecParamterContextRes = avcodec_parameters_to_context(avCodecContext, codecParameters);
    if (avCodecParamterContextRes < 0) {
        LOGE("avcodec parameters to context error");
        callPlayError(threadMode, AVCODEC_PARAMETERS_TO_CONTEXT_ERROR_CODE,
                      "avcodec parameters to context error");
        return;
    }
    int avcodecOpenRes = avcodec_open2(avCodecContext, avCodec, NULL);
    if (avcodecOpenRes != 0) {
        callPlayError(threadMode, AVCODEC_OPEN_ERROR_CODE, "avcoder open error");
        return;
    }

    // ---------- 重采样 start ----------
    //转换器上下文
    this->swrContext = swr_alloc();
    enum AVSampleFormat out_formart = AV_SAMPLE_FMT_S16;
    int out_sample_rate = avCodecContext->sample_rate;
    swr_alloc_set_opts2(&swrContext, &avCodecContext->ch_layout, out_formart,
                        out_sample_rate, &avCodecContext->ch_layout,
                        avCodecContext->sample_fmt, avCodecContext->sample_rate, 0, NULL);

    swr_init(swrContext);
    this->out_buffer = (uint8_t *) av_malloc(AUDIO_SAMPLE_RATE * 2);
    // ---------- 重采样 end ----------

}

void DZAudio::release() {
    if (audioTrackObject != NULL) {
        env->DeleteGlobalRef(this->audioTrackObject);
    }
    if (codecParameters != NULL) {
        avcodec_parameters_free(&codecParameters);
    }
    if (avCodecContext != NULL) {
        avcodec_close(avCodecContext);
        avcodec_free_context(&avCodecContext);
    }

    if (swrContext != NULL) {
        swr_free(&swrContext);
    }

    if (out_buffer != NULL) {
        av_free(out_buffer);
    }

    while (!avFrame_queue.isEmpty()) {
        AVFrame *frame = avFrame_queue.pop();
        av_frame_unref(frame);
        av_frame_free(&frame);
    }
}

//读
void *readRun(void *arg) {
    DZAudio *dzAudio = (DZAudio *) arg;
    AVPacket *pkt = av_packet_alloc();
    while (av_read_frame(dzAudio->pFormatContext, pkt) >= 0) {
        AVFrame *frame = av_frame_alloc();
        //pkt 是压缩的数据，需要解码成pcm数据
        if (pkt->stream_index == dzAudio->audioIndex) {
            //音频
            int sendPacketRes = avcodec_send_packet(dzAudio->avCodecContext, pkt);
            if (sendPacketRes == 0) {
                int receiveFrameRes = avcodec_receive_frame(dzAudio->avCodecContext, frame);
                if (receiveFrameRes == 0) {
                    dzAudio->avFrame_queue.push(frame);
                }
            }
            //解引用
            av_packet_unref(pkt);
        }
    }

    //1.解引用 2.销毁pkt结构体数据 3.pkt = null
    LOGE("free引用");
    av_packet_free(&pkt);


    return 0;
}

//写
void *writeRun(void *arg) {
    DZAudio *dzAudio = (DZAudio *) arg;
    JNIEnv *env;
    if (dzAudio->dzjniCall->javaVm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
        LOGE("get child thread jniEnv error");
    }

    if (dzAudio->type == TYPE_AUDIO_TRACK) {
        //audioTrack
        dzAudio->startAudioTrack(env);
    } else {
        dzAudio->startSLES();
    }

    dzAudio->dzjniCall->javaVm->DetachCurrentThread();
    return 0;
}


int DZAudio::resampleAudio() {
    if (AVFrame *frame = avFrame_queue.pop()) {
        // AV_PACKET 压缩数据 -> AV_FRAME 解码后的数据
        //frame.data->java byte
        //大小 1s 44100点 2通道 每通道2字节
        //一帧不是1s frame->nb_samples
        swr_convert(swrContext, &out_buffer, frame->nb_samples,
                    (const uint8_t **) (frame->data),
                    frame->nb_samples);
        int size = av_samples_get_buffer_size(NULL,
                                              codecParameters->ch_layout.nb_channels,
                                              frame->nb_samples,
                                              AV_SAMPLE_FMT_S16, 1);
        // 解引用
        av_frame_unref(frame);
        av_frame_free(&frame);
        return size;
    }
}

void playerCallback(SLAndroidSimpleBufferQueueItf caller, void *pContext) {
    DZAudio *pAudio = (DZAudio *) pContext;
    int dataSize = pAudio->resampleAudio();
    (*caller)->Enqueue(caller, pAudio->out_buffer, dataSize);
}


void DZAudio::startSLES() {
    SLObjectItf engineObject = NULL;
    SLEngineItf engineEngine;
    //创建引擎
    slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    //实现引擎
    (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    //获取引擎接口
    (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
    //设置混音器:  创建输出混音器对象，实现输出混音器
    SLObjectItf outputMixObject = NULL;
    const SLInterfaceID ids[1] = {SL_IID_ENVIRONMENTALREVERB};
    const SLboolean req[1] = {SL_BOOLEAN_FALSE};
    //创建混音器
    (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, ids, req);
    //实现输出混音器
    (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;
    //获取混响接口
    (*outputMixObject)->GetInterface(outputMixObject, SL_IID_ENVIRONMENTALREVERB,
                                     &outputMixEnvironmentalReverb);
    SLEnvironmentalReverbSettings reverbSettings = SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;
    //设置混响
    (*outputMixEnvironmentalReverb)->SetEnvironmentalReverbProperties(outputMixEnvironmentalReverb,
                                                                      &reverbSettings);
    // 创建播放器
    SLObjectItf pPlayer = NULL;
    SLPlayItf pPlayItf = NULL;
    SLDataLocator_AndroidSimpleBufferQueue simpleBufferQueue = {
            SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
    // PCM 格式
    SLDataFormat_PCM formatPcm = {
            SL_DATAFORMAT_PCM,//pcm 格式
            2,//两声道
            SL_SAMPLINGRATE_44_1,//采样率44100 Hz
            SL_PCMSAMPLEFORMAT_FIXED_16,//采样位16位
            SL_PCMSAMPLEFORMAT_FIXED_16,//容器 16位
            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,//左右声道
            SL_BYTEORDER_LITTLEENDIAN};//小端格式
    //设置音频数据源：simpleBufferQueue（配置缓冲区）、 formatPcm（音频格式）
    SLDataSource audioSrc = {&simpleBufferQueue, &formatPcm};

    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&outputMix, NULL};
    SLInterfaceID interfaceIds[3] = {SL_IID_BUFFERQUEUE, SL_IID_VOLUME, SL_IID_PLAYBACKRATE};
    SLboolean interfaceRequired[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};
    (*engineEngine)->CreateAudioPlayer(engineEngine, &pPlayer, &audioSrc, &audioSnk, 3,
                                       interfaceIds, interfaceRequired);
    (*pPlayer)->Realize(pPlayer, SL_BOOLEAN_FALSE);
    //获取播放器接口
    (*pPlayer)->GetInterface(pPlayer, SL_IID_PLAY, &pPlayItf);
    // 设置缓存队列和回调函数
    SLAndroidSimpleBufferQueueItf playerBufferQueue;
    (*pPlayer)->GetInterface(pPlayer, SL_IID_BUFFERQUEUE, &playerBufferQueue);
    // 每次回调 this 会被带给 playerCallback 里面的 context
    (*playerBufferQueue)->RegisterCallback(playerBufferQueue, playerCallback, this);
    // 设置播放状态
    (*pPlayItf)->SetPlayState(pPlayItf, SL_PLAYSTATE_PLAYING);
    //调用回调函数
    playerCallback(playerBufferQueue, this);
}

void DZAudio::play() {
    pthread_t readThread;
    pthread_create(&readThread, NULL, readRun, this);

    pthread_t writeThread;
    pthread_create(&writeThread, NULL, writeRun, this);

    pthread_detach(readThread);
    pthread_detach(writeThread);

}

DZAudio::~DZAudio() {
    release();
}

void DZAudio::startAudioTrack(JNIEnv *env) {
    jclass audioTrackClass = env->FindClass("android/media/AudioTrack");
    //创建播放器
    /**
    * (int streamType, int sampleRateInHz, int channelConfig, int audioFormat,
           int bufferSizeInBytes, int mode)
    */
    int sampleRateInHz = codecParameters->sample_rate;
    int channelConfig = (0x4 | 0x8);
    int audioFormat = 2;//ENCODING_PCM_16BIT
    int mode = 1;
    /**
    * getMinBufferSize(int sampleRateInHz, int channelConfig, int audioFormat)
    */
    jmethodID getMinBufferSizeMethodId = env->GetStaticMethodID(audioTrackClass, "getMinBufferSize",
                                                                "(III)I");
    int buffSize = env->CallStaticIntMethod(audioTrackClass, getMinBufferSizeMethodId,
                                            sampleRateInHz, channelConfig, audioFormat);
    //构造函数
    jmethodID audioTrackMethodId = env->GetMethodID(audioTrackClass, "<init>", "(IIIIII)V");
    jobject audioTrackObject = env->NewObject(audioTrackClass, audioTrackMethodId, 3,
                                              sampleRateInHz,
                                              channelConfig, audioFormat, buffSize, mode);
    this->audioTrackObject = env->NewGlobalRef(audioTrackObject);
    jmethodID playMethodId = env->GetMethodID(audioTrackClass, "play", "()V");
    env->CallVoidMethod(audioTrackObject, playMethodId);

    this->writeMethodId = env->GetMethodID(audioTrackClass, "write", "([BII)I");

    //释放资源
    env->DeleteLocalRef(audioTrackClass);

    while (true) {
        // AV_PACKET 压缩数据 -> AV_FRAME 解码后的数据
        //frame.data->java byte
        //大小 1s 44100点 2通道 每通道2字节
        //一帧不是1s frame->nb_samples

        int size = resampleAudio();
        jbyteArray audio_sample_array = env->NewByteArray(size);
        env->SetByteArrayRegion(audio_sample_array, 0, size,
                                reinterpret_cast<const jbyte *>(out_buffer));
        env->CallIntMethod(audioTrackObject, writeMethodId,
                           audio_sample_array, 0,
                           size);
        env->DeleteLocalRef(audio_sample_array);
    }


}
