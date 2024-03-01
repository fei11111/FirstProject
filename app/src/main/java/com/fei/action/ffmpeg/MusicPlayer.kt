package com.fei.action.ffmpeg

import android.media.AudioTrack
import android.media.MediaPlayer
import android.text.TextUtils
import android.util.Log

class MusicPlayer {

    private lateinit var url:String

    fun setDataSource(url:String) {
        this.url = url
    }

    /**
     * 准备
     */
    fun prepare(){
        if(TextUtils.isEmpty(url)) {
            throw NullPointerException("url is unknow,please call setDataSource")
        }
        nPrepare(url)
    }

    /**
     * 异步准备
     */
    fun prepareAsync() {
        if(TextUtils.isEmpty(url)) {
            throw NullPointerException("url is unknow,please call setDataSource")
        }
        nPrepareAsync(url)
    }

    /**
     * 播放
     */
    fun play(){
        if(TextUtils.isEmpty(url)) {
            throw NullPointerException("url is unknow,please call setDataSource")
        }
        nPlay()
    }

    /**
     * 暂停
     */
    fun pause() {

    }

    fun stop(){

    }

    fun release(){
        nRelease()
    }

    private fun onError(errCode:Int,msg:String) {

        Log.i("tag","thread = ${Thread.currentThread().name} errCode = $errCode msg = $msg")
    }

    private fun onPrepared() {
        Log.i("tag","thread = ${Thread.currentThread().name} onPrepared 被调用")
        play()
    }

    private external fun nPlay()
    private external fun nPrepare(url: String)

    private external fun nPrepareAsync(url:String)

    private external fun nRelease()

    init {
        System.loadLibrary("demo")
    }


}