package com.buffup.sdk.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BuffsEntity (
	val result : Result
): Parcelable

@Parcelize
data class Result (
	val id : Int,
	val client_id : Int,
	val stream_id : Int,
	val time_to_show : Int,
	val priority : Int,
	val created_at : String,
	val author : Author,
	val question : Question,
	val answers : List<Answers>,
	val language : String
): Parcelable

@Parcelize
data class Author (
	val first_name : String,
	val last_name : String,
	val image : String
): Parcelable

@Parcelize
data class Question (
	val id : Int,
	val title : String,
	val category : Int
): Parcelable

@Parcelize
data class Answers (
	val id : Int,
	val buff_id : Int,
	val title : String,
	val image : Images
): Parcelable

@Parcelize
data class Images (
	@SerializedName("0") val image0 : Image,
	@SerializedName("1") val image1 : Image,
	@SerializedName("2") val image2 : Image
): Parcelable

@Parcelize
data class Image (
	val id : String,
	val key : String,
	val url : String
): Parcelable
