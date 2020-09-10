package com.buffup.sdk.custom

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buffup.sdk.BuffsApi
import com.buffup.sdk.entities.BuffsEntity
import com.buffup.sdk.mvvm.MvvmCustomViewModel
import com.buffup.sdk.remote.ResultWrapper
import com.buffup.sdk.remote.RetrofitClientInstance
import com.buffup.sdk.remote.networkBounceResource
import kotlinx.coroutines.*
import java.io.InputStream

class BuffUpViewModel: ViewModel(), MvvmCustomViewModel<CustomViewState> {
    private lateinit var countDownJob: Job
    private val liveData = MutableLiveData<BuffsEntity?>()
    private val countDownTimerLiveData = MutableLiveData<Int>()

    init {
        getBuffs()
    }

    private fun getBuffs() {
        viewModelScope.launch {
            val response = networkBounceResource {
                val buffsApi = RetrofitClientInstance
                    .retrofitInstance?.create(BuffsApi::class.java)

                buffsApi?.getBuffs(1)
            }

            if(response is ResultWrapper.Success)
                liveData.postValue(response.value)
        }
    }

    override var state: CustomViewState? = null
        get() = CustomViewState(liveData.value)
        set(value) {
            field = value
            restore(value)
        }

    fun getLiveData(): LiveData<BuffsEntity?> = liveData

    fun getCountDownTimerLiveData(): LiveData<Int> = countDownTimerLiveData

    private fun restore(state: CustomViewState?) {
        liveData.value = state?.hexCode
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

    fun stopCountDownTimer() {
        countDownJob.cancel()
    }

}