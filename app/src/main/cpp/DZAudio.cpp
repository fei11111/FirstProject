//
// Created by huangjf on 2024/2/29.
//

#include "DZAudio.h"

double timeBase;
long current = 0;
long duration = 0;
timer_t timerid = NULL;

DZAudio::DZAudio(DZJNICall *dzjniCall, JNIEnv *env, AVFormatContext *pFormatContext,
                 int audioIndex) {
    this->dzjniCall = dzjniCall;
    this->env = env;
    this->pFormatContext = pFormatContext;
    this->audioIndex = audioIndex;
    this->state = INIT;
    //todo 切换不同播放器
    this->type = TYPE_SLES;
}

void DZAudio::prepare(ThreadMode threadMode) {
    //查找解码器，AVCodecParameters不弄成成员变量，不用释放，avcodec_parameters_to_context会与avCodecContext联系，avCodecContext释放就好了
    AVCodecParameters *codecParameters = pFormatContext->streams[audioIndex]->codecpar;
    const AVCodec *avCodec = avcodec_find_decoder(codecParameters->codec_id);
    if (avCodec == NULL) {
        LOGE("find decoder error");
        callPlayError(threadMode, CODEC_FIND_DECODER_ERROR_CODE, "find decoder error");
        return;
    }

    //打开解码器
    //解码器上下文
    avCodecContext = avcodec_alloc_context3(avCodec);
    if (avCodecContext == NULL) {
        LOGE("alloc avcodec context3 error");
        callPlayError(threadMode, CODEC_ALLOC_CONTEXT_ERROR_CODE, "alloc avcodec context3 error");
        return;
    }
    //设置对应的参数
    int avCodecParamterContextRes = avcodec_parameters_to_context(avCodecContext, codecParameters);
    if (avCodecParamterContextRes < 0) {
        LOGE("avcodec parameters to context error");
        callPlayError(threadMode, AVCODEC_PARAMETERS_TO_CONTEXT_ERROR_CODE,
                      "avcodec parameters to context error");
        return;
    }
    int avcodecOpenRes = avcodec_open2(avCodecContext, avCodec, NULL);
    if (avcodecOpenRes != 0) {
        callPlayError(threadMode, AVCODEC_OPEN_ERROR_CODE, "avcoder open error");
        return;
    }

    // ---------- 重采样 start ----------
    //转换器上下文
    this->swrContext = swr_alloc();
    enum AVSampleFormat out_formart = AV_SAMPLE_FMT_S16;
    int out_sample_rate = avCodecContext->sample_rate;
    swr_alloc_set_opts2(&swrContext, &avCodecContext->ch_layout, out_formart,
                        out_sample_rate, &avCodecContext->ch_layout,
                        avCodecContext->sample_fmt, avCodecContext->sample_rate, 0, NULL);

    swr_init(swrContext);
    this->out_buffer = (uint8_t *) av_malloc(AUDIO_SAMPLE_RATE * 2);
    // ---------- 重采样 end ----------

    timeBase = av_q2d(pFormatContext->streams[audioIndex]->time_base);
    duration = pFormatContext->duration / AV_TIME_BASE;//算出时长
    dzjniCall->callPlayProgress(threadMode, current, duration);
}

//暂停
void DZAudio::pause() {
    state = PAUSE;
    if (type == TYPE_AUDIO_TRACK) {
        dzAudioTrack->pause(env);
    } else {
        dzOpensles->pause();
    }
}

//停止
void DZAudio::stop() {
    state = STOP;
    if (type == TYPE_AUDIO_TRACK) {
        dzAudioTrack->stop(env);
    } else {
        dzOpensles->stop();
    }
}

//seek
void DZAudio::seekTo(jint position) {

}

//读
void *readRun(void *arg) {
    DZAudio *dzAudio = (DZAudio *) arg;
    AVPacket *pkt = av_packet_alloc();
    while (dzAudio->state != STOP) {
        if (dzAudio->state != PLAYING) {
            continue;
        }
        if (av_read_frame(dzAudio->pFormatContext, pkt) >= 0) {
            //pkt 是压缩的数据，需要解码成pcm数据
            if (pkt->stream_index == dzAudio->audioIndex) {
                //音频
                AVFrame *frame = av_frame_alloc();
                int sendPacketRes = avcodec_send_packet(dzAudio->avCodecContext, pkt);
                if (sendPacketRes == 0) {
                    int receiveFrameRes = avcodec_receive_frame(dzAudio->avCodecContext, frame);
                    if (receiveFrameRes == 0) {
                        dzAudio->avFrame_queue.push(frame);
                    }
                }
                //解引用
                av_packet_unref(pkt);
            } else {
                av_packet_unref(pkt);
            }
        }
    }

    //1.解引用 2.销毁pkt结构体数据 3.pkt = null
    LOGE("读停止");
    av_packet_free(&pkt);
    return 0;
}

//写
void *writeRun(void *arg) {
    DZAudio *dzAudio = (DZAudio *) arg;
    JNIEnv *env;
    if (dzAudio->dzjniCall->javaVm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
        LOGE("get child thread jniEnv error");
    }

    if (dzAudio->type == TYPE_AUDIO_TRACK) {
        //audioTrack
        dzAudio->startAudioTrack(env);
    } else {
        dzAudio->startSLES();
    }

    dzAudio->dzjniCall->javaVm->DetachCurrentThread();
    return 0;
}

//获取size
int DZAudio::resampleAudio() {
    if (AVFrame *frame = avFrame_queue.pop()) {
        // AV_PACKET 压缩数据 -> AV_FRAME 解码后的数据
        //frame.data->java byte
        //大小 1s 44100点 2通道 每通道2字节
        //一帧不是1s frame->nb_samples
        swr_convert(swrContext, &out_buffer, frame->nb_samples,
                    (const uint8_t **) (frame->data),
                    frame->nb_samples);
        int size = av_samples_get_buffer_size(NULL,
                                              avCodecContext->ch_layout.nb_channels,
                                              frame->nb_samples,
                                              AV_SAMPLE_FMT_S16, 1);
        current = frame->pts * timeBase;

        // 解引用
        av_frame_unref(frame);
        av_frame_free(&frame);
        return size;
    }
    return -1;
}

//进度回调
void timer_thread(union sigval v) {
    DZAudio *dzAudio = (DZAudio *) v.sival_ptr;
    if (dzAudio->state == PLAYING) {
        //回调进度
        dzAudio->dzjniCall->callPlayProgress(THREAD_CHILD, current, duration);
        if (current == duration) {
            LOGE("读取完成");
            timer_delete(timerid);
            timerid = NULL;
            dzAudio->dzjniCall->callPlayCompleted(THREAD_CHILD);
            dzAudio->state = COMPLETE;
        }
    }
}


void *progressRun(void *arg) {

    struct sigevent evp;
    memset(&evp, 0, sizeof(struct sigevent));       //清零

    evp.sigev_value.sival_ptr = arg;
    evp.sigev_notify = SIGEV_THREAD;                //线程通知的方式，创建新线程
    evp.sigev_notify_function = timer_thread;       //线程函数名，当定时器到时后调用该函数

    if (timer_create(CLOCK_REALTIME, &evp, &timerid) == -1) {
        LOGE("fail to timer_create");
    }

    // 启动后等待it.it_value时间后第一次调用,然后每次间隔it.it_interval时调用
    struct itimerspec it;
    it.it_interval.tv_sec = 1;  // timer函数执行频率为2s运行1次
    it.it_interval.tv_nsec = 0;
    it.it_value.tv_sec = 0;     // 倒计时4秒开始调用timer函数
    it.it_value.tv_nsec = 1000000;

    if (timer_settime(timerid, 0, &it, NULL) == -1) {
        LOGE("fail to timer_settime");
    }
    return 0;
}

void DZAudio::play() {
    if (state == INIT) {

        pthread_t readThread = NULL;
        pthread_t writeThread = NULL;
        pthread_t timerThread = NULL;

        pthread_create(&readThread, NULL, readRun, this);
        pthread_create(&writeThread, NULL, writeRun, this);
        pthread_create(&timerThread, NULL, progressRun, this);

        pthread_detach(readThread);
        pthread_detach(writeThread);
        pthread_detach(timerThread);

    } else if (dzOpensles != NULL) {
        //openSLES
        dzOpensles->play(this);
    }

    state = PLAYING;
}

//OpenSLES
void DZAudio::startSLES() {
    if (dzOpensles == NULL) {
        dzOpensles = new DZOpensles();
    }
    dzOpensles->play(this);
}

//AudioTrack
void DZAudio::startAudioTrack(JNIEnv *env) {
    if (dzAudioTrack == NULL) {
        this->dzAudioTrack = new DZAudioTrack(env, avCodecContext->sample_rate);
    }
    while (state != STOP) {
        if (state != PLAYING) {
            continue;
        }
        // AV_PACKET 压缩数据 -> AV_FRAME 解码后的数据
        //frame.data->java byte
        //大小 1s 44100点 2通道 每通道2字节
        //一帧不是1s frame->nb_samples
        int size = resampleAudio();
        if (size == -1) break;
        jbyteArray audio_sample_array = env->NewByteArray(size);
        env->SetByteArrayRegion(audio_sample_array, 0, size,
                                reinterpret_cast<const jbyte *>(out_buffer));
        this->dzAudioTrack->play(env, audio_sample_array, size);
        env->DeleteLocalRef(audio_sample_array);
    }
    LOGE("写停止");
}

void DZAudio::callPlayError(ThreadMode threadMode, int errCode, char *msg) {
    release();
    this->dzjniCall->callPlayerError(threadMode, errCode, msg);
}

void DZAudio::release() {
    state = STOP;

    if (dzAudioTrack != NULL) {
        LOGE("dzAudioTrack release");
        dzAudioTrack->stop(env);
        dzAudioTrack->release(env);
        delete dzAudioTrack;
        dzAudioTrack = NULL;
    }

    if (dzOpensles != NULL) {
        LOGE("dzOpensles release");
        dzOpensles->stop();
        delete dzOpensles;
        dzOpensles = NULL;
    }

    LOGE("avFrame_queue release");
    avFrame_queue.clear();

    if (out_buffer != NULL) {
        LOGE("out_buffer release");
        av_free(out_buffer);
        out_buffer = NULL;
    }

    if (swrContext != NULL) {
        LOGE("swrContext release");
        swr_close(swrContext);
        swr_free(&swrContext);
        swrContext = NULL;
    }

    if (avCodecContext != NULL) {
        LOGE("avCodecContext release");
        avcodec_close(avCodecContext);
        avcodec_free_context(&avCodecContext);
        avCodecContext = NULL;
    }

    if (timerid != NULL) {
        LOGE("timer_delete");
        timer_delete(timerid);
        timerid = NULL;
    }

    current = 0;
    duration = 0;

    LOGE("queue size = %d", avFrame_queue.size());
}

DZAudio::~DZAudio() {
    release();
}

