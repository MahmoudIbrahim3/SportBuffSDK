package com.buffup.sdk.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> networkBounceResource(dispatcher: CoroutineDispatcher = Dispatchers.IO,
                                      apiCall: suspend () -> T): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())

        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> {
                    ResultWrapper.NetworkError
                }
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse =
                        convertErrorBody(throwable)

                    ResultWrapper.GenericError(code, errorResponse)
                }
                else -> {
                    ResultWrapper.GenericError(null, null)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): ResponseBody? {
    return try {
        throwable.response()?.errorBody()?.let {
            return throwable.response()?.errorBody()
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
        null
    }
}