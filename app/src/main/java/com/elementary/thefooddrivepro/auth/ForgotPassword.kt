package com.elementary.thefooddrivepro.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.network.ResponseCallBack
import com.elementary.thefooddrivepro.network.RetrofitClient
import com.elementary.thefooddrivepro.utils.*
import kotlinx.android.synthetic.main.activity_forgot_password.etEmail
import kotlinx.android.synthetic.main.activity_forgot_password.etEmailError
import org.json.JSONObject

class ForgotPassword : BaseActivity(), ResponseCallBack {

    private lateinit var btnNext: TextView
    private lateinit var btnBack: RelativeLayout
    private lateinit var etEmail : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setStatusBarColor(this)

        initViews()

    }

    private fun initViews() {
        btnNext = findViewById(R.id.tvFPNext)
        btnBack = findViewById(R.id.back)
        etEmail = findViewById(R.id.etEmail)

        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener {

            if (AppUtils.validateEmail(etEmail.text.toString())) {
                etEmail.setBackgroundResource(R.drawable.white_text_field_drawable)
                etEmailError.visibility = View.INVISIBLE
                callForgotPasswordApi()

            } else {
                etEmail.setBackgroundResource(R.drawable.error_text_field_drawable)
                etEmailError.visibility = View.VISIBLE
            }

        }
    }

    private fun callForgotPasswordApi() {
        Loader.showLoader(this)
        val call = RetrofitClient.getClientNoToken()
            .forgotPassword(etEmail.text.toString())
        RetrofitClient.apiCall(call, this, "ForgotPassword",this)
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        Loader.hideLoader()
        AppUtils.showToast(this, jsonObject.getString("message"), true)

        Handler().postDelayed({
            val intent = Intent(this, OTP::class.java)
            intent.putExtra("email", etEmail.text.toString())
            startActivity(intent)
            finish()
        }, 800)

    }

    override fun onError(message: String, tag: String) {
        Loader.hideLoader()
        AppUtils.showToast(this, message, false)
    }

}