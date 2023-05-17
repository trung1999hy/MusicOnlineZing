package com.trungtv.Onmuzik.model

import java.io.Serializable

class Song(
    var play : Boolean = false,
    val id: String? =null,
    val name: String? =null,
    val lyric: String? = null,
    val thumbnail: String? = null,
    val performer: String? = null,
    var like : Boolean = false,
    var path : String ? = null
) : Serializable
