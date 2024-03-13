//
// Created by huangjf on 2024/3/8.
//

#ifndef FIRSTPROJECT_DZVIDEO_H
#define FIRSTPROJECT_DZVIDEO_H

#include "DZJNICall.h"
#include "DZQueue.h"


class DZVideo {
public:
    JNIEnv *env = NULL;
    DZJNICall *dzjniCall = NULL;
    AVFormatContext *pFormatContext = NULL;
    int videoIndex;
    AVCodecContext *avCodecContext = NULL;
    DZQueue<AVFrame *> avFrame_queue;
    double timeBase = 0;
    long current = 0;
    long duration = 0;
public:
    DZVideo(DZJNICall *dzjniCall, JNIEnv *env, AVFormatContext *pFormatContext, int audioIndex,long duration);

    ~DZVideo();

    void prepare(ThreadMode threadMode);

    void callPlayError(ThreadMode threadMode, int errCode, char *msg);

    void play();

    void seekTo(jint position);

    void pause();

    void stop();

    void release();

    void read(AVPacket *pkt);

    void write();

};


#endif //FIRSTPROJECT_DZVIDEO_H
