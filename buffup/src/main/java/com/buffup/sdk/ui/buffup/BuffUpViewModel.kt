package com.buffup.sdk.ui.buffup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buffup.sdk.remote.BuffsApi
import com.buffup.sdk.entities.BuffsEntity
import com.buffup.sdk.custom.CustomViewModel
import com.buffup.sdk.remote.ResultWrapper
import com.buffup.sdk.remote.RetrofitClientInstance
import com.buffup.sdk.remote.networkBounceResource
import kotlinx.coroutines.*

class BuffUpViewModel: ViewModel(), CustomViewModel<CustomViewState> {
    private lateinit var countDownJob: Job
    private val buffsLiveData = MutableLiveData<BuffsEntity?>()
    private val countDownTimerLiveData = MutableLiveData<Int>()

    fun getBuffsLiveData() = buffsLiveData
    fun getCountDownTimerLiveData() = countDownTimerLiveData

    init {
        initBuffUps()
    }

    private fun initBuffUps() {
        viewModelScope.launch {
            delay(10000) // Start buff ups after 10 seconds.
            repeat(5) {
                getBuffs(it + 1)
                delay(30000) // A new buff will be shown every 30 seconds
            }
        }
    }

    private fun getBuffs(buffId: Int) {
        viewModelScope.launch {
            val response = networkBounceResource {
                val buffsApi = RetrofitClientInstance
                    .retrofitInstance?.create(BuffsApi::class.java)

                buffsApi?.getBuffs(buffId)
            }

            if(response is ResultWrapper.Success)
                buffsLiveData.postValue(response.value)
        }
    }

    override var state: CustomViewState? = null
        get() = CustomViewState(buffsLiveData.value)
        set(value) {
            field = value
            restore(value)
        }

    private fun restore(state: CustomViewState?) {
        buffsLiveData.value = state?.hexCode
    }

    fun startCountDownTimer(timeToShow: Int) {
        countDownTimerLiveData.value = timeToShow
        countDownJob = viewModelScope.launch {
            repeat(timeToShow) {
                val newTime = getCountDownTimerLiveData().value?.minus(1)
                countDownTimerLiveData.postValue(newTime)
                delay(1000)
            }
        }
    }

    fun stopCountDownTimer() = countDownJob.cancel()
}