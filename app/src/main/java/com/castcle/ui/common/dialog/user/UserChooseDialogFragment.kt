package com.castcle.ui.common.dialog.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.DialogFragmentUserChooseBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.extensions.getColorResource
import com.castcle.extensions.setNavigationResult
import com.castcle.ui.base.*
import com.castcle.ui.common.dialog.profilechoose.ProfileChooseDialogViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import javax.inject.Inject

class UserChooseDialogFragment : BaseBottomSheetDialogFragment<ProfileChooseDialogViewModel>(),
    BaseFragmentCallbacks, ViewBindingInflater<DialogFragmentUserChooseBinding> {

    @Inject
    lateinit var onBoardNavigator: OnBoardNavigator
    private val args: UserChooseDialogFragmentArgs by navArgs()

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> DialogFragmentUserChooseBinding
        get() = { inflater, container, attachToRoot ->
            DialogFragmentUserChooseBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: DialogFragmentUserChooseBinding
        get() = viewBinding as DialogFragmentUserChooseBinding

    override val layoutRes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetRecastDialogTheme)
    }

    override fun viewModel(): ProfileChooseDialogViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(ProfileChooseDialogViewModel::class.java)

    override fun initViewModel() = Unit


    override fun bindViewModel() {
    }

    override fun setupView() {
        with(binding) {
            tvTitleBlock.text = requireContext().getString(
                R.string.dialog_fragement_user_choose_block
            ).format(
                args.userDisplay
            )
            tvShare.text = requireContext().getString(
                R.string.dialog_fragement_user_choose_share
            ).format(
                args.userDisplay
            )
            tvTitleReport.text = requireContext().getString(
                R.string.dialog_fragement_user_choose_report
            ).format(
                args.userDisplay
            )
        }
        initBottomSheetDialog()
    }

    private fun initBottomSheetDialog() {
        val view: FrameLayout = dialog?.findViewById(R.id.design_bottom_sheet)!!
        view.setBackgroundColor(requireContext().getColorResource(R.color.transparent))
        with(BottomSheetBehavior.from(view)) {
            isFitToContents = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun bindViewEvents() {
        with(binding) {
            clShare.subscribeOnClick {
                handleNavigateResultBack(UserState.SHARE)
            }.addToDisposables()

            clBlock.subscribeOnClick {
                handleNavigateResultBack(UserState.BLOCK)
            }.addToDisposables()

            clReport.subscribeOnClick {
                handleNavigateResultBack(UserState.REPORT)
            }.addToDisposables()
        }
    }


    private fun handleNavigateResultBack(state: UserState) {
        setNavigationResult(onBoardNavigator, KEY_USER_CHOOSE_REQUEST, state)
        onBoardNavigator.findNavController().popBackStack()
    }
}

const val KEY_USER_CHOOSE_REQUEST: String = "USER-CHOOSE-001"
