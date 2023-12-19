package com.elementary.thefooddrivepro.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.auth.models.LoginData
import com.elementary.thefooddrivepro.auth.models.LoginModel
import com.google.gson.Gson
import com.irozon.sneaker.Sneaker
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class AppUtils {

    companion object {

        fun hideKeyboard(context: Activity) {
            val inputManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = context.currentFocus
            if (view != null) {
                inputManager.hideSoftInputFromWindow(view.windowToken, 0)
                view.clearFocus()
            }
        }

        fun validateEmail(email: String): Boolean {
            return !(email.isEmpty() || !isValidEmail(email))
        }

        private fun isValidEmail(email: String): Boolean {
            return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        }

        fun validatePhone(phone: String): Boolean {
            return !(phone.isEmpty() || !isValidPhone(phone));
        }

        fun isValidPhone(phone: String): Boolean {
            return !TextUtils.isEmpty(phone) && android.util.Patterns.PHONE.matcher(phone).matches()
        }

        fun setBackground(view: View, bg: Int) {
            view.setBackgroundResource(bg)
        }

        fun showToast(activity: Activity?, message: String, isSuccess: Boolean) {

            if (isSuccess)
                Sneaker.with(activity!!)
                    .setTitle(
                        activity.resources.getString(R.string.toastSuccessTitle),
                        ContextCompat.getColor(activity.applicationContext, R.color.whiteColor)
                    )
                    .autoHide(true)
                    .setIcon(R.drawable.ic_success_toast, R.color.whiteColor, false)
                    .setTypeface(
                        ResourcesCompat.getFont(
                            activity.applicationContext,
                            R.font.poppins_semibold
                        )!!
                    )
                    .setDuration(1000)
                    .setMessage(message, activity.resources.getColor(R.color.whiteColor))
                    .setHeight(LinearLayout.LayoutParams.WRAP_CONTENT)
                    .sneak(R.color.appGreenColor)
            else
                Sneaker.with(activity!!)
                    .setTitle(
                        activity.resources.getString(R.string.toastErrorTitle),
                        ContextCompat.getColor(activity.applicationContext, R.color.whiteColor)
                    )
                    .autoHide(true)
                    .setIcon(R.drawable.ic_error_toast, R.color.whiteColor, false)
                    .setTypeface(
                        ResourcesCompat.getFont(
                            activity.applicationContext,
                            R.font.poppins_semibold
                        )!!
                    )
                    .setDuration(1000)
                    .setMessage(message, activity.resources.getColor(R.color.whiteColor))
                    .setHeight(LinearLayout.LayoutParams.WRAP_CONTENT)
                    .sneak(R.color.inputFieldBorderErrorColor)
        }

        //        fun createFileMultiPart(imagePath: String, key: String, ext: String): MultipartBody.Part {
//
//            val file = File(imagePath)
//
//            val requestBody = if (ext == ".pdf")
//                file.asRequestBody("application/pdf".toMediaTypeOrNull())
//            else
//                file.asRequestBody("image/*".toMediaTypeOrNull())
//
//            return MultipartBody.Part.createFormData(key, file.name, requestBody)
//        }
//

        fun getLoginModel(context: Context): LoginData {
            return Gson().fromJson(
                EncryptDecryptString.decrypt(
                    SharedPreference.getSimpleString(
                        context,
                        Constants.userData
                    )
                ),
                LoginData::class.java
            )
        }


        fun touchScreenDisable(context: Context) {
            (context as Activity).window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }

        fun touchScreenEnable(context: Context) {
            (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }


        fun getCurrentDateFormated(): String {
            val df = SimpleDateFormat("yyyy-MM-dd hh:mm")
            return df.format(Date())
        }

        fun isOnline(context: Context): Boolean {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return (netInfo != null && netInfo.isConnectedOrConnecting
                    && cm.activeNetworkInfo!!.isAvailable
                    && cm.activeNetworkInfo!!.isConnected)
        }

        fun fromStringToDate(dateStr: String): Date? {

            val format =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            try {
                return format.parse(dateStr)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return null
        }

        fun getCurrentDateTime(): String {
            var cal = Calendar.getInstance()
            var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return sdf.format(cal.time)
        }

        fun isSet(value: String): Boolean {
            return value.isNotEmpty()
        }

        fun getFirstLetterCapital(str: String): String? {
            return if (isSet(str)) str.substring(0, 1).toUpperCase() + str.substring(1)
                .toLowerCase() else ""
        }

        fun getDeviceUniqueId(ctx: Context): String {
            return Settings.Secure.getString(
                ctx?.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }

        fun encryptValue(value: String): String {
            return try {
                EncryptDecryptString.encrypt(value)
            } catch (e: Exception) {
                ""
            }
        }

        fun decryptValue(encryptedValue: String): String {
            return try {
                EncryptDecryptString.decrypt(encryptedValue)
            } catch (e: Exception) {
                ""
            }
        }


    }
}