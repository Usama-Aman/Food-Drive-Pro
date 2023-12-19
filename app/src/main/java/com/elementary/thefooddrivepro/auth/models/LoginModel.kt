package com.elementary.thefooddrivepro.auth.models

data class LoginModel(
    val `data`: LoginData,
    val message: String,
    val status: Boolean
)

data class LoginData(
    var admin_type: String = "",
    var email: String = "",
    var fullImage: String = "",
    var id: Int = 0,
    var ios_admin_id: Int = 0,
    var name: String = "",
    var photo: String = "",
    var thumbImage: String = "",
    var token: String = "",
    var last_name: String = "",
    var phone: String = "",
    var country_code: String = ""
)