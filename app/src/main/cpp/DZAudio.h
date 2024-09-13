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

//音频播放器类型，audioTrack或者OpenSLES
enum AUDIO_TYPE {
    TYPE_AUDIO_TRACK,
    TYPE_SLES
};

extern "C" {
#include <libswresample/swresample.h>
}

//音频播放
class DZAudio {
public:
    JNIEnv *env = NULL;
    DZJNICall *dzjniCall = NULL;
    AVFormatContext *pFormatContext = NULL;
    int audioIndex;
    AVCodecContext *avCodecContext = NULL;
    SwrContext *swrContext = NULL;
    uint8_t *out_buffer = NULL;
    DZAudioTrack *dzAudioTrack = NULL;
    DZOpensles *dzOpensles = NULL;
    volatile PLAY_STATE *state;
    double timeBase = 0;
    long current = 0;
    long duration = 0;
private:
    DZQueue<AVFrame *> avFrame_queue;
    AUDIO_TYPE type;
public:
    DZAudio(DZJNICall *dzjniCall, JNIEnv *env, AVFormatContext *pFormatContext, int audioIndex,
            volatile PLAY_STATE *state, long duration);

    ~DZAudio();

    void prepare(ThreadMode threadMode);

    void callPlayError(ThreadMode threadMode, int errCode, char *msg);

    void play();

    void pause();

    void stop();

    void seekTo(jint position);

    void release();

    int read(AVPacket *pkt);

    void write();

    void startAudioTrack(JNIEnv *env);

    void startSLES();

    int resampleAudio();
};


#endif //FIRSTPROJECT_DZAUDIO_H
