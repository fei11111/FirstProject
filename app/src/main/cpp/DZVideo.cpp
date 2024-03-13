//
// Created by huangjf on 2024/3/8.
//

#include "DZVideo.h"

DZVideo::DZVideo(DZJNICall *dzjniCall, JNIEnv *env, AVFormatContext *pFormatContext,
                 int videoIndex,long duration) {
    this->dzjniCall = dzjniCall;
    this->env = env;
    this->pFormatContext = pFormatContext;
    this->videoIndex = videoIndex;
    this->duration = duration;
}

void DZVideo::prepare(ThreadMode threadMode) {
    //查找解码器，AVCodecParameters不弄成成员变量，不用释放，avcodec_parameters_to_context会与avCodecContext联系，avCodecContext释放就好了
    AVCodecParameters *codecParameters = pFormatContext->streams[videoIndex]->codecpar;
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

    this->timeBase = av_q2d(pFormatContext->streams[videoIndex]->time_base);
}

//seek
void DZVideo::seekTo(jint position) {
    LOGE("position = %d", position);
    int64_t seek = position * AV_TIME_BASE;
    int res = av_seek_frame(pFormatContext, -1, seek, AVSEEK_FLAG_BACKWARD);
    if (res < 0) {
        callPlayError(THREAD_MAIN, SEEK_ERROR_CODE, av_err2str(res));
    }
    avcodec_flush_buffers(avCodecContext);
    avFrame_queue.clear();
    avFrame_queue.isExit = false;
}

//读
void DZVideo::read(AVPacket *pkt) {
    //pkt 是压缩的数据，需要解码成pcm数据
//    AVFrame *frame = av_frame_alloc();
//    int sendPacketRes = avcodec_send_packet(avCodecContext, pkt);
//    if (sendPacketRes == 0) {
//        int receiveFrameRes = avcodec_receive_frame(avCodecContext, frame);
//        if (receiveFrameRes == 0) {
//            avFrame_queue.push(frame);
//        }
//    }
}

//写
void DZVideo::write() {

}


//暂停
void DZVideo::pause() {

}

//停止
void DZVideo::stop() {

}

void DZVideo::play() {

}


void DZVideo::callPlayError(ThreadMode threadMode, int errCode, char *msg) {
    release();
    this->dzjniCall->callPlayerError(threadMode, errCode, msg);
}

void DZVideo::release() {

    if (avCodecContext != NULL) {
        LOGE("avCodecContext release");
        avcodec_close(avCodecContext);
        avcodec_free_context(&avCodecContext);
        avCodecContext = NULL;
    }

    LOGE("queue size = %d", avFrame_queue.size());
}

DZVideo::~DZVideo() {
    release();
}