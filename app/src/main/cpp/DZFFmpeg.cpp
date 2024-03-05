//
// Created by huangjf on 2024/2/29.
//

#include "DZFFmpeg.h"

DZFFmpeg::DZFFmpeg(const char *url, JNIEnv *env, DZJNICall *jniCall) {
    int size = strlen(url) + 1;
    this->url = static_cast<char *>(malloc(size));
    memcpy(this->url, url, size);
    this->dzjniCall = jniCall;
    this->jniEnv = env;
}

void DZFFmpeg::callPlayerJniError(ThreadMode threadMode, int errCode, char *msg) {
    //释放资源
    release();
    this->dzjniCall->callPlayerError(threadMode, errCode, msg);
}

void DZFFmpeg::prepare() {
    prepare(THREAD_MAIN);
}

void *prepareRun(void *arg) {
    DZFFmpeg *dzfFmpeg = static_cast<DZFFmpeg *>(arg);
    dzfFmpeg->prepare(THREAD_CHILD);
    return 0;
}

void DZFFmpeg::prepareAsync() {
    pthread_t prepareThread;
    pthread_create(&prepareThread, NULL, prepareRun, this);
    pthread_detach(prepareThread);
}

void DZFFmpeg::prepare(ThreadMode threadMode) {
    //初始化网络
    avformat_network_init();

    //打开文件
    int formatOpenInputRes = avformat_open_input(&pFormatContext, url, NULL, NULL);
    if (formatOpenInputRes != 0) {
        //回调给java层
        callPlayerJniError(threadMode, OPEN_INPUT_ERROR_CODE,
                           av_err2str(formatOpenInputRes));
        return;
    }

    //获取流信息
    int formatStreamInfoRes = avformat_find_stream_info(pFormatContext, NULL);
    if (formatStreamInfoRes < 0) {
        callPlayerJniError(threadMode, FIND_STREAM_INFO_ERROR_CODE,
                           av_err2str(formatStreamInfoRes));
        return;
    }

    //查找音频流index
    int audioIndex = av_find_best_stream(pFormatContext, AVMediaType::AVMEDIA_TYPE_AUDIO, -1, -1,
                                         NULL,
                                         0);
    if (audioIndex < 0) {
        callPlayerJniError(threadMode, FIND_BEST_STREAM_ERROR_CODE,
                           av_err2str(audioIndex));
        return;
    }

    this->dzAudio = new DZAudio(this->dzjniCall, this->jniEnv, this->pFormatContext, audioIndex);
    dzAudio->prepare(threadMode);

    //已经准备好了
    dzjniCall->callPlayerPrepared(threadMode);
}


void DZFFmpeg::play() {
    if (dzAudio != NULL) {
        dzAudio->play();
    }
}

void DZFFmpeg::pause() {
    if (dzAudio != NULL) {
        dzAudio->pause();
    }
}

void DZFFmpeg::stop() {
    if (dzAudio != NULL) {
        dzAudio->stop();
    }
}

void DZFFmpeg::seekTo(jint position) {
    if (dzAudio != NULL) {
        dzAudio->seekTo(position);
    }
}

void DZFFmpeg::release() {

    LOGE("dzAudio release");
    if (dzAudio != NULL) {
        delete dzAudio;
        dzAudio = NULL;
    }

//    LOGE("pFormatContext release");
//    if (pFormatContext != NULL) {
//        avformat_close_input(&pFormatContext);
//        avformat_free_context(pFormatContext);
//        pFormatContext = NULL;
//    }

//    LOGE("url release");
//    if (url != NULL) {
//        free(url);
//        url = NULL;
//    }
//
//    LOGE("avformat_network_deinit release");
//    avformat_network_deinit();
}

DZFFmpeg::~DZFFmpeg() {
    release();
}
