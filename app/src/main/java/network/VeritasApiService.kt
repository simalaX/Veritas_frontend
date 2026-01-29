package com.example.veritas.network

import com.veritasgenerationministry.model.MediaFile
import com.veritasgenerationministry.model.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface VeritasApiService {

    // Fetch library content with optional search and category filtering
    @GET("content")
    suspend fun getContent(
        @Query("q") query: String? = null,
        @Query("category") category: String? = null
    ): List<MediaFile>

    // Admin: Upload new content
    @Multipart
    @POST("admin/upload")
    suspend fun uploadContent(
        @Part("title") title: RequestBody,
        @Part("category") category: RequestBody,
        @Part file: MultipartBody.Part,
        @Header("Authorization") token: String // Bearer <FirebaseToken>
    ): UploadResponse

    // Admin: Edit existing content
    @FormUrlEncoded
    @PATCH("content/{item_id}")
    suspend fun updateContent(
        @Path("item_id") itemId: Int,
        @Field("title") title: String?,
        @Field("category") category: String?,
        @Header("Authorization") token: String
    ): Response<Unit>

    // Admin: Delete content
    @DELETE("content/{item_id}")
    suspend fun deleteContent(
        @Path("item_id") itemId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>
}