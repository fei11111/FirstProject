//
// Created by huangjf on 2024/2/28.
//
#include "DZJNICall.h"
#include "DZConstDefine.h"

DZJNICall::DZJNICall(JavaVM *javaVm, JNIEnv *env, jobject jobject) {
    this->javaVm = javaVm;
    this->env = env;
    //JNI函数执行完后会释放，因此如果想保存，需要new出来
    this->playObject = this->env->NewGlobalRef(jobject);
    //通过对象获取类
    jclass objectClass = env->GetObjectClass(jobject);
    this->errorMethodId = env->GetMethodID(objectClass, "onError", "(ILjava/lang/String;)V");
    this->preparedMethodId = env->GetMethodID(objectClass, "onPrepared","()V");
    this->env->DeleteLocalRef(objectClass);
}

DZJNICall::~DZJNICall() {
    LOGE("DZJNICall 回收对象");
    this->env->DeleteGlobalRef(this->playObject);
}

void DZJNICall::callPlayerError(ThreadMode threadMode, int errorCode, char *msg) {
    // 子线程用不了主线程 jniEnv （native 线程）
    // 子线程是不共享 jniEnv ，他们有自己所独有的
    if (threadMode == THREAD_MAIN) {
        jstring jMsg = this->env->NewStringUTF(msg);
        this->env->CallVoidMethod(this->playObject, this->errorMethodId, errorCode, jMsg);
        this->env->ReleaseStringUTFChars(jMsg, msg);
        this->env->DeleteLocalRef(jMsg);
    } else {
        // 获取当前线程的 JNIEnv， 通过 JavaVM
        JNIEnv *env;
        if (this->javaVm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
            LOGE("get child thread jniEnv error");
        }
        jstring jMsg = env->NewStringUTF(msg);
        env->CallVoidMethod(this->playObject, this->errorMethodId, errorCode, jMsg);
        //这里不能release jMs和msg 会报错
        env->DeleteLocalRef(jMsg);

        this->javaVm->DetachCurrentThread();
    }
}

void DZJNICall::callPlayerPrepared(ThreadMode threadMode) {
    if (threadMode == THREAD_MAIN) {
        this->env->CallVoidMethod(this->playObject, this->preparedMethodId);
    } else {
        JNIEnv *env;
        if (this->javaVm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
            LOGE("get child thread jniEnv error");
        }
        env->CallVoidMethod(this->playObject, this->preparedMethodId);
        this->javaVm->DetachCurrentThread();
    }
}

