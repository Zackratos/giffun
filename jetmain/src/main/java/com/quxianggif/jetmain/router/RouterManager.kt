package com.quxianggif.jetmain.router

import androidx.navigation.NavDirections

/**
 * Author   : zhangwenchao
 * Date     : 2019-12-05  17:19
 * Describe :
 */
class RouterManager private constructor() {

    companion object {
        // 单例
        val INSTANCE by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { RouterManager() }
    }

    private lateinit var mRouter: Router

    fun register(router: Router) {
        mRouter = router
    }

    fun actionStartLoginNavDirections(): NavDirections = mRouter.actionStartLoginNavDirections()

    fun splashToMainNavDirections(): NavDirections = mRouter.splashToMainNavDirections()

    fun splashToLoginNavDirections(startWithTransition: Boolean = false): NavDirections = mRouter.splashToLoginNavDirections(startWithTransition)

    fun loginToMainNavDirections(): NavDirections = mRouter.loginToMainNavDirections()

}