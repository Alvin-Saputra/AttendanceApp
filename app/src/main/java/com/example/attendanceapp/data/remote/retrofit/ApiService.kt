package com.example.attendanceapp.data.remote.retrofit

import com.example.attendanceapp.data.remote.response.FaceClassificationResponse
import com.example.attendanceapp.data.remote.response.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("process-image")
    fun classifyFace(
        @Part image: MultipartBody.Part,
    ): Call<FaceClassificationResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("user_id") userId: String,
        @Field("password") password: String
    ): Call<LoginResponse>
}