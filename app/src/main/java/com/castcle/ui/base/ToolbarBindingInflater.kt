package com.castcle.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

interface ToolbarBindingInflater<VB : ViewBinding> {

    val toolbarBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    val toolbarBinding: VB
}
