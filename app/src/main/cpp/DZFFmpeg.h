//
// Created by huangjf on 2024/2/29.
//

#ifndef FIRSTPROJECT_DZFFMPEG_H
#define FIRSTPROJECT_DZFFMPEG_H

#include "DZJNICall.h"
#include "DZAudio.h"
#include "DZConstDefine.h"
#include "pthread.h"

extern "C" {
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
}


//解析
class DZFFmpeg {
private:
    char *url;
    DZJNICall *dzjniCall = NULL;
    DZAudio *dzAudio = NULL;
    JNIEnv *jniEnv;
    AVFormatContext *pFormatContext = NULL;

public:
    DZFFmpeg(const char *url, JNIEnv *env,DZJNICall *jniCall);

    void prepare();

    void prepareAsync();

    void prepare(ThreadMode threadMode);

    void play();

    void release();

    void callPlayerJniError(ThreadMode threadMode,int errCode,char* msg);

    ~DZFFmpeg();
};

#endif //FIRSTPROJECT_DZFFMPEG_H
