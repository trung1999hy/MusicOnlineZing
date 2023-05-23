package com.trungtv.Onmuzik.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trungtv.Onmuzik.firebase.Firebase
import com.trungtv.Onmuzik.model.Data
import com.trungtv.Onmuzik.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private val firebase = Firebase()
    val lisMusicLocal: MutableLiveData<ArrayList<Song>> = MutableLiveData()


    fun getMp3Songs() {
        viewModelScope.launch(Dispatchers.IO) {
            firebase.getSongList(object : Firebase.Callback<Data> {
                override fun Success(data: Data) {
                    lisMusicLocal.postValue(data.song)
                }

                override fun Err(err: String?) {
                    lisMusicLocal.postValue(arrayListOf())
                }
            })
        }
    }

    fun upDateSong(position: Int, song: Song ) {
        viewModelScope.launch {
            firebase.upDateSong(position, song)
            getMp3Songs()
        }
    }

}