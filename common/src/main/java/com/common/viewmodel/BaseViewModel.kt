package com.common.viewmodel

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel


/**
 * FileName: BaseViewModel
 * @author: linyh19
 * Date: 2021/11/16 10:20
 * Description:
 */
open class BaseViewModel() : ViewModel(), LifecycleEventObserver {
    override fun onCleared() {
        Log.i("BaseViewModel", "View onCleared ----> ViewModel: $this")
    }
    /**
     * Called when a state transition event happens.
     *
     * @param source The source of the event
     * @param event The event
     */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                onCreate(source)
            }
            Lifecycle.Event.ON_START -> {
                onStart(source)
            }
            Lifecycle.Event.ON_RESUME -> {
                onResume(source)
            }
            Lifecycle.Event.ON_PAUSE -> {
                onPause(source)
            }
            Lifecycle.Event.ON_STOP -> {
                onStop(source)
            }
            Lifecycle.Event.ON_DESTROY -> {
                onDestroy(source)
            }
            else -> {
            }
        }
    }

    open fun onCreate(owner: LifecycleOwner) {}

    open fun onStart(owner: LifecycleOwner) {}

    open fun onResume(owner: LifecycleOwner) {}

    open fun onPause(owner: LifecycleOwner) {}

    open fun onStop(owner: LifecycleOwner) {}

    open fun onDestroy(owner: LifecycleOwner) {}

}