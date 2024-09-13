package com.fei.action.ffmpeg

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.widget.SeekBar
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.firstproject.R
import com.fei.firstproject.databinding.ActivityIjkBinding
import java.io.File

class IjkActivity : BaseActivity<EmptyViewModel, ActivityIjkBinding>() {

    private var surface: Surface? = null
    private var mnPlayer: MNPlayer? = null
    private var musicPlayer: MusicPlayer? = null

    override fun createObserver() {

    }

    override fun initViewAndData(savedInstanceState: Bundle?) {

        musicPlayer = MusicPlayer()
        musicPlayer?.setOnStateCallback(object : MusicPlayer.OnStateCallback {
            override fun onPrepared() {
                mBinding.btnPlay.isEnabled = true
            }

            override fun onSizeChange(width: Int, height: Int) {
                mBinding.surfaceView.onSizeChange(width, height)
            }

            override fun onProgress(current: Long, total: Long) {
                mBinding.tvStartTime.text =
                    "${getHour(current)}:${getMinute(current)}:${getSecond(current)}"
                mBinding.tvEndTime.text =
                    "${getHour(total)}:${getMinute(total)}:${getSecond(total)}"
                mBinding.seek.max = total.toInt()
                mBinding.seek.progress = current.toInt()
            }

            override fun onCompleted() {
                mBinding.tvStartTime.text = "0:00:00"
                mBinding.tvEndTime.text = "0:00:00"
                mBinding.seek.max = 0
                mBinding.seek.progress = 0
                mBinding.btnPlay.text = "play"
            }

            override fun onError(errCode: Int, msg: String) {
                mBinding.btnPlay.isEnabled = true
            }
        })
        prepare(false)

        //测试
        val option = BitmapFactory.Options()
        option.inScaled = false
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_bg_run, option)

        mBinding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                surface = holder.surface
                Log.i(
                    "tag",
                    "surfaceView 宽度 = ${mBinding.surfaceView.width} 高度 = ${mBinding.surfaceView.height}"
                )
                Log.i("tag", "surface = ${surface.hashCode()}")
                musicPlayer?.renderSurface(surface!!)
                //测试
//                musicPlayer?.renderImage(surface!!,bitmap)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

        })
        mBinding.btnPlay.setOnClickListener {
//            if(mnPlayer==null) {
//                mnPlayer = MNPlayer(mBinding.surfaceView)
//            }
//            mnPlayer?.play("/storage/emulated/0/Android/media/com.fei.firstproject/mmexport1704758389503.mp4", surface)
            if (mBinding.btnPlay.text.toString() == "play") {
                play()
            } else {
                pause()
            }

        }

        mBinding.btnStop.setOnClickListener {
            if (musicPlayer != null) {
                mBinding.btnStop.isEnabled = false
                musicPlayer?.stop()
                mBinding.btnStop.isEnabled = true
                mBinding.btnPlay.text = "play"
            }
        }

        mBinding.seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (musicPlayer != null && musicPlayer!!.isPrepared) {
                    musicPlayer?.seek(seekBar!!.progress)
                    play()
                }
            }

        })
    }

    fun play() {
        //todo 第一次没文件时进入初始化了，但是在点一次play就崩溃
        if (!musicPlayer!!.isPrepared) {
            prepare(true)
        } else if (!musicPlayer!!.isPlaying) {
            mBinding.btnPlay.isEnabled = false
            musicPlayer?.play()
            mBinding.btnPlay.text = "pause"
            mBinding.btnPlay.isEnabled = true
        }
    }

    private fun prepare(andPlay: Boolean) {
        val url =
            "/storage/emulated/0/Android/media/com.fei.firstproject/mda-qa72ak0psc13w061.mp4"
        if (File(url).exists()) {
            mBinding.btnPlay.isEnabled = false
            Log.i("tag", "文件存在")
            musicPlayer!!.setDataSource(url)
            if (andPlay) {
                musicPlayer!!.prepareAsyncAndPlay()
                mBinding.btnPlay.text = "pause"
            } else {
                musicPlayer!!.prepareAsync()
            }

        }
    }

    private fun getHour(time: Long): String {
        return String.format("%d", time / 3600)
    }

    private fun getMinute(time: Long): String {
        val minute = time / 60
        return String.format("%02d", if (minute == 60L) 0 else minute)
    }

    private fun getSecond(time: Long): String {
        val second = time % 60
        return String.format("%02d", if (second == 60L) 0 else second)
    }

    fun pause() {
        if (musicPlayer != null && musicPlayer!!.isPrepared) {
            mBinding.btnPlay.isEnabled = false
            musicPlayer?.pause()
            mBinding.btnPlay.text = "play"
            mBinding.btnPlay.isEnabled = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        surface?.release()
        musicPlayer?.destroy()
    }

}