package com.castcle.components_android.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<L : BaseRecyclerViewListener?, T> :
    RecyclerView.ViewHolder {
    protected var listener: L? = null

    constructor(itemView: View?) : super(itemView!!)
    constructor(
        itemView: View?,
        listener: L
    ) : super(itemView!!) {
        this.listener = listener
    }

    abstract fun onBind(item: T)
}
