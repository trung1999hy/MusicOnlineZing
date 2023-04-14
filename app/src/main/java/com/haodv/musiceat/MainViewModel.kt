package com.haodv.musiceat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haodv.musiceat.firebase.Firebase
import com.haodv.musiceat.model.Data
import com.haodv.musiceat.model.Song
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