package com.elementary.thefooddrivepro.network

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.utils.*
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit


object RetrofitClient {


    private const val BASE_URL = "http://elxdrive.com/the_food_drive/api/v1/"

    fun getClient(context: Context): Api {

        val token = EncryptDecryptString.decrypt(
            SharedPreference.getSimpleString(
                context,
                Constants.accessToken
            )
        )

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader(
                        "Authorization",
                        "Bearer $token"
                    )
                    .build()
                chain.proceed(newRequest)
            }.build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(Api::class.java)
    }

    fun getClientNoToken(): Api {

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(newRequest)
            }.build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(Api::class.java)

    }

    fun apiCall(
        call: Call<ResponseBody>,
        responseCallBack: ResponseCallBack,
        tag: String,
        context: Context
    ) {
        if (!AppUtils.isOnline(context)) {
            AppUtils.showToast(
                context as AppCompatActivity,
                context.resources.getString(R.string.no_internet), false
            )
        } else {

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val jsonObject = JSONObject(response.body()!!.string())
                            if (jsonObject.getBoolean("status"))
                                responseCallBack.onSuccess(jsonObject, tag)
                            else
                                responseCallBack.onError(jsonObject.getString("message"), tag)
                        } else {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            responseCallBack.onError(jsonObject.getString("message"), tag)
                        }

                    } catch (e: Exception) {
                        Loader.hideLoader()
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Response Failure", t.localizedMessage!!)
                    responseCallBack.onError(t.localizedMessage!!, tag)
                }

            })
        }
    }

}