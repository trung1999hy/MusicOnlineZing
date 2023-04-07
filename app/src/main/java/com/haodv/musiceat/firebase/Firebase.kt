package com.haodv.musiceat.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.haodv.musiceat.model.Data

class Firebase {
    var database = FirebaseDatabase.getInstance("https://musikloud-5ebb7-default-rtdb.asia-southeast1.firebasedatabase.app/")
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
    fun updateSongList(data: Data, callback: Callback<Data>){
        myRef.child("bxh").setValue(data).addOnCompleteListener {
            callback.Success(data)
        }.addOnFailureListener {
            callback.Err(it.message)
        }
    }

    interface Callback<T> {
        fun Success(data: T)
        fun Err(err: String?)
    }
}