package com.elementary.thefooddrivepro.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface Api {


    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_id") device_id: String,
        @Field("device_type") device_type: String,
        @Field("app_mode") app_mode: String,
        @Field("device_token") device_token: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("forgot_password")
    fun forgotPassword(
        @Field("email") email: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("otp_verification")
    fun verifyOTP(
        @Field("email") email: String,
        @Field("otp") otp: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("change_password")
    fun resetPassword(
        @Field("email") email: String,
        @Field("new_password") new_password: String,
        @Field("confirm_password") confirm_password: String
    ): Call<ResponseBody>


    @GET("pro_item_donation_list")
    fun getItemDonations(@Query("page") page: Int): Call<ResponseBody>

    @GET("pro_money_donation_list")
    fun getMoneyDonations(@Query("page") page: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("picked_donation")
    fun pickItemDonation(@Field("donation_id") id: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("receive_donation")
    fun receiveMoneyDonation(@Field("donation_id") page: Int): Call<ResponseBody>


    @FormUrlEncoded
    @POST("pro_get_message")
    fun getChatMessages(
        @Field("donation_id") donation_id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("pro_delete_message")
    fun deleteChatMessage(
        @Field("donation_id") donation_id: Int,
        @Field("message_id") message_id: Int
    ): Call<ResponseBody>

    @Multipart
    @POST("pro_send_message")
    fun sendChatMessage(
        @Part("donation_id") donation_id: RequestBody,
        @Part("donation_type") donation_type: RequestBody,
        @Part("message") message: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Call<ResponseBody>


    @Multipart
    @POST("pro_update_profile")
    fun updateProfile(
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("country_code") country_code: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("old_password") old_password: RequestBody,
        @Part("new_password") new_password: RequestBody,
        @Part("confirm_password") confirm_password: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Call<ResponseBody>

    @GET("pro_logout")
    fun logout(): Call<ResponseBody>

}






















