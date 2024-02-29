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
            if (musicPlayer == null) {
                musicPlayer = MusicPlayer()
                val url =
                    "/storage/emulated/0/Android/media/com.fei.firstproject/mda-ngi22v9vr986hsnm.mp4"
                if (File(url).exists()) {
                    Log.i("tag", "文件存在")
                }
                musicPlayer!!.setDataSource(url)
            }
            musicPlayer!!.prepareAsync()

        }
    }

}