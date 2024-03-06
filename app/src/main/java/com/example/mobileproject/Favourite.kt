package com.example.mobileproject

import java.time.Duration

data class Favourite(
    val songId: Long = 0, // provide default values or make them nullable if necessary
    val songName: String = "",
    val songImgUrl: String = "",
    val artistName: String = "",
    val songUrl: String = "",
    val duration: Int=0
)

