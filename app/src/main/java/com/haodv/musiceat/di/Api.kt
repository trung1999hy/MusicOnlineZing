package com.haodv.musiceat.di

import com.haodv.musiceat.model.Music
import retrofit2.Call
import retrofit2.http.GET

interface Api {
    @GET("chart-realtime")
    fun bxhZing(): Call<Music>
}