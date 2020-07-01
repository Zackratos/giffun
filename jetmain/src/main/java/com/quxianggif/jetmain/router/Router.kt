package com.quxianggif.jetmain.router

import androidx.navigation.NavDirections

/**
 * Author   : zhangwenchao
 * Date     : 2019-12-05  17:08
 * Describe :
 */
interface Router {
//    fun actionStartLogin(fragment: Fragment, hasNewVersion: Boolean, version: Version?)
    fun actionStartLoginNavDirections(): NavDirections

    fun splashToMainNavDirections(): NavDirections

    fun splashToLoginNavDirections(startWithTransition: Boolean): NavDirections

    fun loginToMainNavDirections(): NavDirections

}