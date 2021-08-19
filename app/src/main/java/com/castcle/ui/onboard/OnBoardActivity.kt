package com.castcle.ui.onboard

import android.os.Bundle
import android.view.Menu.NONE
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.castcle.android.R.id
import com.castcle.android.databinding.ActivityOnBoardBinding
import com.castcle.data.statickmodel.BottomNavigateStatic
import com.castcle.extensions.getDrawableAttribute
import com.castcle.ui.base.BaseActivity
import com.castcle.ui.base.ViewBindingContract
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import javax.inject.Inject

class OnBoardActivity : BaseActivity<OnBoardViewModel>(), ViewBindingContract {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var currentMenuItem = id.onboard_nav_graph

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    override val binding by lazy { ActivityOnBoardBinding.inflate(layoutInflater) }

    override val layoutResource: Int = 0

    override fun beforeLayoutInflated() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBottomNavigation()
    }

    override fun viewModel(): OnBoardViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(OnBoardViewModel::class.java)

    private fun initBottomNavigation() {
        with(binding.bottomNavView) {
            BottomNavigateStatic.bottomMenu.forEachIndexed { index, item ->
                val menuItem = menu.add(
                    NONE,
                    item.navGraph,
                    index,
                    ""
                )
                menuItem.setIcon(
                    getDrawableAttribute(
                        item.icon
                    )
                )

                if (index == 0) {
                    currentMenuItem = menuItem.itemId
                }
            }
        }
    }
}