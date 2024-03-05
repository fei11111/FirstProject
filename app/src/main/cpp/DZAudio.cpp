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
    this->state = INIT;
    //todo 切换不同播放器
    this->type = TYPE_SLES;
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

//暂停
void DZAudio::pause() {
    state = PAUSE;
    if (type == TYPE_AUDIO_TRACK) {
        dzAudioTrack->pause(env);
    } else {
        dzOpensles->pause();
    }
}

//停止
void DZAudio::stop() {
    state = STOP;
    if (type == TYPE_AUDIO_TRACK) {
        dzAudioTrack->stop(env);
    } else {
        dzOpensles->stop();
    }
}

//seek
void DZAudio::seekTo(jint position) {

}

//读
void *readRun(void *arg) {
    DZAudio *dzAudio = (DZAudio *) arg;
    AVPacket *pkt = av_packet_alloc();
    while (dzAudio->state != STOP) {
        if (dzAudio->state != PLAYING) {
            continue;
        }
        if (av_read_frame(dzAudio->pFormatContext, pkt) >= 0) {
            //pkt 是压缩的数据，需要解码成pcm数据
            if (pkt->stream_index == dzAudio->audioIndex) {
                //音频
                AVFrame *frame = av_frame_alloc();
                int sendPacketRes = avcodec_send_packet(dzAudio->avCodecContext, pkt);
                if (sendPacketRes == 0) {
                    int receiveFrameRes = avcodec_receive_frame(dzAudio->avCodecContext, frame);
                    if (receiveFrameRes == 0) {
                        dzAudio->avFrame_queue.push(frame);
                    }
                }
                //解引用
                av_packet_unref(pkt);
            } else {
                av_packet_unref(pkt);
            }
        }
    }

    //1.解引用 2.销毁pkt结构体数据 3.pkt = null
    LOGE("读停止");
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


void DZAudio::play() {
    if (state == INIT) {
        pthread_create(&readThread, NULL, readRun, this);
        pthread_create(&writeThread, NULL, writeRun, this);

        pthread_detach(readThread);
        pthread_detach(writeThread);

    } else if (dzOpensles != NULL) {
        //openSLES
        dzOpensles->play(this);
    }

    state = PLAYING;
}

//OpenSLES
void DZAudio::startSLES() {
    if (dzOpensles == NULL) {
        dzOpensles = new DZOpensles();
    }
    dzOpensles->play(this);
}

//AudioTrack
void DZAudio::startAudioTrack(JNIEnv *env) {
    if (dzAudioTrack == NULL) {
        this->dzAudioTrack = new DZAudioTrack(env, codecParameters->sample_rate);
    }
    while (state != STOP) {
        if (state != PLAYING) {
            continue;
        }
        // AV_PACKET 压缩数据 -> AV_FRAME 解码后的数据
        //frame.data->java byte
        //大小 1s 44100点 2通道 每通道2字节
        //一帧不是1s frame->nb_samples
        int size = resampleAudio();
        jbyteArray audio_sample_array = env->NewByteArray(size);
        env->SetByteArrayRegion(audio_sample_array, 0, size,
                                reinterpret_cast<const jbyte *>(out_buffer));
        this->dzAudioTrack->play(env, audio_sample_array, size);
        env->DeleteLocalRef(audio_sample_array);
    }
    LOGE("write 停止");
}

void DZAudio::callPlayError(ThreadMode threadMode, int errCode, char *msg) {
    release();
    this->dzjniCall->callPlayerError(threadMode, errCode, msg);
}

void DZAudio::release() {

    state = STOP;

    LOGE("avFrame_queue release");
    while (!avFrame_queue.isEmpty()) {
        AVFrame *frame = avFrame_queue.pop();
        if (frame != NULL) {
            av_frame_unref(frame);
            av_frame_free(&frame);
        }
    }

    LOGE("dzAudioTrack release");
    if (dzAudioTrack != NULL) {
        dzAudioTrack->stop(env);
        dzAudioTrack->release(env);
        free(dzAudioTrack);
        dzAudioTrack = NULL;
    }

    LOGE("dzOpensles release");
    if (dzOpensles != NULL) {
        dzOpensles->stop();
        dzOpensles->release();
        free(dzOpensles);
        dzOpensles = NULL;
    }



    LOGE("out_buffer release");
    if (out_buffer != NULL) {
        av_free(out_buffer);
        out_buffer = NULL;
    }

    LOGE("swrContext release");
    if (swrContext != NULL) {
        swr_close(swrContext);
        swr_free(&swrContext);
        swrContext = NULL;
    }

    LOGE("avCodecContext release");
    if (avCodecContext != NULL) {
        avcodec_close(avCodecContext);
        avcodec_free_context(&avCodecContext);
        avCodecContext = NULL;
    }

    LOGE("codecParameters release");
    if (codecParameters != NULL) {
        avcodec_parameters_free(&codecParameters);
        codecParameters = NULL;
    }
}

DZAudio::~DZAudio() {
    release();
}

