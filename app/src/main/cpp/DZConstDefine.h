//
// Created by huangjf on 2024/2/27.
//

#ifndef FIRSTPROJECT_DZCONSTDEFINE_H
#define FIRSTPROJECT_DZCONSTDEFINE_H

#include "../../../../../../android sdk/ndk/26.1.10909125/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include/android/log.h"

#define TAG "JNI_TAG"
#define LOGE(...)     __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__) // ANDROID_LOG_DEBUG为日志等级，tag为标识符，message为要打印的内容
#define AUDIO_SAMPLE_RATE 44100

// ---------- 播放错误码 start ----------
#define OPEN_INPUT_ERROR_CODE -0x01
#define FIND_STREAM_INFO_ERROR_CODE -0x02
#define FIND_BEST_STREAM_ERROR_CODE -0x03
#define FIND_STREAM_ERROR_CODE -0x10
#define CODEC_FIND_DECODER_ERROR_CODE -0x11
#define CODEC_ALLOC_CONTEXT_ERROR_CODE -0x12
#define AVCODEC_PARAMETERS_TO_CONTEXT_ERROR_CODE -0x13
#define AVCODEC_OPEN_ERROR_CODE -0x14
#define SWR_ALLOC_SET_OPTS_ERROR_CODE -0x15
#define SWR_CONTEXT_INIT_ERROR_CODE -0x16
#define SEEK_ERROR_CODE -0x17
// ---------- 播放错误码 end ----------

#endif //FIRSTPROJECT_DZCONSTDEFINE_H
