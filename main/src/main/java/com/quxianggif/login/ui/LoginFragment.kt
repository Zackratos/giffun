package com.quxianggif.login.ui

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.quxianggif.R
import com.quxianggif.core.GifFun
import com.quxianggif.core.util.AndroidVersion
import com.quxianggif.core.util.GlobalUtil
import com.quxianggif.event.FinishActivityEvent
import com.quxianggif.event.MessageEvent
import com.quxianggif.feeds.ui.MainFragment
import com.quxianggif.router.RouterManager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Author   : zhangwenchao
 * Date     : 2019-12-05  10:45
 * Describe :
 */
open class LoginFragment: AuthFragment() {

    /**
     * 是否正在进行transition动画。
     */
    protected var isTransitioning = false


    override fun forwardToMainFragment() {
        MainFragment.actionStart(findNavController(), RouterManager.INSTANCE.loginToMainNavDirections())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onMessageEvent(messageEvent: MessageEvent) {
        if (messageEvent is FinishActivityEvent && LoginActivity::class.java == messageEvent.activityClass) {
            findNavController().popBackStack()
        }
    }

    companion object {

        private const val TAG = "LoginFragment"


        /**
         * 启动LoginActivity。
         * 原来的方法中传入的两个参数（hasNewVersion 和 version）并没有用到，直接删了
         */
        fun actionStart(navController: NavController, navDirections: NavDirections) = navController.navigate(navDirections)


        /**
         * 启动LoginActivity，并附带Transition动画。
         * 跟上面一样，删除了无用的参数
         */
        fun actionStartWithTransition(navController: NavController, logo: View, navDirections: NavDirections) {
            if (AndroidVersion.hasLollipop()) {
                //todo 这里不知为什么共享 View 动画不能正常执行
                val extras = FragmentNavigatorExtras(logo to GlobalUtil.getString(R.string.transition_logo_splash))
                navController.navigate(navDirections, extras)
            } else {
                actionStart(navController, navDirections)
            }
        }
    }

}