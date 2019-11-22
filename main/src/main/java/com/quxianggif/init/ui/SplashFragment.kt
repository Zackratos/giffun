package com.quxianggif.init.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.quxianggif.common.ui.BaseJetPackFragment
import com.quxianggif.core.Const
import com.quxianggif.core.GifFun
import com.quxianggif.core.extension.logWarn
import com.quxianggif.core.model.Version
import com.quxianggif.core.util.GlobalUtil
import com.quxianggif.core.util.SharedUtil
import com.quxianggif.event.FinishActivityEvent
import com.quxianggif.event.MessageEvent
import com.quxianggif.feeds.ui.MainFragment
import com.quxianggif.login.ui.LoginFragment
import com.quxianggif.network.model.Init
import com.quxianggif.network.model.OriginThreadCallback
import com.quxianggif.network.model.Response
import com.quxianggif.router.RouterManager
import com.quxianggif.util.ResponseHandler
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Author   : zhangwenchao
 * Date     : 2019-11-26  10:15
 * Describe :
 */
open class SplashFragment : BaseJetPackFragment() {
    /**
     * 记录进入SplashActivity的时间。
     */
    var enterTime: Long = 0

    /**
     * 判断是否正在跳转或已经跳转到下一个界面。
     */
    var isForwarding = false

    var hasNewVersion = false

    lateinit var logoView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTime = System.currentTimeMillis()
        delayToForward()
        // 屏蔽手机的返回键
        requireActivity().onBackPressedDispatcher.addCallback(this) {  }
    }

    override fun setupViews() {
        super.setupViews()
        startInitRequest()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onMessageEvent(messageEvent: MessageEvent) {
        if (messageEvent is FinishActivityEvent) {
            if (javaClass == messageEvent.activityClass) {
                findNavController().popBackStack()
            }
        }
    }

    /**
     * 开始向服务器发送初始化请求。
     */
    private fun startInitRequest() {
        Init.getResponse(object : OriginThreadCallback {
            override fun onResponse(response: Response) {
                if (activity == null) {
                    return
                }
                var version: Version? = null
                val init = response as Init
                GifFun.BASE_URL = init.base
                if (!ResponseHandler.handleResponse(init)) {
                    val status = init.status
                    if (status == 0) {
                        val token = init.token
                        val avatar = init.avatar
                        val bgImage = init.bgImage
                        hasNewVersion = init.hasNewVersion
                        if (hasNewVersion) {
                            version = init.version
                        }
                        if (!TextUtils.isEmpty(token)) {
                            SharedUtil.save(Const.Auth.TOKEN, token)
                            if (!TextUtils.isEmpty(avatar)) {
                                SharedUtil.save(Const.User.AVATAR, avatar)
                            }
                            if (!TextUtils.isEmpty(bgImage)) {
                                SharedUtil.save(Const.User.BG_IMAGE, bgImage)
                            }
                            GifFun.refreshLoginState()
                        }
                    } else {
                        logWarn(TAG, GlobalUtil.getResponseClue(status, init.msg))
                    }
                }
                forwardToNextFragment(hasNewVersion, version)
            }

            override fun onFailure(e: Exception) {
                logWarn(TAG, e.message, e)
                forwardToNextFragment(false, null)
            }
        })
    }

    /**
     * 设置闪屏界面的最大延迟跳转，让用户不至于在闪屏界面等待太久。
     */
    private fun delayToForward() {
        Thread(Runnable {
            GlobalUtil.sleep(MAX_WAIT_TIME.toLong())
            forwardToNextFragment(false, null)
        }).start()
    }

    /**
     * 跳转到下一个Fragment。如果在闪屏界面停留的时间还不足规定最短停留时间，则会在这里等待一会，保证闪屏界面不至于一闪而过。
     */
    @Synchronized
    open fun forwardToNextFragment(hasNewVersion: Boolean, version: Version?) {
        if (!isForwarding) { // 如果正在跳转或已经跳转到下一个界面，则不再重复执行跳转
            isForwarding = true
            val currentTime = System.currentTimeMillis()
            val timeSpent = currentTime - enterTime
            if (timeSpent < MIN_WAIT_TIME) {
                GlobalUtil.sleep(MIN_WAIT_TIME - timeSpent)
            }
            GifFun.getHandler().post {
                if (GifFun.isLogin()) {
                    MainFragment.actionStart(findNavController(), RouterManager.INSTANCE.splashToMainNavDirections())
                } else {
                    if (isActive) {
                        LoginFragment.actionStartWithTransition(findNavController(), logoView, RouterManager.INSTANCE.splashToLoginNavDirections(true))
                    } else {
                        LoginFragment.actionStart(findNavController(), RouterManager.INSTANCE.splashToLoginNavDirections())
                    }
                }
            }
        }
    }

    companion object {

        private const val TAG = "SplashFragment"

        /**
         * 应用程序在闪屏界面最短的停留时间。
         */
        const val MIN_WAIT_TIME = 2000

        /**
         * 应用程序在闪屏界面最长的停留时间。
         */
        const val MAX_WAIT_TIME = 5000
    }

}