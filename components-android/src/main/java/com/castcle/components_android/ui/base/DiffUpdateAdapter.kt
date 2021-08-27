package com.castcle.components_android.ui.base

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

interface DiffUpdateAdapter {

    fun <T> RecyclerView.Adapter<*>.autoNotify(
        oldList: List<T>,
        newList: List<T>,
        compareItem: (T, T) -> Boolean,
        compareContent: ((T, T) -> Boolean)? = null
    ) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return compareItem(oldList[oldItemPosition], newList[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return if (compareContent != null) {
                    compareContent(oldList[oldItemPosition], newList[newItemPosition])
                } else {
                    oldList[oldItemPosition] == newList[newItemPosition]
                }
            }

            override fun getOldListSize() = oldList.size

            override fun getNewListSize() = newList.size
        })

        diff.dispatchUpdatesTo(this)
    }
}
