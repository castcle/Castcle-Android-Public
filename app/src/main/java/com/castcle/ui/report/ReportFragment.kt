package com.castcle.ui.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentReportBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ReportFragment : BaseFragment<ReportFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentReportBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private val argsBundle: ReportFragmentArgs by navArgs()

    private val profileType: String
        get() = argsBundle.profileType

    private val profileId: String
        get() = argsBundle.id

    private val displayName: String
        get() = argsBundle.displayName

    private val goToProfileFragment: Boolean
        get() = argsBundle.goToProfileFragment

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentReportBinding
        get() = { inflater, container, attachToRoot ->
            FragmentReportBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentReportBinding
        get() = viewBinding as FragmentReportBinding

    override fun viewModel(): ReportFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(ReportFragmentViewModel::class.java)

    override fun initViewModel() {
    }

    override fun setupView() {
        binding.tvDes.text = getString(R.string.report_action, displayName)
        binding.btBlock.text = getString(R.string.report_button, displayName)
        binding.tvFooter.text = getString(R.string.report_footer, displayName, displayName)

        binding.tbProfile.ivToolbarLogoButton
        binding.tbProfile.tvToolbarTitleAction

        binding.btBlock.setOnClickListener {
            viewModel.blockUser(profileId).subscribeBy(
                onComplete = {
                    navigateToProfile(profileId, profileType)
                },
                onError = {
                    displayError(it)
                }
            ).addToDisposables()
        }
        setupToolBar()
    }

    private fun setupToolBar() {
        with(binding.tbProfile) {
            context?.getColorResource(com.castcle.android.R.color.white)?.let {
                tvToolbarTitle.setTextColor(it)
            }

            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().popBackStack()
                }.addToDisposables()

            tvToolbarTitleAction
                .subscribeOnClick {
                    findNavController().popBackStack()
                }.addToDisposables()
        }
    }

    private fun navigateToProfile(castcleId: String, profileType: String) {
        if (goToProfileFragment) {
            findNavController().popBackStack()
            onBoardNavigator.navigateToProfileFragment(castcleId, profileType)
        } else {
            findNavController().popBackStack()
        }
    }

    override fun bindViewEvents() {
    }

    override fun bindViewModel() {
        viewModel.onError.subscribe {
            handleOnError(it)
        }.addToDisposables()

        viewModel.showLoading.subscribe {
            handlerShowLoading(it)
        }.addToDisposables()
    }

    private fun handlerShowLoading(it: Boolean) {
        binding.flCoverLoading.visibleOrGone(it)
    }

    private fun handleOnError(throwable: Throwable) {
        displayError(throwable)
    }

}

