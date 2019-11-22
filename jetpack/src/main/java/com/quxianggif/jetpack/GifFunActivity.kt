package com.quxianggif.jetpack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.quxianggif.common.ui.BaseActivity
import com.quxianggif.common.ui.BaseJetPackFragment
import com.quxianggif.jetpack.databinding.ActivityGiffunBinding
import kotlinx.android.synthetic.main.activity_giffun.*

/**
 * Author   : zhangwenchao
 * Date     : 2019-11-23  19:32
 * Describe :
 */
class GifFunActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityGiffunBinding>(this, R.layout.activity_giffun)
    }

//    override fun onBackPressed() {
//        val f = currentFragment
//        if (f !is BaseJetPackFragment || f.onBackPressed()) {
//            if (!findNavController(R.id.nav_host).navigateUp()) {
//                super.onBackPressed()
//            }
//        }
//    }


    private val currentFragment: Fragment?
        get() = nav_host.childFragmentManager.findFragmentById(R.id.nav_host)
}