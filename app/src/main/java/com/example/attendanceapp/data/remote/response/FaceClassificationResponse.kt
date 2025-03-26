package com.example.attendanceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class FaceClassificationResponse(

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("username")
	val username: String
)
