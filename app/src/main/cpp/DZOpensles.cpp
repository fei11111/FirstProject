//
// Created by huangjf on 2024/3/5.
//

#include "DZOpensles.h"
#include "DZAudio.h"

DZOpensles::DZOpensles() {
}

DZOpensles::~DZOpensles() {
    release();
}

void playerCallback(SLAndroidSimpleBufferQueueItf caller, void *pContext) {
    DZAudio *pAudio = (DZAudio *) pContext;
    int dataSize = pAudio->resampleAudio();
    if (dataSize == -1) return;
    (*caller)->Enqueue(caller, pAudio->out_buffer, dataSize);
}

void DZOpensles::play(void *pContext) {
    if (this->pPlayItf == NULL) {
        //创建引擎
        slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
        //实现引擎
        (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
        //获取引擎接口
        (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
        //设置混音器:  创建输出混音器对象，实现输出混音器

        const SLInterfaceID ids[1] = {SL_IID_ENVIRONMENTALREVERB};
        const SLboolean req[1] = {SL_BOOLEAN_FALSE};
        //创建混音器
        (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, ids, req);
        //实现输出混音器
        (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);

        //获取混响接口
        (*outputMixObject)->GetInterface(outputMixObject, SL_IID_ENVIRONMENTALREVERB,
                                         &outputMixEnvironmentalReverb);
        SLEnvironmentalReverbSettings reverbSettings = SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;
        //设置混响
        (*outputMixEnvironmentalReverb)->SetEnvironmentalReverbProperties(
                outputMixEnvironmentalReverb,
                &reverbSettings);
        // 创建播放器
        this->pPlayer = NULL;
        this->pPlayItf = NULL;
        SLDataLocator_AndroidSimpleBufferQueue simpleBufferQueue = {
                SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
        // PCM 格式
        SLDataFormat_PCM formatPcm = {
                SL_DATAFORMAT_PCM,//pcm 格式
                2,//两声道
                SL_SAMPLINGRATE_44_1,//采样率44100 Hz
                SL_PCMSAMPLEFORMAT_FIXED_16,//采样位16位
                SL_PCMSAMPLEFORMAT_FIXED_16,//容器 16位
                SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,//左右声道
                SL_BYTEORDER_LITTLEENDIAN};//小端格式
        //设置音频数据源：simpleBufferQueue（配置缓冲区）、 formatPcm（音频格式）
        SLDataSource audioSrc = {&simpleBufferQueue, &formatPcm};

        SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
        SLDataSink audioSnk = {&outputMix, NULL};
        SLInterfaceID interfaceIds[3] = {SL_IID_BUFFERQUEUE, SL_IID_VOLUME, SL_IID_PLAYBACKRATE};
        SLboolean interfaceRequired[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};
        (*engineEngine)->CreateAudioPlayer(engineEngine, &pPlayer, &audioSrc, &audioSnk, 3,
                                           interfaceIds, interfaceRequired);
        (*pPlayer)->Realize(pPlayer, SL_BOOLEAN_FALSE);
        //获取播放器接口
        (*pPlayer)->GetInterface(pPlayer, SL_IID_PLAY, &pPlayItf);
        // 设置缓存队列和回调函数
        (*pPlayer)->GetInterface(pPlayer, SL_IID_BUFFERQUEUE, &playerBufferQueue);
        // 每次回调 this 会被带给 playerCallback 里面的 context
        (*playerBufferQueue)->RegisterCallback(playerBufferQueue, playerCallback, pContext);
        // 设置播放状态
        (*pPlayItf)->SetPlayState(pPlayItf, SL_PLAYSTATE_PLAYING);
        //调用回调函数
        playerCallback(playerBufferQueue, pContext);
    } else {
        (*pPlayItf)->SetPlayState(pPlayItf, SL_PLAYSTATE_PLAYING);
    }

}

void DZOpensles::pause() {
    (*pPlayItf)->SetPlayState(pPlayItf, SL_PLAYSTATE_PAUSED);
}

void DZOpensles::stop() {
    (*pPlayItf)->SetPlayState(pPlayItf, SL_PLAYSTATE_STOPPED);
}

void DZOpensles::release() {
    if (pPlayer != NULL) {
        (*playerBufferQueue)->Clear(playerBufferQueue);
//        (*pPlayer)->Destroy(pPlayer);
        pPlayer = NULL;
        pPlayItf = NULL;
        playerBufferQueue = NULL;
    }

    if (engineObject != NULL) {
        LOGE("engineObject release");
//        (*engineObject)->Destroy(engineObject);
        engineObject = NULL;
        engineEngine = NULL;
    }

    if (outputMixObject != NULL) {
        LOGE("outputMixObject release");
//        (*outputMixObject)->Destroy(outputMixObject);
        outputMixObject = NULL;
        outputMixEnvironmentalReverb = NULL;
    }

}

