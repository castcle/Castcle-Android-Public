package com.castcle.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.castcle.android.R
import com.castcle.android.databinding.ToolbarCastcleCommonBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.di.ActivityViewModelFactory
import com.castcle.di.FragmentViewModelFactory
import com.castcle.extensions.inflate
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

abstract class BaseFragment<VM : ViewModel> : DaggerFragment() {

    @Inject lateinit var viewModelFactory: FragmentViewModelFactory

    @Inject lateinit var activityViewModelFactory: ActivityViewModelFactory

    val viewModel: VM by lazy { viewModel() }

    protected var viewBinding: ViewBinding? = null
    protected var toolbarViewBinding: ViewBinding? = null
    protected var toolbar: Toolbar? = null
    protected open val toolbarLayoutRes: Int? = null
    protected open val layoutRes: Int = 0

    private val disposables = CompositeDisposable()
    private var _baseToolbarBinding: ViewBinding? = null

    abstract fun viewModel(): VM

    val baseToolbarBinding
        get() = when (getToolbar()) {
            TOOLBAR_COMMON -> {
                _baseToolbarBinding!! as ToolbarCastcleCommonBinding
            }
            else -> null
        }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (this as? BaseFragmentCallbacks)?.let { initViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindActionbar(inflater, container)
        return if (this is ViewBindingInflater<*>) {
            bindingInflater.invoke(inflater, container, false)
                .apply { viewBinding = this }
                .root
        } else {
            inflater.inflate(getLayoutResInflate(), container, false)
        }
    }

    private fun bindActionbar(inflater: LayoutInflater, container: ViewGroup?) {
        if (this is ToolbarBindingInflater<*>) {
            toolbarBindingInflater.invoke(inflater, container, false)
                .apply { toolbarViewBinding = this }
                .run(::setupActionBar)
        } else {
            setupActionBar()
        }
    }

    private fun getLayoutResInflate(): Int {
        return if (this is HasLayoutRes) {
            getFragmentLayoutRes()
        } else {
            layoutRes
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (this as? BaseFragmentCallbacks)?.let {
            setupView()
            bindViewEvents()
            bindViewModel()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
        viewBinding = null
        toolbarViewBinding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        _baseToolbarBinding = null
    }

    private fun getToolbar(): Int? {
        return if (this is HasLayoutRes) {
            getFragmentToolbarLayoutRes()
        } else {
            toolbarLayoutRes
        }
    }

    @SuppressLint("RestrictedApi")
    protected fun setupActionBar(toolbarViewBinding: ViewBinding? = null) {
        toolbar = activity?.findViewById(R.id.toolbar)
        val actionBar = (activity as? AppCompatActivity)?.supportActionBar

        actionBar?.apply {
            when {
                toolbarLayoutRes == null && toolbarViewBinding == null -> hide()
                else -> {
                    show()
                    setShowHideAnimationEnabled(false)
                    setDisplayHomeAsUpEnabled(false)
                    setDisplayShowTitleEnabled(false)
                }
            }
        }

        toolbar?.apply {
            if (toolbarViewBinding != null) {
                removeAllViews()
                inflate(toolbarViewBinding)
                return
            }
            getToolbar()
                ?.let {
                    removeAllViews()
                    setBaseToolbarBinding(context)
                    when (it) {
                        TOOLBAR_COMMON_GREETING,
                        TOOLBAR_COMMON -> inflateCommonToolbar()
                        else -> inflate(it)
                    }
                }
        }
    }

    private fun inflateCommonToolbar() {
        toolbar?.apply {
            baseToolbarBinding?.apply {
                inflate(this)

                ivToolbarLogoButton
                    .subscribeOnClick { findNavController().navigateUp() }
                    .addToDisposables()
                if (this@BaseFragment is HasTitleToolbar) {
                    tvToolbarTitle.text = getToolbarTitle()
                } else {
                    tvToolbarTitle.text = findNavController().currentDestination?.label
                }
            }
        }
    }

    private fun setBaseToolbarBinding(context: Context) {
        val container = activity?.findViewById<ViewGroup>(R.id.clOnBoardActivity)

        when (getToolbar()) {
            TOOLBAR_COMMON -> {
                _baseToolbarBinding = ToolbarCastcleCommonBinding.inflate(
                    LayoutInflater.from(context),
                    container,
                    false
                )
            }
            TOOLBAR_COMMON_GREETING -> {
                _baseToolbarBinding = ToolbarCastcleGreetingBinding.inflate(
                    LayoutInflater.from(context),
                    container,
                    false
                )
            }
        }
    }

    protected fun Disposable.addToDisposables() = addTo(disposables)

    companion object {
        const val TOOLBAR_COMMON_GREETING = 1
        const val TOOLBAR_COMMON = -1
    }
}
