package com.quxianggif.jetpack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quxianggif.jetmain.init.ui.SplashFragment
import com.quxianggif.jetpack.databinding.FragmentSplashBinding

/**
 * Author   : zhangwenchao
 * Date     : 2019-11-26  16:18
 * Describe :
 */
class JetPackSplashFragment: SplashFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSplashBinding.inflate(inflater, container, false)
        logoView = binding.logo
        return binding.root
    }
}