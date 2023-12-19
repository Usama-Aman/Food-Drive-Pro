package com.elementary.thefooddrivepro.chat.model

import android.media.MediaPlayer

data class ChatModel(
    val `data`: List<ChatData>,
    val message: String,
    val status: Boolean
)

data class ChatSingleObjectModel(
    val `data`: ChatData,
    val message: String,
    val status: Boolean
)

data class ChatData(
    val added_date_format: String,
    val chat_file: String,
    val chat_id: Int,
    val donation_id: Int,
    val from_id: Int,
    val full_image: String,
    val id: Int,
    val ios_chat_message_id: Int,
    val is_read: Int,
    val message: String?,
    val thumb_image: String,
    val to_id: Int,
    val updated_date_format: String,
    var toDelete: Boolean = false,
    var audioPlaying: Boolean? = false,
    var audioPaused: Boolean? = false,
    var audioProgressBar: Int = 0,
    var audioTotalLength: Int = 0,
    var mediaPlayer: MediaPlayer? = null,
    val `receiver`: Receiver?,
    val sender: Sender?,
    val sender_type: String?
)

data class Sender(
    val about_me: String,
    val address: Any,
    val app_account_type: String,
    val birth_date_format: String,
    val country_code: String,
    val date_of_birth: String,
    val donor_form_json: String,
    val email: String,
    val first_name: String,
    val fullImage: String,
    val full_name: String,
    val id: Int,
    val ios_donor_id: Int,
    val last_login_ip: Any,
    val last_name: String,
    val latitude: String,
    val longitude: String,
    val otp: String,
    val otp_date_time: String,
    val phone_number: String,
    val photo: String,
    val status: String,
    val thumbImage: String,
    val user_type: String
)
data class Receiver(
    val admin_type: String,
    val email: String,
    val full_image: String,
    val id: Int,
    val ios_admin_id: Int,
    val last_login_ip: Any,
    val name: String,
    val password: String,
    val photo: String,
    val status: String,
    val thumb_image: String
)