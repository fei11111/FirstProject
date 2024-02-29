//
// Created by huangjf on 2024/2/29.
//

#ifndef FIRSTPROJECT_DZAUDIO_H
#define FIRSTPROJECT_DZAUDIO_H

#include "DZJNICall.h"
#include "DZConstDefine.h"
#include "pthread.h"

extern "C" {
#include <libswresample/swresample.h>
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
}

//播放
class DZAudio {
private:
    JNIEnv *env;
    DZJNICall *dzjniCall;
    AVFormatContext *pFormatContext = NULL;
    int audioIndex;
    AVCodecParameters *codecParameters = NULL;
    AVCodecContext *avCodecContext = NULL;
    SwrContext *swrContext;
    jobject audioTrackObject;
    jmethodID writeMethodId;
    uint8_t *out_buffer;
public:
    DZAudio(DZJNICall *dzjniCall, JNIEnv *env, AVFormatContext *pFormatContext, int audioIndex);

    ~DZAudio();

    void prepare(ThreadMode threadMode);

    void callPlayError(ThreadMode threadMode, int errCode, char *msg);

    void play();

    void release();

    void startAudioTrack(ThreadMode threadMode);
};

#endif //FIRSTPROJECT_DZAUDIO_H
