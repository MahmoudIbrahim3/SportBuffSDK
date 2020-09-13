package com.buffup.sdk.remote

import com.buffup.sdk.entities.BuffsEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BuffsApi {
    @GET("buffs/{buffId}")
    suspend fun getBuffs(
        @Path("buffId") buffId: Int
    ): BuffsEntity
}
