package com.veritasgenerationministry.model

import com.google.gson.annotations.SerializedName

private val gson: Any
    get() {
        TODO()
    }

data class MediaFile(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("type") val type: String, // PDF, AUDIO, IMAGE
    @SerializedName("url") val url: String,
    @SerializedName("category") val category: String,
    @SerializedName("uploaded_by") val uploadedBy: String
)

data class UploadResponse(
    val message: String,
    val file_id: String
)