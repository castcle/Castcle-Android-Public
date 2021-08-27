package com.castcle.components_android.ui.base

import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo

abstract class BaseTemplateAdapter<L : BaseRecyclerViewListener,
    T,
    VH : BaseViewHolder<L, T>>(val listener: OnItemClickListener) :
    RecyclerView.Adapter<VH>(),
    DiffUpdateAdapter {
    protected abstract var uiModels: List<T>

    /**
     * This method will return layout id based on the position
     */
    abstract fun getLayoutId(position: Int): Int

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (position < uiModels.size) {
            holder.onBind(uiModels[position])
        }
    }

    override fun getItemCount(): Int {
        return uiModels.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutId(position)
    }

    protected open fun inflate(@LayoutRes layout: Int, @Nullable parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(
            layout, parent, false
        )
    }
}

fun Disposable.addToDisposables() = addTo(CompositeDisposable())
