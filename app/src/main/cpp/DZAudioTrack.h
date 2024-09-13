//
// Created by huangjf on 2024/3/4.
//

#ifndef FIRSTPROJECT_DZAUDIOTRACK_H
#define FIRSTPROJECT_DZAUDIOTRACK_H


#include "DZConstDefine.h"
#include <jni.h>

class DZAudioTrack {
private:
    bool isCallPlay = false;
    jobject audioTrackObject = NULL;
    jmethodID playMethodId;
    jmethodID writeMethodId;
    jmethodID pauseMethodId;
    jmethodID stopMethodId;
    jmethodID releaseMethodId;
public:
    DZAudioTrack(JNIEnv *env, int sampleRateInHz);
    ~DZAudioTrack();
    void play(JNIEnv *env,jbyteArray audio_sample_array,int size);
    void pause(JNIEnv *env);
    void stop(JNIEnv *env);
    void release(JNIEnv *env);
};


#endif //FIRSTPROJECT_DZAUDIOTRACK_H
