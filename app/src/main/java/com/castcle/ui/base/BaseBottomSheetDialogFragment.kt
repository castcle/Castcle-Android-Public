package com.castcle.ui.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.viewbinding.ViewBinding
import com.castcle.data.error.userReadableMessage
import com.castcle.di.FragmentViewModelFactory
import com.castcle.ui.util.Toaster
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

abstract class BaseBottomSheetDialogFragment<VM : BaseViewModel> : BottomSheetDialogFragment() {

    @Inject lateinit var viewModelFactory: FragmentViewModelFactory

    @Inject lateinit var toaster: Toaster

    protected val viewModel: VM by lazy { viewModel() }

    protected var viewBinding: ViewBinding? = null
    private val disposables = CompositeDisposable()

    protected abstract val layoutRes: Int

    abstract fun viewModel(): VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (this is ViewBindingInflater<*>) {
            bindingInflater.invoke(inflater, container, false)
                .apply { viewBinding = this }
                .root
        } else {
            inflater.inflate(layoutRes, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        (this as? BaseFragmentCallbacks)?.let {
            setupView()
            bindViewEvents()
            bindViewModel()
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    protected fun Disposable.addToDisposables() = addTo(disposables)

    protected fun displayError(error: Throwable) {
        val message = error.userReadableMessage(requireContext())
        toaster.display(message)
    }
}
