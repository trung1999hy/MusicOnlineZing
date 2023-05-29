package com.trungtv.Onmuzik.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trungtv.Onmuzik.firebase.FirebaseRepsonit
import com.trungtv.Onmuzik.model.Data
import com.trungtv.Onmuzik.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private val firebaseRepsonit = FirebaseRepsonit()
    val lisMusicLocal: MutableLiveData<ArrayList<Song>> = MutableLiveData()


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

    fun upDateSong(position: Int, song: Song ) {
        viewModelScope.launch {
            firebaseRepsonit.upDateSong(position, song)
            getMp3Songs()
        }
    }

}