package com.example.attendanceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class AttendanceHistoryResponse(

	@field:SerializedName("attendance_data")
	val attendanceData: List<AttendanceDataItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class AttendanceDataItem(

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("status")
	val status: String
)
