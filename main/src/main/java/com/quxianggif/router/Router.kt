package com.quxianggif.router

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import com.quxianggif.core.model.Version

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