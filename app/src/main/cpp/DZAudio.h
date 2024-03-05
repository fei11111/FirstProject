//
// Created by huangjf on 2024/2/29.
//

#ifndef FIRSTPROJECT_DZAUDIO_H
#define FIRSTPROJECT_DZAUDIO_H

#include "DZJNICall.h"
#include "pthread.h"
#include "DZQueue.h"
#include "DZAudioTrack.h"
#include "DZOpensles.h"

enum PLAY_STATE {
    INIT,
    PLAYING,
    PAUSE,
    STOP
};

enum PLAY_TYPE {
    TYPE_AUDIO_TRACK,
    TYPE_SLES
};

extern "C" {
#include <libswresample/swresample.h>
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
}

//播放
class DZAudio {
public:
    JNIEnv *env;
    DZJNICall *dzjniCall;
    AVFormatContext *pFormatContext = NULL;
    int audioIndex;
    AVCodecParameters *codecParameters = NULL;
    AVCodecContext *avCodecContext = NULL;
    SwrContext *swrContext;
    uint8_t *out_buffer;
    DZAudioTrack *dzAudioTrack = NULL;
    DZOpensles *dzOpensles = NULL;
    DZQueue<AVFrame *> avFrame_queue;
    pthread_t readThread = NULL;
    pthread_t writeThread = NULL;
    PLAY_TYPE type;
    PLAY_STATE state;
public:
    DZAudio(DZJNICall *dzjniCall, JNIEnv *env, AVFormatContext *pFormatContext, int audioIndex);

    ~DZAudio();

    void prepare(ThreadMode threadMode);

    void callPlayError(ThreadMode threadMode, int errCode, char *msg);

    void play();

    void pause();

    void stop();

    void seekTo(jint position);

    void release();

    void startAudioTrack(JNIEnv *env);

    void startSLES();

    int resampleAudio();
};


#endif //FIRSTPROJECT_DZAUDIO_H
