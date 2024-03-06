//
// Created by huangjf on 2024/3/4.
//

#include "DZAudioTrack.h"

DZAudioTrack::DZAudioTrack(JNIEnv *env, int sampleRateInHz) {
    jclass audioTrackClass = env->FindClass("android/media/AudioTrack");
    //创建播放器
    /**
    * (int streamType, int sampleRateInHz, int channelConfig, int audioFormat,
           int bufferSizeInBytes, int mode)
    */
    int channelConfig = (0x4 | 0x8);
    int audioFormat = 2;//ENCODING_PCM_16BIT
    int mode = 1;
    /**
    * getMinBufferSize(int sampleRateInHz, int channelConfig, int audioFormat)
    */
    jmethodID getMinBufferSizeMethodId = env->GetStaticMethodID(audioTrackClass, "getMinBufferSize",
                                                                "(III)I");
    int buffSize = env->CallStaticIntMethod(audioTrackClass, getMinBufferSizeMethodId,
                                            sampleRateInHz, channelConfig, audioFormat);
    //构造函数
    jmethodID audioTrackMethodId = env->GetMethodID(audioTrackClass, "<init>", "(IIIIII)V");
    jobject audioTrackObject = env->NewObject(audioTrackClass, audioTrackMethodId, 3,
                                              sampleRateInHz,
                                              channelConfig, audioFormat, buffSize, mode);
    this->audioTrackObject = env->NewGlobalRef(audioTrackObject);
    this->playMethodId = env->GetMethodID(audioTrackClass, "play", "()V");
    this->stopMethodId = env->GetMethodID(audioTrackClass, "stop", "()V");
    this->pauseMethodId = env->GetMethodID(audioTrackClass, "pause", "()V");
    this->releaseMethodId = env->GetMethodID(audioTrackClass, "release", "()V");
    this->writeMethodId = env->GetMethodID(audioTrackClass, "write", "([BII)I");
    //释放资源
    env->DeleteLocalRef(audioTrackClass);
}

DZAudioTrack::~DZAudioTrack() {

}

void DZAudioTrack::play(JNIEnv *env, jbyteArray audio_sample_array, int size) {
    if (!this->isCallPlay) {
        env->CallVoidMethod(audioTrackObject, playMethodId);
    }
    env->CallIntMethod(audioTrackObject, writeMethodId,
                       audio_sample_array, 0,
                       size);
    this->isCallPlay = true;
}

void DZAudioTrack::pause(JNIEnv *env) {
    env->CallVoidMethod(audioTrackObject, pauseMethodId);
    this->isCallPlay = false;
}

void DZAudioTrack::stop(JNIEnv *env) {
    isCallPlay = false;
    env->CallVoidMethod(audioTrackObject, stopMethodId);
    this->isCallPlay = false;
}

void DZAudioTrack::release(JNIEnv *env) {
    env->CallVoidMethod(audioTrackObject, releaseMethodId);
    if (audioTrackObject != NULL) {
        env->DeleteGlobalRef(this->audioTrackObject);
        audioTrackObject = NULL;
    }

}
