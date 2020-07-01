package com.quxianggif.jetpack.router

import androidx.navigation.NavDirections
import com.quxianggif.jetmain.router.Router
import com.quxianggif.jetpack.JetPackLoginFragmentDirections
import com.quxianggif.jetpack.JetPackSplashFragmentDirections
import com.quxianggif.jetpack.NavigationGiffunDirections

/**
 * Author   : zhangwenchao
 * Date     : 2019-12-05  17:17
 * Describe :
 */
class RouterImpl: Router {

    override fun actionStartLoginNavDirections(): NavDirections = NavigationGiffunDirections.actionStartLogin()

    override fun splashToMainNavDirections(): NavDirections = JetPackSplashFragmentDirections.splashToMain()

    override fun splashToLoginNavDirections(startWithTransition: Boolean): NavDirections = JetPackSplashFragmentDirections.splashToLogin(startWithTransition)

    override fun loginToMainNavDirections(): NavDirections = JetPackLoginFragmentDirections.actionStartLogin()

}