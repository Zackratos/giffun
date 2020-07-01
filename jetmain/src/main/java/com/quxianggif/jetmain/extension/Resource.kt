package com.quxianggif.jetmain.extension

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.quxianggif.GifFunApplication
import com.quxianggif.core.GifFun

/**
 * @Author   : zhangwenchao
 * @Date     : 2020/7/1  4:12 PM
 * @email    : zhangwenchao@soulapp.cn
 * @Describe :
 */
@ColorInt
fun getColor(@ColorRes colorRes: Int): Int = ContextCompat.getColor(GifFun.getContext(), colorRes)