package com.kedaireka.monitoringkjabb.utils.retrofitApi
import retrofit2.http.GET

interface Api {
    @GET("data")
    fun getPosts(): retrofit2.Call<ArrayList<PostResponse>>
}