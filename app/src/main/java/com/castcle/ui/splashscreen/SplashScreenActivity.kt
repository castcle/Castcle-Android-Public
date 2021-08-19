package com.castcle.ui.splashscreen

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.*
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.databinding.ActivitySplashScreenBinding
import com.castcle.ui.base.BaseActivity
import com.castcle.ui.base.ViewBindingContract
import com.castcle.ui.onboard.OnBoardActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity<SplashScreenViewModel>(), ViewBindingContract {

    override val binding by lazy { ActivitySplashScreenBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            startMainActivity()
        }, DEPLAYED_SCREEN)
    }

    private fun startMainActivity() {
        val intent = Intent(this,OnBoardActivity::class.java)
        startActivity(intent)
    }

    override val layoutResource: Int = 0

    override fun beforeLayoutInflated() {
    }

    override fun viewModel(): SplashScreenViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(SplashScreenViewModel::class.java)
}

private const val DEPLAYED_SCREEN: Long = 3000
