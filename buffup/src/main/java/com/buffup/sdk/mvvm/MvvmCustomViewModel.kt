package com.buffup.sdk.mvvm

interface MvvmCustomViewModel<T: MvvmCustomViewState> {
    var state: T?
}