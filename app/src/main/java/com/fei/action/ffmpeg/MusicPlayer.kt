package com.fei.action.ffmpeg

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.Surface

class MusicPlayer {

    private lateinit var url: String
    private var onStateCallback: OnStateCallback? = null
    private val mHandler = Handler(Looper.getMainLooper())
    private var preparedAndPlay = false
    var isPrepared = false
    var isPlaying = false

    init {
        /**
        "video/x-vnd.on2.vp8" - VP8 video (i.e. video in .webm)
        "video/x-vnd.on2.vp9" - VP9 video (i.e. video in .webm)
        "video/avc" - H.264/AVC video
        "video/hevc" - H.265/HEVC video
        "video/mp4v-es" - MPEG4 video
        "video/3gpp" - H.263 video
        "audio/3gpp" - AMR narrowband audio
        "audio/amr-wb" - AMR wideband audio
        "audio/mpeg" - MPEG1/2 audio layer III
        "audio/mp4a-latm" - AAC audio (note, this is raw AAC packets, not packaged in LATM!)
        "audio/vorbis" - vorbis audio
        "audio/g711-alaw" - G.711 alaw audio
        "audio/g711-mlaw" - G.711 ulaw audio
         */
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

    fun seek(position: Int) {
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
        mHandler.post {
            onStateCallback?.onProgress(current, total)
        }
    }

    private fun onSizeChange(width: Int,height: Int) {
        mHandler.post {
            onStateCallback?.onSizeChange(width,height)
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

    fun renderSurface(surface: Surface) {
        nSetSurfaceAndArea(surface)
    }

    //测试使用
    fun renderImage(surface: Surface, bitmap: Bitmap) {
        nRenderImage(surface, bitmap)
    }

    private external fun nRenderImage(surface: Surface, bitmap: Bitmap);
    private external fun nPlay()
    private external fun nSeek(position: Int)
    private external fun nPause();
    private external fun nStop();
    private external fun nPrepare(url: String)

    private external fun nPrepareAsync(url: String)

    private external fun nRelease()

    private external fun nSetSurfaceAndArea(surface: Surface)

    interface OnStateCallback {
        fun onPrepared()

        fun onSizeChange(width: Int, height: Int)

        fun onProgress(current: Long, total: Long)

        fun onCompleted()
        fun onError(errCode: Int, msg: String)
    }


}