package com.fei.action.ffmpeg

import android.view.Surface
import android.view.SurfaceView

class MNPlayer(surfaceView: SurfaceView) {
    public external fun play(url: String, surface: Surface?)

    companion object {
        init {
            System.loadLibrary("demo")
        }
    }


}