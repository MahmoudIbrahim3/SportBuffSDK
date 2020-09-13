package com.buffup.sdk.custom

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CustomViewStateWrapper(
        val superState: Parcelable?,
        val state: CustomViewState?
): Parcelable