package com.buffup.sdk.custom

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner

abstract class CustomConstraintLayout<V: CustomViewState, T: CustomViewModel<V>>(
        context: Context,
        attributeSet: AttributeSet
): ConstraintLayout(context, attributeSet), CustomView<V, T> {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lifecycleOwner = context as? LifecycleOwner ?: throw LifecycleOwnerNotFoundException()
        onLifecycleOwnerAttached(lifecycleOwner)
    }

    override fun onSaveInstanceState() = CustomViewStateWrapper(
        super.onSaveInstanceState(), viewModel.state)

    @Suppress("UNCHECKED_CAST")
    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is CustomViewStateWrapper) {
            viewModel.state = state.state as V?
            super.onRestoreInstanceState(state.superState)
        }
    }
}