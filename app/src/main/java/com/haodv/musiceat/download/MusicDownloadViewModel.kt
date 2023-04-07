package com.haodv.musiceat.download

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haodv.musiceat.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MusicDownloadViewModel : ViewModel() {
     val lisMusicLocal: MutableLiveData<ArrayList<Song>> = MutableLiveData()

    @SuppressLint("Range")
    fun getMp3Songs(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            var list: ArrayList<Song> = arrayListOf()
            val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        val id =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                        val title: String =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                        val duration: Int =
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                        val size =
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
                        val artist =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                        val path =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        val songLocal = Song( false, id.toString(), title,null, artist, path)
                        list.add(songLocal)
                    } while (cursor.moveToNext())
                }
                cursor.close()
                withContext(Dispatchers.Main) {
                    lisMusicLocal.postValue(list)
                }

            }
        }
    }
}