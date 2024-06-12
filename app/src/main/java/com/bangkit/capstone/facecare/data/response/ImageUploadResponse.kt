package com.bangkit.capstone.facecare.data.response

import com.google.gson.annotations.SerializedName

data class ImageUploadResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Thumb(

	@field:SerializedName("extension")
	val extension: String? = null,

	@field:SerializedName("filename")
	val filename: String? = null,

	@field:SerializedName("mime")
	val mime: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class Data(

	@field:SerializedName("display_url")
	val displayUrl: String? = null,

	@field:SerializedName("image")
	val image: Image? = null,

	@field:SerializedName("thumb")
	val thumb: Thumb? = null,

	@field:SerializedName("delete_url")
	val deleteUrl: String? = null,

	@field:SerializedName("medium")
	val medium: Medium? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("url_viewer")
	val urlViewer: String? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("size")
	val size: String? = null,

	@field:SerializedName("width")
	val width: String? = null,

	@field:SerializedName("expiration")
	val expiration: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("height")
	val height: String? = null
)

data class Image(

	@field:SerializedName("extension")
	val extension: String? = null,

	@field:SerializedName("filename")
	val filename: String? = null,

	@field:SerializedName("mime")
	val mime: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class Medium(

	@field:SerializedName("extension")
	val extension: String? = null,

	@field:SerializedName("filename")
	val filename: String? = null,

	@field:SerializedName("mime")
	val mime: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)
