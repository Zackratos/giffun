package com.quxianggif.jetpack

import com.quxianggif.GifFunApplication
import com.quxianggif.jetpack.router.RouterImpl
import com.quxianggif.router.RouterManager

/**
 * Author   : zhangwenchao
 * Date     : 2019-12-05  17:30
 * Describe :
 */
class JetPackApplication: GifFunApplication() {

    override fun onCreate() {
        super.onCreate()
        RouterManager.INSTANCE.register(RouterImpl())
    }
}