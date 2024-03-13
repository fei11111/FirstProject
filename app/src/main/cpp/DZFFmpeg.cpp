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
    this->state = INIT;
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
    int videoIndex = av_find_best_stream(pFormatContext, AVMediaType::AVMEDIA_TYPE_VIDEO, -1, -1,
                                         NULL,
                                         0);

    if (audioIndex < 0) {
        callPlayerJniError(threadMode, FIND_BEST_STREAM_AUDIO_ERROR_CODE,
                           av_err2str(audioIndex));
        return;
    }

    if (videoIndex < 0) {
        callPlayerJniError(threadMode, FIND_BEST_STREAM_VIDEO_ERROR_CODE,
                           av_err2str(videoIndex));
        return;
    }

    duration = pFormatContext->duration / AV_TIME_BASE;//算出时长

    this->dzAudio = new DZAudio(this->dzjniCall, this->jniEnv, this->pFormatContext, audioIndex,
                                &state, duration);
    dzAudio->prepare(threadMode);

    this->dzVideo = new DZVideo(this->dzjniCall, this->jniEnv, this->pFormatContext, videoIndex,
                                duration);
    dzVideo->prepare(threadMode);

    //已经准备好了
    dzjniCall->callPlayerPrepared(threadMode);
}


//读
void *readRun(void *arg) {
    DZFFmpeg *dzfFmpeg = (DZFFmpeg *) arg;
    AVPacket *pkt = av_packet_alloc();
    while (dzfFmpeg->state != STOP) {
        if (dzfFmpeg->state != PLAYING) {
            continue;
        }
        if (av_read_frame(dzfFmpeg->pFormatContext, pkt) >= 0) {
            //pkt 是压缩的数据
            if (pkt->stream_index == dzfFmpeg->dzAudio->audioIndex) {
                //音频，需要解码成pcm数据
                dzfFmpeg->dzAudio->read(pkt);
            } else if (pkt->stream_index == dzfFmpeg->dzVideo->videoIndex) {
                //视频 yuv-> rgb
                dzfFmpeg->dzVideo->read(pkt);
            }
            //解引用
            av_packet_unref(pkt);
        } else {
            //视频
            av_packet_unref(pkt);
        }
    }
    //1.解引用 2.销毁pkt结构体数据 3.pkt = null
    LOGE("读停止");
    av_packet_free(&pkt);
    return 0;
}


//写
void *writeRun(void *arg) {
    DZFFmpeg *dzfFmpeg = (DZFFmpeg *) arg;
    dzfFmpeg->dzAudio->write();
    dzfFmpeg->dzVideo->write();
    return 0;
}


void DZFFmpeg::play() {
    if (state == INIT) {

        pthread_t readThread = NULL;
        pthread_t writeThread = NULL;

        pthread_create(&readThread, NULL, readRun, this);
        pthread_create(&writeThread, NULL, writeRun, this);

        pthread_detach(readThread);
        pthread_detach(writeThread);

    }
    state = PLAYING;
    if (dzAudio != NULL) {
        dzAudio->play();
    }

    if (dzVideo != NULL) {
        dzVideo->play();
    }
}

void DZFFmpeg::pause() {
    if (dzAudio != NULL) {
        dzAudio->pause();
        state = PAUSE;
    }
    if (dzVideo != NULL) {
        dzVideo->pause();
    }
}

void DZFFmpeg::stop() {
    if (dzAudio != NULL) {
        dzAudio->stop();
        state = STOP;
    }

    if (dzVideo != NULL) {
        dzVideo->stop();
    }
}

void DZFFmpeg::seekTo(jint position) {
    if (dzAudio != NULL) {
        dzAudio->seekTo(position);
    }

    if (dzVideo != NULL) {
        dzVideo->seekTo(position);
    }
}

void DZFFmpeg::release() {

    state = STOP;

    if (dzAudio != NULL) {
        LOGE("dzAudio release");
        delete dzAudio;
        dzAudio = NULL;
    }

    if (dzVideo != NULL) {
        LOGE("dzVideo release");
        delete dzVideo;
        dzVideo = NULL;
    }

    if (pFormatContext != NULL) {
        LOGE("pFormatContext release");
        avformat_close_input(&pFormatContext);
        avformat_free_context(pFormatContext);
        pFormatContext = NULL;
    }

    LOGE("url release");
    if (url != NULL) {
        free(url);
        url = NULL;
    }

    LOGE("avformat_network_deinit release");
    avformat_network_deinit();
}

DZFFmpeg::~DZFFmpeg() {
    release();
}
