package com.quxianggif.login.ui

import androidx.navigation.fragment.findNavController
import com.quxianggif.R
import com.quxianggif.common.ui.BaseJetPackFragment
import com.quxianggif.core.Const
import com.quxianggif.core.GifFun
import com.quxianggif.core.extension.logWarn
import com.quxianggif.core.extension.showToast
import com.quxianggif.core.util.GlobalUtil
import com.quxianggif.core.util.SharedUtil
import com.quxianggif.network.model.Callback
import com.quxianggif.network.model.GetBaseinfo
import com.quxianggif.network.model.Response
import com.quxianggif.util.ResponseHandler
import com.quxianggif.util.UserUtil

/**
 * Author   : zhangwenchao
 * Date     : 2019-12-05  10:50
 * Describe :
 */
abstract class AuthFragment: BaseJetPackFragment() {

    /**
     * 根据参数中传入的登录类型数值获取登录类型的名称。
     *
     * @param loginType
     * 登录类型的数值。
     * @return 登录类型的名称。
     */
    protected fun getLoginTypeName(loginType: Int) = when (loginType) {
        TYPE_QQ_LOGIN -> "QQ"
        TYPE_WECHAT_LOGIN -> "微信"
        TYPE_WEIBO_LOGIN -> "微博"
        TYPE_GUEST_LOGIN -> "游客"
        else -> ""
    }

    /**
     * 存储用户身份的信息。
     *
     * @param userId
     * 用户id
     * @param token
     * 用户token
     * @param loginType
     * 登录类型
     */
    protected fun saveAuthData(userId: Long, token: String, loginType: Int) {
        SharedUtil.save(Const.Auth.USER_ID, userId)
        SharedUtil.save(Const.Auth.TOKEN, token)
        SharedUtil.save(Const.Auth.LOGIN_TYPE, loginType)
        GifFun.refreshLoginState()
    }

    /**
     * 获取当前登录用户的基本信息，包括昵称、头像等。
     */
    protected fun getUserBaseinfo() {
        GetBaseinfo.getResponse(object : Callback {
            override fun onResponse(response: Response) {
                if (fragment == null) {
                    return
                }
                if (!ResponseHandler.handleResponse(response)) {
                    val baseinfo = response as GetBaseinfo
                    val status = baseinfo.status
                    when (status) {
                        0 -> {
                            UserUtil.saveNickname(baseinfo.nickname)
                            UserUtil.saveAvatar(baseinfo.avatar)
                            UserUtil.saveDescription(baseinfo.description)
                            UserUtil.saveBgImage(baseinfo.bgImage)
                            forwardToMainFragment()
                        }
                        10202 -> {
                            showToast(GlobalUtil.getString(R.string.get_baseinfo_failed_user_not_exist))
                            GifFun.logout()
                            findNavController().popBackStack()
                        }
                        else -> {
                            logWarn(TAG, "Get user baseinfo failed. " + GlobalUtil.getResponseClue(status, baseinfo.msg))
                            showToast(GlobalUtil.getString(R.string.get_baseinfo_failed))
                            GifFun.logout()
                            findNavController().popBackStack()
                        }
                    }
                } else {
                    fragment?.let {
                        if (it.javaClass.name == "club.giffun.app.LoginDialogActivity") {
                            findNavController().popBackStack()
                        }
                    }
                }
            }

            override fun onFailure(e: Exception) {
                logWarn(TAG, e.message, e)
                showToast(GlobalUtil.getString(R.string.get_baseinfo_failed))
                GifFun.logout()
                findNavController().popBackStack()
            }
        })
    }

    protected abstract fun forwardToMainFragment()

    companion object {

        private const val TAG = "AuthActivity"

        /**
         * QQ第三方登录的类型。
         */
        const val TYPE_QQ_LOGIN = 1

        /**
         * 微信第三方登录的类型。
         */
        const val TYPE_WECHAT_LOGIN = 2

        /**
         * 微博第三方登录的类型。
         */
        const val TYPE_WEIBO_LOGIN = 3

        /**
         * 手机号登录的类型。
         */
        const val TYPE_PHONE_LOGIN = 4

        /**
         * 游客登录的类型，此登录只在测试环境下有效，线上环境没有此项功能。
         */
        const val TYPE_GUEST_LOGIN = -1
    }

}