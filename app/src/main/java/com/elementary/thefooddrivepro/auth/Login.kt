package com.elementary.thefooddrivepro.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.elementary.thefooddrivepro.MainActivity
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.auth.models.LoginModel
import com.elementary.thefooddrivepro.network.ResponseCallBack
import com.elementary.thefooddrivepro.network.RetrofitClient
import com.elementary.thefooddrivepro.utils.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class Login : BaseActivity(), ResponseCallBack {

    private lateinit var btnLogin: RelativeLayout
    private lateinit var tvForgotPassword: TextView

    private var isEmail: Boolean = false
    private var isPassword: Boolean = false

    private var deviceId: String = ""
    private var deviceType: String = ""
    private var appMode: String = ""
    private var FCM_TOKEN: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setStatusBarColor(this)

        initViews()

    }

    private fun initViews() {
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }

        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            validate()

            if (isEmail && isPassword) {
                callLoginApi()
            }
        }
    }

    private fun validate() {

        isEmail = if (AppUtils.validateEmail(etEmail.text.toString())) {
            etEmail.setBackgroundResource(R.drawable.white_text_field_drawable)
            etEmailError.visibility = View.INVISIBLE
            true
        } else {
            etEmail.setBackgroundResource(R.drawable.error_text_field_drawable)
            etEmailError.visibility = View.VISIBLE
            false
        }

        isPassword = if (!etPassword.text.toString().isBlank()) {
            etPassword.setBackgroundResource(R.drawable.white_text_field_drawable)
            etPasswordError.visibility = View.INVISIBLE
            true
        } else {
            etPassword.setBackgroundResource(R.drawable.error_text_field_drawable)
            etPasswordError.visibility = View.VISIBLE
            false
        }

    }

    private fun callLoginApi() {
        Loader.showLoader(this)


        deviceId = AppUtils.getDeviceUniqueId(this)
        deviceType = "android"
        appMode = "development"

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                FCM_TOKEN = task.result?.token

                val call = RetrofitClient.getClientNoToken()
                    .login(
                        etEmail.text.toString(), etPassword.text.toString(),
                        deviceId, deviceType, appMode, FCM_TOKEN!!
                    )
                RetrofitClient.apiCall(call, this, "Login", this)
            }
        }
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        Loader.hideLoader()
        AppUtils.showToast(this, jsonObject.getString("message"), true)

        val gson = Gson()
        val model = gson.fromJson(jsonObject.toString(), LoginModel::class.java)

        SharedPreference.saveBoolean(this, Constants.IsLoggedIn, true)

        SharedPreference.saveSimpleString(
            this,
            Constants.accessToken,
            EncryptDecryptString.encrypt(model.data.token)
        )

        SharedPreference.saveSimpleString(
            this,
            Constants.userData,
            EncryptDecryptString.encrypt(jsonObject.getJSONObject("data").toString())
        )

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 800)

    }

    override fun onError(message: String, tag: String) {
        Loader.hideLoader()
        AppUtils.showToast(this, message, false)
    }

}