package com.buffup.sdk.custom

import androidx.lifecycle.LifecycleOwner

interface CustomView<V: CustomViewState, T: CustomViewModel<V>> {
    val viewModel: T

    fun onLifecycleOwnerAttached(lifecycleOwner: LifecycleOwner)
}