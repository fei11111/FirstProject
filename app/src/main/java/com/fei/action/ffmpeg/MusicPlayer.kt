package com.fei.action.ffmpeg

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log

class MusicPlayer {

    private lateinit var url: String
    private var onStateCallback: OnStateCallback? = null
    private val mHandler = Handler(Looper.getMainLooper())
    private var preparedAndPlay = false
    var isPrepared = false
    var isPlaying = false

    init {
        System.loadLibrary("demo")
    }

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
        preparedAndPlay = false
        if (TextUtils.isEmpty(url)) {
            throw NullPointerException("url is unknow,please call setDataSource")
        }
        nPrepare(url)
    }

    /**
     * 异步准备
     */
    fun prepareAsyncAndPlay() {
        preparedAndPlay = true
        if (TextUtils.isEmpty(url)) {
            throw NullPointerException("url is unknow,please call setDataSource")
        }
        nPrepareAsync(url)
    }

    /**
     * 异步准备
     */
    fun prepareAsync() {
        preparedAndPlay = false
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
        isPlaying = true
    }

    fun seek(position:Int) {
        nSeek(position)
    }

    /**
     * 暂停
     */
    fun pause() {
        nPause()
        isPlaying = false
    }

    fun stop() {
        nStop()
        isPlaying = false
    }

    fun release() {
        nRelease()
        isPlaying = false
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
        isPrepared = true
        Log.i("tag", "thread = ${Thread.currentThread().name} onPrepared 被调用")
        mHandler.post {
            onStateCallback?.onPrepared()
        }
        if (preparedAndPlay) {
            play()
        }
    }

    private fun onCompleted() {
        isPrepared = false
        Log.i("tag", "thread = ${Thread.currentThread().name} onCompleted 被调用")
        mHandler.post {
            onStateCallback?.onCompleted()
            release()
        }
    }

    private external fun nPlay()

    private external fun nSeek(position: Int)
    private external fun nPause();
    private external fun nStop();
    private external fun nPrepare(url: String)

    private external fun nPrepareAsync(url: String)

    private external fun nRelease()

    interface OnStateCallback {
        fun onPrepared()

        fun onProgress(current: Long, total: Long)

        fun onCompleted()
        fun onError(errCode: Int, msg: String)
    }


}