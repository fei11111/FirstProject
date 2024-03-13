//
// Created by huangjf on 2024/2/29.
//

#ifndef FIRSTPROJECT_DZFFMPEG_H
#define FIRSTPROJECT_DZFFMPEG_H

#include "DZJNICall.h"
#include "DZAudio.h"
#include "DZConstDefine.h"
#include "pthread.h"
#include "DZVideo.h"

extern "C" {
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
}


//解析
class DZFFmpeg {
public:
    char *url = nullptr;
    DZJNICall *dzjniCall = nullptr;
    DZAudio *dzAudio = nullptr;
    DZVideo *dzVideo = nullptr;
    JNIEnv *jniEnv = nullptr;
    AVFormatContext *pFormatContext = nullptr;
    volatile PLAY_STATE state;
    long duration;
public:
    DZFFmpeg(const char *url, JNIEnv *env, DZJNICall *jniCall);

    void prepare();

    void prepareAsync();

    void prepare(ThreadMode threadMode);

    void play();

    void pause();

    void stop();

    void seekTo(jint position);

    void release();

    void callPlayerJniError(ThreadMode threadMode, int errCode, char *msg);

    ~DZFFmpeg();
};

#endif //FIRSTPROJECT_DZFFMPEG_H
