package com.trungtv.Onmuzik.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.trungtv.Onmuzik.model.Data
import com.trungtv.Onmuzik.model.Song

class Firebase {
    var database =
        FirebaseDatabase.getInstance("https://music-8c982-default-rtdb.asia-southeast1.firebasedatabase.app/")
    var myRef = database.getReference("Music")

    fun getSongList(callback: Callback<Data>) {
        myRef.child("bxh").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val song: Data? = snapshot.getValue(Data::class.java)
                song?.let { callback.Success(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                callback.Err(error.message)
            }
        })
    }

    fun updateSongList(data: Data, callback: Callback<Data>) {
        myRef.child("bxh").setValue(data).addOnCompleteListener {
            callback.Success(data)
        }.addOnFailureListener {
            callback.Err(it.message)
        }
    }

    fun upDateSong(position: Int, song: Song ) {
        myRef.child("bxh").child("song").child(position.toString()).setValue(song)


    }

    interface Callback<T> {
        fun Success(data: T)
        fun Err(err: String?)
    }
}