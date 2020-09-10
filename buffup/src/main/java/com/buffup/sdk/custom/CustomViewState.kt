package com.buffup.sdk.custom

import com.buffup.sdk.entities.BuffsEntity
import com.buffup.sdk.mvvm.MvvmCustomViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomViewState(
        val hexCode: BuffsEntity?
): MvvmCustomViewState