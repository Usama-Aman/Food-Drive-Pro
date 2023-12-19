package com.elementary.thefooddrivepro.edit_profile

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils.substring
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.elementary.thefooddrivepro.MainActivity
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.network.ResponseCallBack
import com.elementary.thefooddrivepro.network.RetrofitClient
import com.elementary.thefooddrivepro.utils.*
import com.elementary.thefooddrivepro.utils.Constants.PROFILE_IMAGE_REQUEST_CODE
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.hbb20.CountryCodePicker
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.lang.Exception


class EditProfileFragment : Fragment(), ResponseCallBack {

    private lateinit var options: Options
    private lateinit var v: View
    private lateinit var toolbarTitle: TextView
    private lateinit var toolbarBtn: TextView
    private lateinit var toolbarLogo: ImageView

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var profileImageView: ImageView
    private lateinit var cameraIcon: ImageView

    private var isFirstName = false
    private var isLastName = false
    private var isPhone = false
    private var isEmail = false
    private var isCheckMatchPassword = false
    private var isNewPassword = true
    private var isRetypeNewPassword = true

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var mobileCodePicker: CountryCodePicker

    private var profileImagePath = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        initViews()

        return v
    }

    private fun initViews() {
        mainLayout = v.findViewById(R.id.mainConstraint)
        mainLayout.setOnClickListener {
            AppUtils.hideKeyboard(activity!!)
        }
        toolbarTitle = activity!!.findViewById(R.id.toolbarTitle)
        toolbarBtn = activity!!.findViewById(R.id.toolbarBtn)
        toolbarLogo = activity!!.findViewById(R.id.toolBarLogo)

        toolbarTitle.text = context!!.resources.getString(R.string.profileScreenTitle)
        toolbarBtn.text = context!!.resources.getString(R.string.profileBtnSave)
        toolbarTitle.visibility = View.VISIBLE
        toolbarBtn.visibility = View.VISIBLE
        toolbarLogo.visibility = View.INVISIBLE

        etFirstName = v.findViewById(R.id.etFirstName)
        etLastName = v.findViewById(R.id.etLastName)
        etPhone = v.findViewById(R.id.etPhone)
        etEmail = v.findViewById(R.id.etEmail)
        mobileCodePicker = v.findViewById(R.id.mobileCodePicker)

        toolbarBtn.setOnClickListener {
            validate()

            if (isFirstName && isPhone && isEmail) {
                checkForOldPassword()

                if (isCheckMatchPassword) {
                    checkForPasswordMatch()
                } else {
                    updateProfile()
                }

            }
        }

        cameraIcon = v.findViewById(R.id.cameraIcon)
        profileImageView = v.findViewById(R.id.editProfileImageView)
        profileImageView.setOnClickListener {
            Pix.start(
                this,
                options
            )
        }

        options = Options.init()
            .setRequestCode(PROFILE_IMAGE_REQUEST_CODE)
            .setCount(1)
            .setFrontfacing(true)
            .setExcludeVideos(true)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("/FoodDrivePro/images")

        showUserData()
    }

    private fun showUserData() {
        val userData = AppUtils.getLoginModel(context!!)

        if (userData.name.contains(' ')) {
            etFirstName.setText(userData.name.substring(0, userData.name.indexOf(' ')))
            etLastName.setText(
                userData.name.substringAfter(
                    ' ', userData.name
                )
            )
        } else
            etFirstName.setText(userData.name)

        etPhone.setText(userData.phone)
        etEmail.setText(userData.email)
        mobileCodePicker.setCountryForPhoneCode(userData.country_code.toInt())

        if (!userData.fullImage.isNullOrBlank())
            Glide.with(context!!).load(userData.fullImage).placeholder(R.drawable.ic_place_holder)
                .into(profileImageView)
    }

    private fun validate() {
        isFirstName = if (etFirstName.text.toString().isNotBlank()) {
            etFirstName.setBackgroundResource(R.drawable.white_text_field_drawable)
            etFirstNameError.visibility = View.INVISIBLE
            true
        } else {
            etFirstName.setBackgroundResource(R.drawable.error_text_field_drawable)
            etFirstNameError.visibility = View.VISIBLE
            false
        }

        isPhone = if (etPhone.text.toString().isNotBlank()) {
            etPhone.setBackgroundResource(R.drawable.white_text_field_drawable)
            etPhoneError.visibility = View.INVISIBLE
            true
        } else {
            etPhone.setBackgroundResource(R.drawable.error_text_field_drawable)
            etPhoneError.visibility = View.VISIBLE
            false
        }

        isEmail = if (AppUtils.validateEmail(etEmail.text.toString())) {
            etEmail.setBackgroundResource(R.drawable.white_text_field_drawable)
            etEmailError.visibility = View.INVISIBLE
            true
        } else {
            etEmail.setBackgroundResource(R.drawable.error_text_field_drawable)
            etEmailError.visibility = View.VISIBLE
            false
        }
    }

    private fun checkForOldPassword() {

        if (etOldPassword.text.isNotBlank()) {
            isCheckMatchPassword = true
            isNewPassword = if (!etNewPassword.text.toString().isBlank()) {
                etNewPassword.setBackgroundResource(R.drawable.white_text_field_drawable)
                etNewPasswordError.visibility = View.INVISIBLE
                true
            } else {
                etNewPassword.setBackgroundResource(R.drawable.error_text_field_drawable)
                etNewPasswordError.text =
                    context!!.resources.getString(R.string.profileErrorNewPassword)
                etNewPasswordError.visibility = View.VISIBLE
                false
            }
            isRetypeNewPassword = if (!etRetypePassword.text.toString().isBlank()) {
                etRetypePassword.setBackgroundResource(R.drawable.white_text_field_drawable)
                etRetypePasswordError.visibility = View.INVISIBLE
                true
            } else {
                etRetypePassword.setBackgroundResource(R.drawable.error_text_field_drawable)
                etRetypePasswordError.text =
                    context!!.resources.getString(R.string.profileErrorRetypeNewPassword)
                etRetypePasswordError.visibility = View.VISIBLE
                false
            }
        } else {

            isCheckMatchPassword = false
            etNewPassword.setBackgroundResource(R.drawable.white_text_field_drawable)
            etNewPasswordError.visibility = View.INVISIBLE
            etRetypePassword.setBackgroundResource(R.drawable.white_text_field_drawable)
            etRetypePasswordError.visibility = View.INVISIBLE

        }

    }

    private fun checkForPasswordMatch() {

        if (etNewPassword.text.toString().isBlank() || etRetypePassword.text.toString().isBlank()) {
            etNewPassword.setBackgroundResource(R.drawable.error_text_field_drawable)
            etRetypePassword.setBackgroundResource(R.drawable.error_text_field_drawable)
            etNewPasswordError.text =
                context!!.resources.getString(R.string.profileErrorPasswordEmpty)
            etRetypePasswordError.text =
                context!!.resources.getString(R.string.profileErrorPasswordEmpty)
            etNewPasswordError.visibility = View.VISIBLE
            etRetypePasswordError.visibility = View.VISIBLE

            return
        } else if (etNewPassword.text.toString() != etRetypePassword.text.toString()) {
            etNewPassword.setBackgroundResource(R.drawable.error_text_field_drawable)
            etRetypePassword.setBackgroundResource(R.drawable.error_text_field_drawable)
            etNewPasswordError.text =
                context!!.resources.getString(R.string.profileErrorPasswordMatch)
            etRetypePasswordError.text =
                context!!.resources.getString(R.string.profileErrorPasswordMatch)
            etNewPasswordError.visibility = View.VISIBLE
            etRetypePasswordError.visibility = View.VISIBLE

            return
        } else {

            etNewPassword.setBackgroundResource(R.drawable.white_text_field_drawable)
            etNewPasswordError.visibility = View.INVISIBLE
            etRetypePassword.setBackgroundResource(R.drawable.white_text_field_drawable)
            etRetypePasswordError.visibility = View.INVISIBLE

            updateProfile()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.PROFILE_IMAGE_REQUEST_CODE -> {
                    context?.let {
                        CropImage.activity(
                            Uri.fromFile(
                                File(
                                    data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.get(0)!!
                                )
                            )
                        )
                            .start(it, this)
                    }
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result =
                        CropImage.getActivityResult(data)
                    if (resultCode == Activity.RESULT_OK) {
                        val resultUri: Uri = result.uri
                        try {

                            val file = File(resultUri.path.toString())
                            Glide.with(context!!).load(file).placeholder(R.drawable.ic_place_holder)
                                .into(profileImageView)

                            profileImageView.setImageURI(resultUri)
                            cameraIcon.visibility = View.GONE

                            profileImagePath = resultUri.path.toString()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        val error = result.error
                    }


                }
            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this, Options.init().setRequestCode(PROFILE_IMAGE_REQUEST_CODE))
                } else {
                    AppUtils.showToast(
                        activity,
                        context!!.resources.getString(R.string.permissionNotGranted),
                        false
                    )
                }
                return
            }
        }
    }

    private fun updateProfile() {
        Loader.showLoader(context!!)

        val profileMultiPartBody = if (profileImagePath != "") {
            val file = File(profileImagePath)
            MultipartBody.Part.createFormData(
                "photo",
                file.name,
                file
                    .asRequestBody("image/*".toMediaTypeOrNull())
            )
        } else null

        val profileFirstName =
            etFirstName.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val profileLastName =
            etLastName.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val profilePhone =
            etPhone.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val profileCountryCode =
            mobileCodePicker.selectedCountryCodeWithPlus.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

        val profileEmail =
            etEmail.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())


        val profileOldPasswrod =
            etOldPassword.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())


        val profileNewPassword =
            etNewPassword.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())


        val profileNewConfirmPassword =
            etRetypePassword.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())


        val call = RetrofitClient.getClient(context!!).updateProfile(
            profileFirstName, profileLastName,
            profileEmail, profileCountryCode, profilePhone,
            profileOldPasswrod, profileNewPassword, profileNewConfirmPassword, profileMultiPartBody
        )
        RetrofitClient.apiCall(call, this, "UpdateProfile", context!!)

    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        Loader.hideLoader()

        SharedPreference.saveSimpleString(
            context!!,
            Constants.userData,
            EncryptDecryptString.encrypt(jsonObject.getJSONObject("data").toString())
        )

        AppUtils.showToast(activity, jsonObject.getString("message"), true)

        (activity as MainActivity).setUpUserData()
        Handler().postDelayed({
            (activity as MainActivity).launchDashBoard()
        }, 600)
    }

    override fun onError(message: String, tag: String) {
        Loader.hideLoader()
        AppUtils.showToast(activity, message, false)
    }


}
