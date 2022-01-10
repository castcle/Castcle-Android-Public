package com.castcle.ui.onboard

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu.NONE
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.castcle.android.R
import com.castcle.android.databinding.ActivityOnBoardBinding
import com.castcle.android.databinding.LayoutBottomMenuCustomBinding
import com.castcle.data.staticmodel.BottomNavigateStatic
import com.castcle.data.staticmodel.LanguageStatic
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.networking.service.interceptor.AppRefreshTokenFailedListener
import com.castcle.networking.service.interceptor.AppTokenExpiredDelegate
import com.castcle.ui.base.BaseActivity
import com.castcle.ui.base.ViewBindingContract
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.profile.RC_CROP_IMAGE
import com.castcle.ui.signin.createaccount.RC_CROP_IMAGE_ACCOUNT
import com.castcle.usecase.OverrideLocaleAppImpl
import com.castcle.usecase.userprofile.StateWorkLoading
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class OnBoardActivity : BaseActivity<OnBoardViewModel>(),
    AppRefreshTokenFailedListener,
    ViewBindingContract {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var overrideLocaleApp: OverrideLocaleAppImpl

    @Inject lateinit var localizedResources: LocalizedResources

    private var currentMenuItem = R.id.onboard_nav_graph
    private var currentNavController: LiveData<NavController>? = null

    private val isOnBoardScreen: Boolean
        get() = onBoardNavigator.findNavController().currentDestination?.id ==
            R.id.feedFragment

    override val binding by lazy { ActivityOnBoardBinding.inflate(layoutInflater) }

    override val layoutResource: Int = 0

    override fun beforeLayoutInflated() {
        initFirebaseToken()
        overrideLocaleApp.execute(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBottomNavigation()

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }

        if (viewModel.currentAppLanguage.value.isNullOrEmpty()) {
            viewModel.setCurrentAppLanguage(LanguageStatic.appLanguage)
        }

        viewModel.onRegisterSuccess.observe(this, {
            setupBottomNavigationBar()
        })

        viewModel.checkCastPostWithImageStatus().subscribe { isState ->
            when (isState) {
                StateWorkLoading.SUCCESS -> {
                    displayMessage(localizedResources.getString(R.string.cast_post_status_success))
                }
                StateWorkLoading.ERROR -> {
                    displayMessage(localizedResources.getString(R.string.cast_post_status_error))
                }
                else -> {}
            }
        }.addToDisposables()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        overrideLocaleApp.execute(this)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    override fun viewModel(): OnBoardViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(OnBoardViewModel::class.java)

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        try {
            onBoardNavigator.handleDeepLink(
                intent,
                intent.getBooleanExtra(EXTRA_SHOULD_POP_STACK_TO_ENTRY, true)
            )
        } catch (e: Exception) {
            // Ignored
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun initBottomNavigation() {
        with(binding.bottomNavView) {
            itemIconTintList = null
            BottomNavigateStatic.bottomMenu.forEachIndexed { index, item ->
                val menuItem = menu.add(
                    NONE,
                    item.navGraphId,
                    index,
                    ""
                )
                setItemIconSizeRes(item.sizeIcon)
                menuItem.setIcon(
                    getDrawableAttribute(
                        item.icon
                    )
                )
                if (index == 0) {
                    currentMenuItem = menuItem.itemId
                }
            }
            customBottomMenu(1)
        }
    }

    private fun setupBottomNavigationBar() {
        with(binding.bottomNavView) {
            itemIconTintList = null
            val navGraphList = BottomNavigateStatic.bottomMenu.map { it.navGraph }
            val controller = setupWithNavController(
                viewModel.isGuestMode,
                navGraphList,
                supportFragmentManager,
                R.id.navHostContainer,
                intent = intent,
                onDestinationChangedListener = { _, destination, _ ->
                    setBottomNavVisibility(destination)
                },
                onNavigateNotiflyLogin = {
                    if (viewModel.isGuestMode) {
                        onBoardNavigator.navigateToNotiflyLoginDialogFragment()
                    } else {
                        onBoardNavigator.navigateCreateBlogFragment()
                    }
                },
                onRefreshPosition = {
                    viewModel.onRefreshPosition()
                }
            )
            currentNavController = controller
            itemIconTintList = null
            setOnApplyWindowInsetsListener(null)

            viewModel.onBackToFeed.subscribe {
                selectedItemId = R.id.onboard_nav_graph
                binding.bottomNavView.visible()
            }.addToDisposables()
        }
    }

    fun onGoneBottomNavigate() {
        binding.bottomNavView.gone()
    }

    private fun setBottomNavVisibility(destination: NavDestination) {
        with(binding) {
            when (destination.id) {
                R.id.webviewFragment,
                R.id.emailFragment,
                R.id.passwordFragment,
                R.id.greetingFragment,
                R.id.verifyEmailFragment,
                R.id.createDisplayProfileFragment,
                R.id.profileChooseFragment,
                R.id.settingFragment,
                R.id.resentVerifyFragment,
                R.id.aboutYouFragment,
                R.id.createBlogFragment,
                R.id.profileFragment,
                R.id.languageAppFragment,
                R.id.languageFragment,
                R.id.createPasswordFragment,
                R.id.changePasswordFragment,
                R.id.completeFragment,
                R.id.feedDetailFragment,
                R.id.greetingPageFragment,
                R.id.dialogChooseFragment,
                R.id.deletePageFragment,
                R.id.profileChooseDialogFragment,
                R.id.notificationFragment,
                R.id.verifyOtpFragment,
                R.id.searchAccountFragment,
                R.id.trendFragment,
                R.id.createAccountFragment,
                R.id.createAccountCompleteFragment,
                R.id.settingProfileFragment,
                R.id.reportFragment,
                R.id.addLinksFragment,
                R.id.viewProfileFragment,
                R.id.loginFragment -> {
                    bottomNavView.gone()
                }
                else -> {
                    bottomNavView.visible()
                }
            }
        }
    }

    private fun customBottomMenu(position: Int) {
        with(binding.bottomNavView) {
            val todayTabView = getChildAt(0) as BottomNavigationMenuView
            val todayTabViewItem = todayTabView.getChildAt(position) as BottomNavigationItemView
            val layoutNotificationBadge = LayoutBottomMenuCustomBinding.inflate(
                LayoutInflater.from(this@OnBoardActivity)
            )
            todayTabViewItem.addView(layoutNotificationBadge.root)
        }
    }

    override fun onRefreshTokenFailed(error: Throwable) {
        onTokenIsExpired()
    }

    private fun onTokenIsExpired() {
        viewModel.onAccessTokenExpired(this).subscribe().addToDisposables()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        AppTokenExpiredDelegate.bindAppTokenExpiredListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppTokenExpiredDelegate.unbindAppTokenExpiredListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_CROP_IMAGE) {
            data?.data?.let { viewModel.setImageResponse(it) }
        }
        if (requestCode == RC_CROP_IMAGE_ACCOUNT) {
            data?.data?.let { viewModel.setImageResponse(it) }
        }
    }

    private fun initFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            try {
                val token = task.result.toString()
                onUpdateFireBaseToken(token)
                Log.i("TOKEN-PUSH", "Push notification token: $token")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance()
                    .setCustomKey(
                        LOG_FIREBASE_TOKEN_FAILED,
                        e.message.toString()
                    )
                e.printStackTrace()
            }
        }
    }

    private fun onUpdateFireBaseToken(token: String) {
        viewModel.updateFireBaseToken(token).subscribe().addToDisposables()
    }

    companion object {
        fun start(context: Context, bundle: Bundle? = null) {
            val intent = Intent(context, OnBoardActivity::class.java)
            bundle?.let { intent.putExtras(it) }
            context.startActivity(intent)
        }

        fun start(context: Context, deepLinkUri: Uri, shouldPopStackToEntry: Boolean = true) {
            val intent = Intent(context, OnBoardActivity::class.java).apply {
                data = deepLinkUri
                putExtra(EXTRA_SHOULD_POP_STACK_TO_ENTRY, shouldPopStackToEntry)
                action = Intent.ACTION_VIEW
            }
            context.startActivity(intent)
        }
    }
}

private const val LOG_FIREBASE_TOKEN_FAILED = "log-firebase-token-failed"
private const val EXTRA_SHOULD_POP_STACK_TO_ENTRY = "shouldPopStackToEntry"
private const val APP_ID = "1:946784514991:android:ac2a54362ffcb0f71ef55a"
private const val PROJECT_ID = "castcle-dev"
private const val PROJECT_NAME = "Castcle-DEV"
