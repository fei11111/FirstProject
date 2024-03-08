package com.fei.action.ffmpeg

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log

class MusicPlayer {

    interface OnStateCallback {
        fun onPrepared()

        fun onProgress(current: Long, total: Long)

        fun onCompleted()
        fun onError(errCode: Int, msg: String)
    }

    private lateinit var url: String
    private var onStateCallback: OnStateCallback? = null
    private val mHandler = Handler(Looper.getMainLooper())

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
        nRelease()
    }

    fun destroy() {
        release()
        mHandler.removeCallbacksAndMessages(null)
        onStateCallback = null
    }

    private fun onProgress(current: Long, total: Long) {
        Log.i("tag", "thread = ${Thread.currentThread().name} current = $current total = $total")
        mHandler.post {
            onStateCallback?.onProgress(current, total)
        }
    }

    private fun onError(errCode: Int, msg: String) {
        Log.i("tag", "thread = ${Thread.currentThread().name} errCode = $errCode msg = $msg")
        mHandler.post {
            onStateCallback?.onError(errCode, msg)
        }

    }

    private fun onPrepared() {
        Log.i("tag", "thread = ${Thread.currentThread().name} onPrepared 被调用")
        mHandler.post {
            onStateCallback?.onPrepared()
        }

    }

    private fun onCompleted() {
        Log.i("tag", "thread = ${Thread.currentThread().name} onCompleted 被调用")
        mHandler.post {
            onStateCallback?.onCompleted()
            release()
        }
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