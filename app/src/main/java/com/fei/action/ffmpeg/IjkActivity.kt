package com.fei.action.ffmpeg

import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.firstproject.databinding.ActivityIjkBinding
import java.io.File

class IjkActivity : BaseActivity<EmptyViewModel, ActivityIjkBinding>() {

    private var surface: Surface? = null
    private var mnPlayer: MNPlayer? = null
    private var musicPlayer: MusicPlayer? = null

    override fun createObserver() {

    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
        mBinding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                surface = holder.surface
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
    }

    fun play() {
        //todo 第一次没文件时进入初始化了，但是在点一次play就崩溃
        if (musicPlayer == null) {
            musicPlayer = MusicPlayer()
            musicPlayer?.setOnStateCallback(object : MusicPlayer.OnStateCallback {
                override fun onPrepared() {
                    musicPlayer?.play()
                    runOnUiThread {
                        mBinding.btnPlay.text = "pause"
                        mBinding.btnPlay.isEnabled = true
                    }
                }

                override fun onError(errCode: Int, msg: String) {
                    runOnUiThread {
                        mBinding.btnPlay.isEnabled = true
                    }
                }
            })
            val url =
                "/storage/emulated/0/Android/media/com.fei.firstproject/mda-ngi22v9vr986hsnm.mp4"
            if (File(url).exists()) {
                mBinding.btnPlay.isEnabled = false
                Log.i("tag", "文件存在")
                musicPlayer!!.setDataSource(url)
                musicPlayer!!.prepare()
            }

        } else {
            mBinding.btnPlay.isEnabled = false
            musicPlayer?.play()
            mBinding.btnPlay.text = "pause"
            mBinding.btnPlay.isEnabled = true
        }

    }

    fun pause() {
        mBinding.btnPlay.isEnabled = false
        musicPlayer?.pause()
        mBinding.btnPlay.text = "play"
        mBinding.btnPlay.isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        surface?.release()
        musicPlayer?.release()
    }

}