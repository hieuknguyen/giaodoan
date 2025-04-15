package com.example.giaodoan.database.api

import android.graphics.Bitmap
import com.example.giaodoan.ui.Apiemail
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @GET("image/{bankId}-{accountNumber}-{format}.png")
    suspend fun getQrImage(
        @Path("bankId") bankId: String,
        @Path("accountNumber") accountNumber: String,
        @Path("format") format: String = "compact2",
        @Query("amount") amount: String? = null,
        @Query("addInfo") additionalInfo: String? = null,
        @Query("accountName") accountName: String? = null
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("email/send")
    suspend fun sendEmail(
        @Field("apikey") apiKey: String,
        @Field("from") from: String,
        @Field("fromName") fromName: String,
        @Field("to") to: String,
        @Field("subject") subject: String,
        @Field("bodyHtml") bodyHtml: String?,
        @Field("bodyText") bodyText: String?
    ): Apiemail
}


