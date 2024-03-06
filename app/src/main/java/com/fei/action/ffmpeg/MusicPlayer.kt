package com.fei.action.ffmpeg

import android.os.Handler
import android.text.TextUtils
import android.util.Log

class MusicPlayer {

    interface OnStateCallback {
        fun onPrepared()
        fun onError(errCode: Int, msg: String);
    }

    private lateinit var url: String
    private var onStateCallback: OnStateCallback? = null

    fun setDataSource(url: String) {
        this.url = url
    }

    fun setOnStateCallback(callback: OnStateCallback?) {
        this.onStateCallback = callback
    }

    /**
     * 准备
     */
    fun prepare() {
        if (TextUtils.isEmpty(url)) {
            throw NullPointerException("url is unknow,please call setDataSource")
        }
        nPrepare(url)
    }

    /**
     * 异步准备
     */
    fun prepareAsync() {
        if (TextUtils.isEmpty(url)) {
            throw NullPointerException("url is unknow,please call setDataSource")
        }
        nPrepareAsync(url)
    }

    /**
     * 播放
     */
    fun play() {
        if (TextUtils.isEmpty(url)) {
            throw NullPointerException("url is unknow,please call setDataSource")
        }
        nPlay()
    }

    /**
     * 暂停
     */
    fun pause() {
        nPause()
    }

    fun stop() {
        nStop()
    }

    fun release() {
        onStateCallback = null
        nRelease()
    }

    private fun onError(errCode: Int, msg: String) {
        Log.i("tag", "thread = ${Thread.currentThread().name} errCode = $errCode msg = $msg")
        onStateCallback?.onError(errCode, msg)
    }

    private fun onPrepared() {
        Log.i("tag", "thread = ${Thread.currentThread().name} onPrepared 被调用")
        onStateCallback?.onPrepared()
    }

    private external fun nPlay()

    private external fun nPause();
    private external fun nStop();
    private external fun nPrepare(url: String)

    private external fun nPrepareAsync(url: String)

    private external fun nRelease()

    init {
        System.loadLibrary("demo")
    }


}