package com.quxianggif.jetmain.feeds.ui

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.quxianggif.R
import com.quxianggif.common.ui.BaseFragment
import com.quxianggif.core.Const
import com.quxianggif.core.extension.logDebug
import com.quxianggif.core.util.GlobalUtil
import com.quxianggif.core.util.SharedUtil
import com.quxianggif.jetmain.databinding.FragmentMainBinding

/**
 * Author   : zhangwenchao
 * Date     : 2019-11-26  15:49
 * Describe :
 */
class MainFragment: BaseFragment() {

    internal var isNeedToRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIsNeedToRefresh()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun checkIsNeedToRefresh() {
        val autoRefresh = PreferenceManager.getDefaultSharedPreferences(activity)
                .getBoolean(GlobalUtil.getString(R.string.key_auto_refresh), true)
        if (autoRefresh) {
            val lastUseTime = SharedUtil.read(Const.Feed.MAIN_LAST_USE_TIME, 0L)
            val timeNotUsed = System.currentTimeMillis() - lastUseTime
            logDebug(TAG, "not used for " + timeNotUsed / 1000 + " seconds")
            if (timeNotUsed > 10 * 60 * 1000) { // 超过10分钟未使用
                isNeedToRefresh = true
            }
        }
    }

    companion object {

        private const val TAG = "MainFragment"

        private const val REQUEST_SEARCH = 10000

        fun actionStart(navController: NavController, directions: NavDirections) = navController.navigate(directions)

    }
}