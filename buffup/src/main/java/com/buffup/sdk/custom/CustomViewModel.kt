package com.buffup.sdk.custom

interface CustomViewModel<T: CustomViewState> {
    var state: T?
}