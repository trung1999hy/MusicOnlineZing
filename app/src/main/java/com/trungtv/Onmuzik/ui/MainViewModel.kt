package com.trungtv.Onmuzik.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trungtv.Onmuzik.firebase.Api
import com.trungtv.Onmuzik.firebase.FirebaseRepsonit
import com.trungtv.Onmuzik.model.Data
import com.trungtv.Onmuzik.model.Music
import com.trungtv.Onmuzik.model.Song
import com.trungtv.Onmuzik.model.aaa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainViewModel : ViewModel() {
    private val firebaseRepsonit = FirebaseRepsonit()
    val lisMusicLocal: MutableLiveData<ArrayList<Song>> = MutableLiveData()
    val retrofit = Retrofit.Builder()
        .baseUrl("https://mp3.zing.vn/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    init {
        getAllData()
    }
    fun getAllData() {
        viewModelScope.launch(Dispatchers.Main){
                retrofit.create(Api::class.java).getChartData(0, 0, 0, "song", -1).enqueue(object : Callback<Music>{
                    override fun onResponse(call: Call<Music>, response: Response<Music>) {
                        Log.d(TAG, "onResponse: ${response.body()}")
                        val data = response.body()?.data
                        for (item in data?.song!!){
                            item.path =    "http://api.mp3.zing.vn/api/streaming/audio/${item.id}/320"
                        }
                        firebaseRepsonit.updateSongList(data!!,object : FirebaseRepsonit.Callback<Data>{
                            override fun Success(data: Data) {
                                getMp3Songs()
                            }

                            override fun Err(err: String?) {

                            }

                        })
                    }

                    override fun onFailure(call: Call<Music>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })

           
        }
    }

    fun getMp3Songs() {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepsonit.getSongList(object : FirebaseRepsonit.Callback<Data> {
                override fun Success(data: Data) {
                    lisMusicLocal.postValue(data.song)
                }

                override fun Err(err: String?) {
                    lisMusicLocal.postValue(arrayListOf())
                }
            })
        }
    }

    fun upDateSong(position: Int, song: Song) {
        viewModelScope.launch {
            firebaseRepsonit.upDateSong(position, song)
            getMp3Songs()
        }
    }

}