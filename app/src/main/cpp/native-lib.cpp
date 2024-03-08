#include <jni.h>
#include <sched.h>
#include <string>
#include <sys/times.h>
#include <android/log.h>
#include "pthread.h"
#include "DZConstDefine.h"
#include "DZJNICall.h"
#include "DZFFmpeg.h"


extern "C" {
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include <libswresample/swresample.h>
}


void printLog(const char *tag, const char *message) {
    __android_log_write(ANDROID_LOG_DEBUG, tag,
                        message); // ANDROID_LOG_DEBUG为日志等级，tag为标识符，message为要打印的内容
}

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

extern "C"
JNIEXPORT jfloat JNICALL
Java_com_fei_action_optimize_startup_StartUpOpActivity_getCpuTime(JNIEnv *env, jobject thiz) {
    struct tms currentTms;
    times(&currentTms);
    return currentTms.tms_utime + currentTms.tms_stime;
}

AVFormatContext *pFormatCtx;
AVCodecContext *context;
bool isStart = false;
int videoStream = -1;
int audioStream = -1;

//读取线程
void *decodePacket(void *arg) {
    printLog("decodePacket", "decodePacket");
    AVPacket *packet = av_packet_alloc();
    while (isStart) {
        int rect = av_read_frame(pFormatCtx, packet);
        if (rect < 0) {
            break;
        }
        if (packet->stream_index == videoStream) {
            //放入视频队列
        } else if (packet->stream_index == audioStream) {
            //放入音频队列
        }
    }
    return NULL;
}

//视频解码线程
void *decodeVideo(void *arg) {
    printLog("decodeVideo", "decodeVideo");
    while (isStart) {
        //从视频队列读取


    }
    return 0;
}

void *decodeAudio(void *arg) {
    printLog("decodeAudio", "decodeAudio");
    while (isStart) {
        //从音频队列读取
    }
    return 0;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fei_action_ffmpeg_MNPlayer_play(JNIEnv *env, jobject thiz, jstring s, jobject surface) {
    printLog("play", "play");
    const char *url = env->GetStringUTFChars(s, 0);
    avformat_network_init();
    pFormatCtx = avformat_alloc_context();
    //视频文件打开
    avformat_open_input(&pFormatCtx, url, NULL, NULL);
    //获取流信息
    avformat_find_stream_info(pFormatCtx, NULL);
//    av_dump_format(pFormatCtx, 0, url, 0);
    //遍历流,获取视频流，音频流
    for (int i = 0; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoStream = i;
            AVCodecParameters *parameters = pFormatCtx->streams[i]->codecpar;
//            char str1[50] = "videoStream = ";
//            sprintf(str1, "videoStream = %d", videoStream);
//            printLog("tag", str1);
//            sprintf(str1, "%d", parameters->width);
//            printLog("tag", str1);
//            sprintf(str1, "%d", parameters->height);
//            printLog("tag", str1);
            //获取解码器
            const AVCodec *codec = avcodec_find_decoder(parameters->codec_id);
            //解码器上下文
            context = avcodec_alloc_context3(codec);
            //设置对应的参数
            avcodec_parameters_to_context(context, parameters);

            //打开解码器
            avcodec_open2(context, codec, NULL);

            //创建视频队列

        } else if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            audioStream = i;
//            char str1[50] = "audioStream = ";
//            sprintf(str1, "audioStream = %d", audioStream);
//            printLog("tag", str1);
            //创建音频队列
        }

    }

    //        ANativeWindow_fromSurface(env, surface);
//        ANative

    //读取线程
    pthread_t thread_decode;
    pthread_create(&thread_decode, NULL, decodePacket, NULL);
    //视频线程
    pthread_t thread_video;
    pthread_create(&thread_video, NULL, decodeVideo, NULL);
    //音频线程
    pthread_t thread_audio;
    pthread_create(&thread_audio, NULL, decodeAudio, NULL);

    avformat_close_input(&pFormatCtx);
    avformat_network_deinit();
    env->ReleaseStringUTFChars(s, url);


}

JavaVM *g_vm;
DZJNICall *dzjniCall;
DZFFmpeg *dzfFmpeg;
extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    g_vm = vm;
    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fei_action_ffmpeg_MusicPlayer_nPrepare(JNIEnv *env, jobject thiz, jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);
    if (dzjniCall == NULL) {
        dzjniCall = new DZJNICall(g_vm, env, thiz);
    }
    if (dzfFmpeg == NULL) {
        dzfFmpeg = new DZFFmpeg(url, env, dzjniCall);
    }
    dzfFmpeg->prepare();
    env->ReleaseStringUTFChars(url_, url);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fei_action_ffmpeg_MusicPlayer_nPrepareAsync(JNIEnv *env, jobject thiz, jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);
    if (dzjniCall == NULL) {
        dzjniCall = new DZJNICall(g_vm, env, thiz);
    }
    if (dzfFmpeg == NULL) {
        dzfFmpeg = new DZFFmpeg(url, env, dzjniCall);
    }
    dzfFmpeg->prepareAsync();
    env->ReleaseStringUTFChars(url_, url);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fei_action_ffmpeg_MusicPlayer_nPlay(JNIEnv *env, jobject thiz) {
    if (dzfFmpeg != NULL) {
        dzfFmpeg->play();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fei_action_ffmpeg_MusicPlayer_nPause(JNIEnv *env, jobject thiz) {
    if (dzfFmpeg != NULL) {
        dzfFmpeg->pause();
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_fei_action_ffmpeg_MusicPlayer_nStop(JNIEnv *env, jobject thiz) {
    if (dzfFmpeg != NULL) {
        dzfFmpeg->stop();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fei_action_ffmpeg_MusicPlayer_nSeek(JNIEnv *env, jobject thiz, jint position) {
    if (dzfFmpeg != NULL) {
        dzfFmpeg->seekTo(position);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fei_action_ffmpeg_MusicPlayer_nRelease(JNIEnv *env, jobject thiz) {
    if (dzfFmpeg != NULL) {
        delete dzfFmpeg;
        dzfFmpeg = NULL;
    }
    if (dzjniCall != NULL) {
        delete dzjniCall;
        dzjniCall = NULL;
    }
}
