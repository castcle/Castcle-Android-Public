package com.castcle.extensions

import android.graphics.Rect
import android.os.Parcelable
import android.util.SparseArray
import android.view.*
import androidx.annotation.LayoutRes
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.viewbinding.ViewBinding

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, position: Int = -1): View {
    return LayoutInflater.from(context)
        .inflate(layoutRes, this, false)
        .apply { addView(this, position) }
}

fun ViewGroup.inflate(viewBinding: ViewBinding, position: Int = -1): View {
    return viewBinding.root.apply {
        addView(this, position)
    }
}

fun ViewGroup.enableViews(isEnabled: Boolean) {
    this.isEnabled = isEnabled
    forEach { view ->
        if (view is ViewGroup) view.enableViews(isEnabled)
        else view.isEnabled = isEnabled
    }
}

fun ViewGroup.getProminentView(parentView: ViewGroup): View? {
    val largeContentView = ArrayList<View>()
    val fullyVisibleView = ArrayList<View>()
    val partialVisibleTopView = ArrayList<View>()
    val partialVisibleBottomView = ArrayList<View>()

    val parentViewBounds = Rect()
    parentView.getDrawingRect(parentViewBounds)
    forEach { childView ->
        val top = childView.y
        val bottom = top + childView.height
        when {
            top < parentViewBounds.top && bottom > parentViewBounds.bottom -> {
                largeContentView.add(childView)
            }
            top > parentViewBounds.top && bottom < parentViewBounds.bottom -> {
                fullyVisibleView.add(childView)
            }
            top > parentViewBounds.top && top < parentViewBounds.bottom
                && bottom > parentViewBounds.bottom -> {
                partialVisibleTopView.add(childView)
            }
            top < parentViewBounds.top && bottom < parentViewBounds.bottom
                && bottom > parentViewBounds.top -> {
                partialVisibleBottomView.add(childView)
            }
        }
    }
    return when {
        largeContentView.isNotEmpty() -> largeContentView[DEFAULT_VISIBLE_ITEM_POSITION]
        fullyVisibleView.isNotEmpty() -> fullyVisibleView[DEFAULT_VISIBLE_ITEM_POSITION]
        partialVisibleTopView.isNotEmpty() -> partialVisibleTopView[DEFAULT_VISIBLE_ITEM_POSITION]
        partialVisibleBottomView.isNotEmpty() ->
            partialVisibleBottomView[DEFAULT_VISIBLE_ITEM_POSITION]
        else -> null
    }
}

fun ViewGroup.saveChildViewStates(): SparseArray<Parcelable> {
    val childViewStates = SparseArray<Parcelable>()
    children.forEach { child -> child.saveHierarchyState(childViewStates) }
    return childViewStates
}

fun ViewGroup.restoreChildViewStates(childViewStates: SparseArray<Parcelable>) {
    children.forEach { child -> child.restoreHierarchyState(childViewStates) }
}

fun ViewGroup.getLayoutInflater(): LayoutInflater = LayoutInflater.from(context)

private const val DEFAULT_VISIBLE_ITEM_POSITION = 0
