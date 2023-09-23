package com.trungtv.Onmuzik.firebase

import com.trungtv.Onmuzik.model.Music
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("xhr/chart-realtime")
    fun getChartData(
        @Query("songId") songId: Int,
        @Query("videoId") videoId: Int,
        @Query("albumId") albumId: Int,
        @Query("chart") chart: String,
        @Query("time") time: Int
    ): Call<Music>


}