#include <jni.h>
#include <sched.h>

extern "C"
JNIEXPORT jint JNICALL
Java_com_fei_action_optimize_startup_StartUpOpActivity_bindMaxFreqCore(JNIEnv *env, jobject thiz,
                                                                       jint max_freq_cpu_index,
                                                                       jint pid) {
    cpu_set_t mask;
    // 将 mask 置空
    CPU_ZERO(&mask);
    // 将需要绑定的 CPU 核设置给 mask，核为序列 0、1、2、3...
    CPU_SET(max_freq_cpu_index, &mask);
    return sched_setaffinity(pid, sizeof(mask), &mask);
}