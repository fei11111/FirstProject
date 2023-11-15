package com.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * FileName: ViewModelFactory
 * @author: linyh19
 * Date: 2021/11/18 10:06
 * Description:
 */
class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.newInstance()
    }
}