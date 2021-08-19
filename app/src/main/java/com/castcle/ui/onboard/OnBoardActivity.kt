package com.castcle.ui.onboard

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import com.castcle.android.databinding.ActivityOnBoardBinding
import com.castcle.ui.base.BaseActivity
import com.castcle.ui.base.ViewBindingContract

class OnBoardActivity : BaseActivity<OnBoardViewModel>(), ViewBindingContract {

    private lateinit var appBarConfiguration: AppBarConfiguration
    override val binding by lazy { ActivityOnBoardBinding.inflate(layoutInflater) }

    override val layoutResource: Int = 0

    override fun beforeLayoutInflated() {
    }

    override fun viewModel(): OnBoardViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(OnBoardViewModel::class.java)
}