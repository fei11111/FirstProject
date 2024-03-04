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

    startAudioTrack(threadMode);
}

void DZAudio::release() {
    if (audioTrackObject != NULL) {
        env->DeleteGlobalRef(this->audioTrackObject);
    }
    if (codecParameters != NULL) {
        avcodec_parameters_free(&codecParameters);
    }
    if (avCodecContext != NULL) {
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

    while (true) {
        AVFrame *frame = dzAudio->avFrame_queue.pop();
        // AV_PACKET 压缩数据 -> AV_FRAME 解码后的数据
        //frame.data->java byte
        //大小 1s 44100点 2通道 每通道2字节
        //一帧不是1s frame->nb_samples
        swr_convert(dzAudio->swrContext, &dzAudio->out_buffer, AUDIO_SAMPLE_RATE * 2,
                    (const uint8_t **) (frame->data),
                    frame->nb_samples);
        int size = av_samples_get_buffer_size(NULL,
                                              dzAudio->codecParameters->ch_layout.nb_channels,
                                              frame->nb_samples,
                                              AV_SAMPLE_FMT_S16, 1);
        jbyteArray audio_sample_array = env->NewByteArray(size);
        env->SetByteArrayRegion(audio_sample_array, 0, size,
                                reinterpret_cast<const jbyte *>(dzAudio->out_buffer));
        env->CallIntMethod(dzAudio->audioTrackObject, dzAudio->writeMethodId,
                           audio_sample_array, 0,
                           size);
        env->DeleteLocalRef(audio_sample_array);

        av_frame_unref(frame);
        av_frame_free(&frame);
    }


    dzAudio->dzjniCall->javaVm->DetachCurrentThread();
    return 0;
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

void DZAudio::startAudioTrack(ThreadMode threadMode) {
    JNIEnv *env;
    if (threadMode == THREAD_CHILD) {
        if (this->dzjniCall->javaVm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
            LOGE("get child thread jniEnv error");
        }
    } else {
        env = this->env;
    }
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

    if (threadMode == THREAD_CHILD) {
        this->dzjniCall->javaVm->DetachCurrentThread();
    }

}
