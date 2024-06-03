package com.miempresa.lab12_comments

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PostApiService {
    @GET("comments")
    suspend fun getCommentPost(): ArrayList<Comments>

    @GET("comments/{id}")
    suspend fun getCommentPostById(@Path("id") id:String): Response<Comments>
}