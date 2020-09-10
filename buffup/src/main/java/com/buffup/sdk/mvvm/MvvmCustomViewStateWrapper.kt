package com.buffup.sdk.mvvm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class MvvmCustomViewStateWrapper(
        val superState: Parcelable?,
        val state: MvvmCustomViewState?
): Parcelable