package com.castcle.ui.common.events

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface ItemClickable<T> {

    val itemClick: Observable<T>

    fun notifyItemClick(data: T)
}

class ItemClickableImpl<T> : ItemClickable<T> {
    private val _itemClick = PublishSubject.create<T>()

    override val itemClick: Observable<T>
        get() = _itemClick

    override fun notifyItemClick(data: T) = _itemClick.onNext(data)
}
