package com.example.attendanceapp.repository

import android.content.Context
import android.media.FaceDetector.Face
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.attendanceapp.data.pref.UserModel
import com.example.attendanceapp.data.pref.UserPreference
import com.example.attendanceapp.data.remote.response.FaceClassificationResponse
import com.example.attendanceapp.data.remote.response.LoginResponse
import com.example.attendanceapp.data.remote.retrofit.ApiService
import com.example.attendanceapp.utility.uriToFile
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Repository(private val apiService: ApiService, private val userPreference: UserPreference) {
    private val resultFaceClassification = MediatorLiveData<Result<FaceClassificationResponse>>()
    private val resultLoginRequest = MediatorLiveData<Result<LoginResponse>>()


    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun uploadSelfie(context: Context, ImageUri: Uri): LiveData<Result<FaceClassificationResponse>> {
        resultFaceClassification.postValue(Result.Loading)
        ImageUri?.let { uri ->
            val imageFile = uriToFile(uri, context)
            Log.d("Image File", "showImage: ${imageFile.path}")

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestImageFile
            )


            val client = apiService.classifyFace(multipartBody)
            client.enqueue(object : Callback<FaceClassificationResponse> {
                override fun onResponse(
                    call: Call<FaceClassificationResponse>,
                    response: Response<FaceClassificationResponse>
                ) {
                    if (response.isSuccessful) {
                        val classficationResponse = response.body()

                        if (classficationResponse != null) {
                            if (classficationResponse.status == "success") {
                                resultFaceClassification.postValue(Result.Success(classficationResponse))
                            } else {
                                resultFaceClassification.postValue(Result.Error("Unknown Error"))
                            }
                        }

                    } else {
                        resultFaceClassification.postValue(Result.Error("Can't Upload, Check Your Connection"))
                    }
                }

                override fun onFailure(call: Call<FaceClassificationResponse>, t: Throwable) {
                    resultFaceClassification.postValue(Result.Error("Can't Upload, Check Your Connection"))
                }
            })
        } ?: run { resultFaceClassification.postValue(Result.Error("No Image Added")) }

        return resultFaceClassification
    }


    fun loginRequest(userId: String, password: String): LiveData<Result<LoginResponse>> {
        resultLoginRequest.postValue(Result.Loading)

        val client = apiService.login(userId, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        Log.d("LoginRequest", "Login Success: ${loginResponse.message}")
                        resultLoginRequest.postValue(Result.Success(loginResponse))
                    }
                } else {
                    val loginResponse = response.body()
                    Log.d("LoginRequest", "Login Failed: ${loginResponse?.message}")
                    resultLoginRequest.postValue(Result.Error("${loginResponse?.message}"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginRequest", "Error: ${t.message}")
                resultLoginRequest.postValue(Result.Error("Check Your Connection"))
            }
        })
        return resultLoginRequest
    }
}