package com.elementary.thefooddrivepro.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.network.ResponseCallBack
import com.elementary.thefooddrivepro.network.RetrofitClient
import com.elementary.thefooddrivepro.utils.AppUtils
import com.elementary.thefooddrivepro.utils.BaseActivity
import com.elementary.thefooddrivepro.utils.Loader
import kotlinx.android.synthetic.main.activity_otp.*
import org.json.JSONObject

class OTP : BaseActivity(), ResponseCallBack {

    private lateinit var btnNext: TextView
    private lateinit var btnResendEmail: TextView
    private lateinit var btnBack: RelativeLayout

    private var userEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        setStatusBarColor(this)

        initViews()

    }

    private fun initViews() {
        userEmail = intent.getStringExtra("email")!!
        if (userEmail == null || userEmail == "")
            finish()

        btnResendEmail = findViewById(R.id.btnResendEmail)
        btnNext = findViewById(R.id.tvFPNext)
        btnBack = findViewById(R.id.back)

        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener {
            if (txtPinEntry.text.toString().length == 4) {

                callVerifyOTP()

//                pinView.setLineColor(
//                    ContextCompat.getColor(
//                        this,
//                        R.color.inputFieldBorderErrorColor
//                    )
//                )
                pinViewError.visibility = View.INVISIBLE

            } else {
//                pinView.setLineColor(
//                    ContextCompat.getColor(
//                        this,
//                        R.color.inputFieldBorderColor
//                    )
//                )
                pinViewError.visibility = View.VISIBLE
//                callVerifyOTP()
            }
        }

        btnResendEmail.setOnClickListener {
//            AppUtils.showToa|st(this, resources.getString(R.string.otpErrorEmailSent), true)
            callForgotPasswordApi()
        }

        txtPinEntry.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "")
                    if (s.toString().length == 4)
                        callVerifyOTP()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

    }

    private fun callForgotPasswordApi() {
        Loader.showLoader(this)
        val call = RetrofitClient.getClientNoToken()
            .forgotPassword(userEmail)
        RetrofitClient.apiCall(call, this, "ForgotPassword", this)
    }


    private fun callVerifyOTP() {
        Loader.showLoader(this)
        val call = RetrofitClient.getClientNoToken()
            .verifyOTP(userEmail, txtPinEntry.text.toString())
        RetrofitClient.apiCall(call, this, "ChangePassword", this)
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        Loader.hideLoader()
        AppUtils.showToast(this, jsonObject.getString("message"), true)

        if (tag == "ChangePassword") {
            Handler().postDelayed({
                val intent = Intent(this, ResetPassword::class.java)
                intent.putExtra("email", userEmail)
                startActivity(intent)
                finish()
            }, 800)
        }

    }

    override fun onError(message: String, tag: String) {
        Loader.hideLoader()
        AppUtils.showToast(this, message, false)
    }

}