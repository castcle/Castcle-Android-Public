package com.castcle.ui.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu.NONE
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.castcle.android.R
import com.castcle.android.databinding.ActivityOnBoardBinding
import com.castcle.android.databinding.LayoutBottomMenuCustomBinding
import com.castcle.data.statickmodel.BottomNavigateStatic
import com.castcle.extensions.*
import com.castcle.ui.base.BaseActivity
import com.castcle.ui.base.ViewBindingContract
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import javax.inject.Inject

class OnBoardActivity : BaseActivity<OnBoardViewModel>(), ViewBindingContract {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private var currentMenuItem = R.id.onboard_nav_graph
    private var currentNavController: LiveData<NavController>? = null

    private val isOnBoardScreen: Boolean
        get() = onBoardNavigator.findNavController().currentDestination?.id ==
            R.id.feedFragment

    override val binding by lazy { ActivityOnBoardBinding.inflate(layoutInflater) }

    override val layoutResource: Int = 0

    override fun beforeLayoutInflated() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBottomNavigation()

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
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

    override fun onBackPressed() {
        if (isOnBoardScreen) {
            // Disable the Back button
            return
        }
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
                    onBoardNavigator.navigateToNotiflyLoginDialogFragment()
                }
            )
            currentNavController = controller
            itemIconTintList = null
            setOnApplyWindowInsetsListener(null)
        }
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
}
