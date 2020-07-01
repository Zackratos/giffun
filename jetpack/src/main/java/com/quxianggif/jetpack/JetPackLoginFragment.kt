package com.quxianggif.jetpack

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.quxianggif.core.extension.logWarn
import com.quxianggif.core.extension.showToast
import com.quxianggif.core.util.AndroidVersion
import com.quxianggif.core.util.GlobalUtil
import com.quxianggif.jetpack.databinding.FragmentLoginBinding
import com.quxianggif.jetmain.login.ui.LoginFragment
import com.quxianggif.network.model.Callback
import com.quxianggif.network.model.FetchVCode
import com.quxianggif.network.model.PhoneLogin
import com.quxianggif.network.model.Response
import com.quxianggif.util.ResponseHandler
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.regex.Pattern

/**
 * Author   : zhangwenchao
 * Date     : 2019-12-05  18:42
 * Describe :
 */
class JetPackLoginFragment: LoginFragment() {

    private lateinit var timer: CountDownTimer
    /**
     * 是否正在登录中。
     */
    private var isLogin = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        super.setupViews()
        val safeArgs: JetPackLoginFragmentArgs by navArgs()
        val startWithTransition = safeArgs.startWithTransition
        //todo 这里不知为什么共享 View 动画不能正常执行
//        if (AndroidVersion.hasLollipop() && isStartWithTransition) {
//            isTransitioning = true
//            sharedElementEnterTransition.addListener(object : SimpleTransitionListener() {
//                override fun onTransitionEnd(transition: Transition) {
//                    val event = FinishActivityEvent()
//                    event.activityClass = OpenSourceSplashActivity::class.java
//                    EventBus.getDefault().post(event)
//                    isTransitioning = false
//                    fadeElementsIn()
//                }
//            })
//        } else {
//            loginLayoutBottom.visibility = View.VISIBLE
//            loginBgWallLayout.visibility = View.VISIBLE
//        }
        if (AndroidVersion.hasLollipop() && startWithTransition) {
            loginLayoutBottom.visibility = View.VISIBLE
            loginBgWallLayout.visibility = View.VISIBLE
        } else {
            loginLayoutBottom.visibility = View.VISIBLE
            loginBgWallLayout.visibility = View.VISIBLE
        }
        timer = SMSTimer(60 * 1000, 1000)
        getVerifyCode.setOnClickListener {
            val number = phoneNumberEdit.text.toString()
            if (number.isEmpty()) {
                showToast(GlobalUtil.getString(R.string.phone_number_is_empty))
                return@setOnClickListener
            }
            val pattern = "^1\\d{10}\$"
            if (!Pattern.matches(pattern, number)) {
                showToast(GlobalUtil.getString(R.string.phone_number_is_invalid))
                return@setOnClickListener
            }
            getVerifyCode.isClickable = false
            FetchVCode.getResponse(number, object : Callback {
                override fun onResponse(response: Response) {
                    if (response.status == 0) {
                        timer.start()
                        verifyCodeEdit.requestFocus()
                    } else {
                        showToast(response.msg)
                        getVerifyCode.isClickable = true
                    }
                }

                override fun onFailure(e: Exception) {
                    logWarn(TAG, e.message, e)
                    ResponseHandler.handleFailure(e)
                    getVerifyCode.isClickable = true
                }
            })
        }
        loginButton.setOnClickListener {
            if (isLogin) return@setOnClickListener
            val number = phoneNumberEdit.text.toString()
            val code = verifyCodeEdit.text.toString()
            if (number.isEmpty() || code.isEmpty()) {
                showToast(GlobalUtil.getString(R.string.phone_number_or_code_is_empty))
                return@setOnClickListener
            }
            val pattern = "^1\\d{10}\$"
            if (!Pattern.matches(pattern, number)) {
                showToast(GlobalUtil.getString(R.string.phone_number_is_invalid))
                return@setOnClickListener
            }
            processLogin(number, code)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun processLogin(number: String, code: String) {
        hideSoftKeyboard()
        loginInProgress(true)
        PhoneLogin.getResponse(number, code, object : Callback {
            override fun onResponse(response: Response) {
                if (!ResponseHandler.handleResponse(response)) {
                    val thirdPartyLogin = response as PhoneLogin
                    val status = thirdPartyLogin.status
                    val msg = thirdPartyLogin.msg
                    val userId = thirdPartyLogin.userId
                    val token = thirdPartyLogin.token
                    when (status) {
                        0 -> {
                            hideSoftKeyboard()
                            // 处理登录成功时的逻辑，包括数据缓存，界面跳转等
                            saveAuthData(userId, token, TYPE_PHONE_LOGIN)
                            getUserBaseinfo()
                        }
                        10101 -> {
                            hideSoftKeyboard()
//                            OpenSourceRegisterActivity.registerByPhone(this@OpenSourceLoginActivity, number, code)
                            loginInProgress(false)
                        }
                        else -> {
                            logWarn(TAG, "Login failed. " + GlobalUtil.getResponseClue(status, msg))
                            showToast(response.msg)
                            loginInProgress(false)
                        }
                    }
                } else {
                    loginInProgress(false)
                }
            }

            override fun onFailure(e: Exception) {
                logWarn(TAG, e.message, e)
                ResponseHandler.handleFailure(e)
                loginInProgress(false)
            }
        })
    }

    /**
     * 根据用户是否正在注册来刷新界面。如果正在处理就显示进度条，否则的话就显示输入框。
     *
     * @param inProgress 是否正在注册
     */
    private fun loginInProgress(inProgress: Boolean) {
        if (AndroidVersion.hasMarshmallow() && !(inProgress && loginRootLayout.keyboardShowed)) {
            TransitionManager.beginDelayedTransition(loginRootLayout, Fade())
        }
        isLogin = inProgress
        if (inProgress) {
            loginInputElements.visibility = View.INVISIBLE
            loginProgressBar.visibility = View.VISIBLE
        } else {
            loginProgressBar.visibility = View.INVISIBLE
            loginInputElements.visibility = View.VISIBLE
        }
    }

    /**
     * 将LoginActivity的界面元素使用淡入的方式显示出来。
     */
    private fun fadeElementsIn() {
        TransitionManager.beginDelayedTransition(loginLayoutBottom, Fade())
        loginLayoutBottom.visibility = View.VISIBLE
        TransitionManager.beginDelayedTransition(loginBgWallLayout, Fade())
        loginBgWallLayout.visibility = View.VISIBLE
    }

    inner class SMSTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            getVerifyCode.text = GlobalUtil.getString(R.string.fetch_vcode)
            getVerifyCode.isClickable = true
        }

        override fun onTick(millisUntilFinished: Long) {
            getVerifyCode.text = String.format(GlobalUtil.getString(R.string.sms_is_sent), millisUntilFinished / 1000)
        }

    }

    companion object {
        const val TAG = "JetPackLoginFragment"
    }

}