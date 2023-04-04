package com.haodv.musiceat.di;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {
    public Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://mp3.zing.vn/xhr/")  .addConverterFactory(GsonConverterFactory.create()).build();

    }
}

