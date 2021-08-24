package com.castcle.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.*
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.databinding.ActivitySplashScreenBinding
import com.castcle.ui.base.BaseActivity
import com.castcle.ui.base.ViewBindingContract
import com.castcle.ui.onboard.OnBoardActivity
import io.reactivex.rxkotlin.subscribeBy

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity<SplashScreenViewModel>(), ViewBindingContract {

    override val binding by lazy { ActivitySplashScreenBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestLogin()
    }

    private fun requestLogin() {
        viewModel.requestGuestLogin()
            .subscribeBy(
                onComplete = {
                    startMainActivity()
                },
                onError = {
                    displayError(it)
                }
            )
            .addToDisposables()
    }

    override val layoutResource: Int = 0

    override fun beforeLayoutInflated() {
    }

    override fun viewModel(): SplashScreenViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(SplashScreenViewModel::class.java)

    private fun startMainActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, OnBoardActivity::class.java)
            startActivity(intent)
        }, DEPLAYED_SCREEN)
    }
}

private const val DEPLAYED_SCREEN: Long = 3000
