package com.quxianggif.common.ui

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.quxianggif.R
import com.quxianggif.core.extension.logWarn
import com.quxianggif.event.ForceToLoginEvent
import com.quxianggif.event.MessageEvent
import com.quxianggif.login.ui.LoginFragment
import com.quxianggif.router.RouterManager
import com.umeng.analytics.MobclickAgent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Author   : zhangwenchao
 * Date     : 2019-12-05  10:12
 * Describe :
 */
open class BaseJetPackFragment: BaseFragment() {

    /**
     * 判断当前Fragment是否在前台。
     */
    protected var isActive: Boolean = false

    /**
     * 当前Fragment的实例。
     */
    protected var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragment = this
        EventBus.getDefault().register(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    override fun onResume() {
        super.onResume()
        isActive = true
    }

    override fun onPause() {
        super.onPause()
        isActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        fragment = null
        EventBus.getDefault().unregister(this)
    }

    protected open fun setupViews() { loading = view?.findViewById(R.id.loading) }

    protected fun setupToolbar() {
        // todo
    }

    /**
     * 隐藏软键盘。
     */
    fun hideSoftKeyboard() = (activity as BaseActivity?)?.hideSoftKeyboard()

    /**
     * 显示软键盘。
     */
    fun showSoftKeyboard(editText: EditText?) = (activity as BaseActivity?)?.showSoftKeyboard(editText)

    fun showProgressDialog(title: String?, message: String) = (activity as BaseActivity?)?.showProgressDialog(title, message)

    fun closeProgressDialog() = (activity as BaseActivity?)?.closeProgressDialog()

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(messageEvent: MessageEvent) {
        if (messageEvent is ForceToLoginEvent) {
            if (isActive) { // 判断Fragment是否在前台，防止非前台的Fragment也处理这个事件，造成打开多个LoginFragment的问题。
                // force to login
                LoginFragment.actionStart(findNavController(), RouterManager.INSTANCE.actionStartLoginNavDirections())
            }
        }
    }

    open fun onBackPressed() = true
}