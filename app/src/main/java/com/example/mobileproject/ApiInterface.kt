package com.example.mobileproject

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
interface ApiInterface {

    @Headers("X-RapidAPI-Key: f3b7dd5ae2mshb7beb7882a3a517p1e4f1fjsn8ed115571b4b",
    "X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com")
    @GET("search")
    fun getData(@Query("q") query: String):Call<MyData>
}