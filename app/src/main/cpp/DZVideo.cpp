//
// Created by huangjf on 2024/3/8.
//

#include "DZVideo.h"


DZVideo::DZVideo(DZJNICall *dzjniCall, JNIEnv *env, AVFormatContext *pFormatContext,
                 int videoIndex, volatile PLAY_STATE *state, long duration) {
    this->dzjniCall = dzjniCall;
    this->env = env;
    this->pFormatContext = pFormatContext;
    this->videoIndex = videoIndex;
    this->state = state;
    this->duration = duration;

    //切换视频播放类型
    type = TYPE_SURFACE;
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

    //回传宽高
    dzjniCall->callPlaySizeChange(threadMode, avCodecContext->width, avCodecContext->height);

    // 格式转换关键类
    // 首先这是mp4 如果需要解析成 yuv 需要用到 SwsContext
    // 构造函数传入的参数为 原视频的宽高、像素格式、目标的宽高这里也取原视频的宽高（可以修改参数）
    //todo
//    swsContext = sws_getContext(avCodecContext->width, avCodecContext->height,
//                                avCodecContext->pix_fmt,
//                                1080, 607,
//                                AV_PIX_FMT_RGBA, SWS_BICUBIC, NULL, NULL, NULL);
    swsContext = sws_getContext(avCodecContext->width, avCodecContext->height,
                                avCodecContext->pix_fmt,
                                avCodecContext->width, avCodecContext->height,
                                AV_PIX_FMT_RGBA, SWS_BICUBIC, NULL, NULL, NULL);
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
int DZVideo::read(AVPacket *pkt) {
    int sendPacketRes = avcodec_send_packet(avCodecContext, pkt);
    // 因为avcodec_send_packet和avcodec_receive_frame并不是一对一的关系的
    if (sendPacketRes == 0) {
        for (;;) {
            AVFrame *frame = av_frame_alloc();
            int receiveFrameRes = avcodec_receive_frame(avCodecContext, frame);
            if (receiveFrameRes != 0) {
                av_frame_unref(frame);
                av_frame_free(&frame);
                break;
            } else {
                avFrame_queue.push(frame);
            }
        }
    }
    return sendPacketRes;
}

//写
void DZVideo::write() {
    if (type == TYPE_SURFACE) {
        startSurface();
    } else {

    }
}


//暂停
void DZVideo::pause() {

}

//停止
void DZVideo::stop() {

}

void DZVideo::play() {

}

void DZVideo::setSurfaceAndArea(jobject surface) {
    if (surface != NULL && nativeWindow == NULL) {
        //todo
        nativeWindow = ANativeWindow_fromSurface(env, surface);
    }
}

void DZVideo::startSurface() {
    //1080, 607,
//    char *rgb = new char[avCodecContext->width * avCodecContext->height * 8];
//    ANativeWindow_Buffer wbuf;
    ANativeWindow_setBuffersGeometry(nativeWindow, avCodecContext->width, avCodecContext->height, WINDOW_FORMAT_RGBA_8888);
    AVFrame* pFrameYUV = av_frame_alloc();
    while (*state != STOP) {
        if (*state != PLAYING) {
            //pause 状态
            continue;
        }
        if (nativeWindow == NULL) continue;


        AVFrame *frame = avFrame_queue.pop();

        ANativeWindow_Buffer buffer;
        ANativeWindow_lock(nativeWindow, &buffer, NULL);

        // 将h264的格式转化成rgb
        // 从srcFrame中的数据（h264）解析成rgb存放到dstFrame中去
        sws_scale(swsContext, frame->data, frame->linesize, 0, frame->height, pFrameYUV->data,
                  pFrameYUV->linesize);
        // 把数据拷贝到 window_buffer 中

        auto *data_src_line = (int32_t *) pFrameYUV->data[0];
        const auto src_line_stride = pFrameYUV->linesize[0] / sizeof(int32_t);

        auto *data_dst_line = (uint32_t *) buffer.bits;

        for (int y = 0; y < buffer.height; y++) {
            std::copy_n(data_src_line, buffer.width, data_dst_line);
            data_src_line += src_line_stride;
            data_dst_line += buffer.stride;
        }

        ANativeWindow_unlockAndPost(nativeWindow);

//        //todo
//        swsContext = sws_getCachedContext(swsContext,
//                                          frame->width,
//                                          frame->height,
//                                          (AVPixelFormat) frame->format,
//                                          avCodecContext->width,
//                                          avCodecContext->height,
//                                          AV_PIX_FMT_RGBA,
//                                          SWS_FAST_BILINEAR,
//                                          0, 0, 0
//        );
//        uint8_t *data[AV_NUM_DATA_POINTERS] = {0};
//        data[0] = (uint8_t *) rgb;
//        int lines[AV_NUM_DATA_POINTERS] = {0};
//        lines[0] = avCodecContext->width * 4;
//        int h = sws_scale(swsContext,
//                          (const uint8_t **) frame->data,
//                          frame->linesize, 0,
//                          frame->height,
//                          data, lines);
//        LOGE("sws_scale = %d", h);
//        if (h > 0) {
//            // 绘制
//            ANativeWindow_lock(nativeWindow, &wbuf, 0);
//            uint8_t *dst = (uint8_t *) wbuf.bits;
//            memcpy(dst, rgb, avCodecContext->width * avCodecContext->height * 4);
//            ANativeWindow_unlockAndPost(nativeWindow);
//        }
    }
    av_frame_unref(pFrameYUV);
    av_frame_free(&pFrameYUV);
    LOGE("video 写停止");
}

void DZVideo::release() {

    LOGE("avFrame_queue release");
    avFrame_queue.clear();

    if (nativeWindow != NULL) {
        ANativeWindow_release(nativeWindow);
    }

    if (swsContext != NULL) {
        LOGE("swsContext release");
        sws_freeContext(swsContext);
        swsContext = NULL;
    }

    if (avCodecContext != NULL) {
        LOGE("avCodecContext release");
        avcodec_close(avCodecContext);
        avcodec_free_context(&avCodecContext);
        avCodecContext = NULL;
    }


    LOGE("queue size = %d", avFrame_queue.size());
}

void DZVideo::callPlayError(ThreadMode threadMode, int errCode, char *msg) {
    release();
    this->dzjniCall->callPlayerError(threadMode, errCode, msg);
}

DZVideo::~DZVideo() {
    release();
}


