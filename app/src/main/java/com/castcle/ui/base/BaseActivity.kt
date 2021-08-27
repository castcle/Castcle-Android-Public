package com.castcle.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import com.castcle.android.R
import com.castcle.data.error.userReadableMessage
import com.castcle.data.storage.AppPreferences
import com.castcle.di.ActivityViewModelFactory
import com.castcle.ui.base.Injectable.Companion.mockInjector
import com.castcle.ui.base.Injectable.Companion.realInjector
import com.castcle.ui.util.Toaster
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewModel> : DaggerAppCompatActivity(), Injectable {

    private val disposables = CompositeDisposable()

    @Inject lateinit var viewModelFactory: ActivityViewModelFactory
    @Inject lateinit var appPreferences: AppPreferences
    @Inject lateinit var toaster: Toaster

    protected val viewModel: VM by lazy { viewModel() }

    @get:LayoutRes
    protected abstract val layoutResource: Int

    abstract fun beforeLayoutInflated()

    abstract fun viewModel(): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.CastcleTheme)
        beforeLayoutInflated()

        when (this) {
            is ViewBindingContract -> {
                setContentView(binding.root)
            }
            else -> {
                setContentView(layoutResource)
            }
        }

        setupActionBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    private fun setupActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.let {
            setSupportActionBar(it)
        }
    }

    protected fun Disposable.addToDisposables() = addTo(disposables)

    override fun androidInjector(): AndroidInjector<Any> {
        realInjector = super.androidInjector()
        return mockInjector ?: realInjector
    }

    fun displayError(error: Throwable) {
        val message = error.userReadableMessage(this)
        toaster.display(message)
    }
}
