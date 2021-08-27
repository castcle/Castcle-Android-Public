package com.castcle.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castcle.ui.util.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response

abstract class BaseViewModel : ViewModel() {

    private val disposables by lazy { CompositeDisposable() }
    var progressLiveEvent = SingleLiveEvent<Boolean>()
    var errorMessage = SingleLiveEvent<String>()

    inline fun <T> launchAsync(
        crossinline execute: suspend () -> Response<T>,
        crossinline onSuccess: (T) -> Unit,
        showProgress: Boolean = true
    ) {
        viewModelScope.launch {
            if (showProgress)
                progressLiveEvent.value = true
            try {
                val result = execute()
                if (result.isSuccessful)
                    onSuccess(result.body()!!)
                else
                    errorMessage.value = result.message()
            } catch (ex: Exception) {
                errorMessage.value = ex.message
            } finally {
                progressLiveEvent.value = false
            }
        }
    }

    inline fun <T> launchPagingAsync(
        crossinline execute: suspend () -> Flow<T>,
        crossinline onSuccess: (Flow<T>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = execute()
                onSuccess(result)
            } catch (ex: Exception) {
                errorMessage.value = ex.message
            }
        }
    }

    @CallSuper
    public override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    protected fun Disposable.addToDisposables() = addTo(disposables)
}
