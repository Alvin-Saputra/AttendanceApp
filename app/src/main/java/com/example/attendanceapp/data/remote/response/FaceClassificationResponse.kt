package com.example.attendanceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class FaceClassificationResponse(

	@field:SerializedName("prediction")
	val prediction: Int,

	@field:SerializedName("status")
	val status: String
)
