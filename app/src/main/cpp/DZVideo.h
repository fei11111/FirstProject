//
// Created by huangjf on 2024/3/8.
//

#ifndef FIRSTPROJECT_DZVIDEO_H
#define FIRSTPROJECT_DZVIDEO_H

#include "DZJNICall.h"
#include "DZQueue.h"
#include <android/native_window_jni.h>
#include <unistd.h>

extern "C" {
#include <libswresample/swresample.h>
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
}

//音频播放器类型，audioTrack或者OpenSLES
enum VIDEO_TYPE {
    TYPE_SURFACE
};

class DZVideo {
public:
    JNIEnv *env = NULL;
    DZJNICall *dzjniCall = NULL;
    AVFormatContext *pFormatContext = NULL;
    int videoIndex;
    AVCodecContext *avCodecContext = NULL;
    DZQueue<AVFrame *> avFrame_queue;
    SwsContext *swsContext = NULL;
    volatile PLAY_STATE *state;
    VIDEO_TYPE type;
    double timeBase = 0;
    long current = 0;
    long duration = 0;
    ANativeWindow *nativeWindow = NULL;
public:
    DZVideo(DZJNICall *dzjniCall, JNIEnv *env, AVFormatContext *pFormatContext, int audioIndex,
            volatile PLAY_STATE *state, long duration);

    ~DZVideo();

    void prepare(ThreadMode threadMode);

    void callPlayError(ThreadMode threadMode, int errCode, char *msg);

    void setSurfaceAndArea(jobject surface);

    void play();

    void seekTo(jint position);

    void pause();

    void stop();

    void release();

    int read(AVPacket *pkt);

    void write();

    void startSurface();
};


#endif //FIRSTPROJECT_DZVIDEO_H
