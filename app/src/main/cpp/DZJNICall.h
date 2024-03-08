//
// Created by huangjf on 2024/2/28.
//

#ifndef FIRSTPROJECT_DZJNICALL_H
#define FIRSTPROJECT_DZJNICALL_H

#include <jni.h>

enum ThreadMode {
    THREAD_CHILD, THREAD_MAIN
};

//回调到java层
class DZJNICall {
private:
    JNIEnv *env;
    jobject playObject;
    jmethodID errorMethodId;
    jmethodID preparedMethodId;
    jmethodID progressMethodId;
    jmethodID completedMethodId;
public:
    JavaVM *javaVm;
public:
    DZJNICall(JavaVM *javaVm, JNIEnv *env, jobject playObject);

    ~DZJNICall();

public:
    void callPlayerError(ThreadMode threadMode, int errorCode, char *msg);

    void callPlayerPrepared(ThreadMode threadMode);

    void callPlayProgress(ThreadMode threadMode,long current,long total);

    void callPlayCompleted(ThreadMode threadMode);
};

#endif //FIRSTPROJECT_DZJNICALL_H
