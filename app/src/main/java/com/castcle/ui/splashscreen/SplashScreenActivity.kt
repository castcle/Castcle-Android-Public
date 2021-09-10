package com.castcle.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        this.intent = intent
        val deepLink = intent.extras?.getString(EXTRA_URI) ?: ""
        when {
            deepLink.isNotEmpty() -> {
                val uri = Uri.parse(deepLink)
                val launchIntent = Intent(Intent.ACTION_VIEW, uri)
                intent.extras?.let { launchIntent.putExtras(it) }
                launchIntent.resolveActivity(packageManager)?.let {
                    startActivity(launchIntent)
                    finish()
                } ?: finish()
            }
        }
    }

    override fun viewModel(): SplashScreenViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(SplashScreenViewModel::class.java)

    private fun startMainActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            OnBoardActivity.start(this)
            finish()
        }, DEPLAYED_SCREEN)
    }

    companion object {
        fun start(context: Context, bundle: Bundle? = null) {
            val intent = Intent(context, SplashScreenActivity::class.java)
            bundle?.let { intent.putExtras(it) }
            context.startActivity(intent)
        }
    }
}

private const val DEPLAYED_SCREEN: Long = 3000
internal const val EXTRA_URI = "uri"