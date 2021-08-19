package com.castcle.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo

abstract class BaseViewModel : ViewModel() {

    private val disposables by lazy { CompositeDisposable() }

    @CallSuper
    public override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    protected fun Disposable.addToDisposables() = addTo(disposables)
}
