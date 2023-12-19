package com.elementary.thefooddrivepro.chat

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elementary.thefooddrivepro.MainActivity
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.chat.model.*
import com.elementary.thefooddrivepro.network.ResponseCallBack
import com.elementary.thefooddrivepro.network.RetrofitClient
import com.elementary.thefooddrivepro.utils.AppUtils
import com.elementary.thefooddrivepro.utils.Constants
import com.elementary.thefooddrivepro.utils.Loader
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiPopup
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_donation_detail.*
import kotlinx.android.synthetic.main.layout_remove_chat.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

class ChatFragment : Fragment(), View.OnClickListener, ChatAdapter.OnPostItemClick,
    ResponseCallBack {

    companion object {
        var isChatActive = false
        var donationId = -1
    }

    private var whichTab: Int = -1
    private var positionToRemove: Int = -1
    private var donationType: String = ""
    private lateinit var toolbarTitle: TextView
    private lateinit var toolbarBtn: TextView
    private lateinit var toolbarLogo: ImageView

    private lateinit var rootView: RelativeLayout
    private lateinit var etMessage: EmojiEditText
    private lateinit var btEmoji: ImageView
    private lateinit var btSend: ImageView
    private lateinit var rlSendMsg: RelativeLayout
    private lateinit var rlBody: RelativeLayout
    private lateinit var rlGalley: RelativeLayout
    private lateinit var rvMessages: RecyclerView

    var options: Options? = null
    private var messageList: MutableList<ChatData> = ArrayList()
    private var messagesAdapter: ChatAdapter? = null
    var mLayoutManager: LinearLayoutManager? = null
    private lateinit var activityParent: AppCompatActivity

    var isShowingDelete = false
    var posRemove = 0

    private lateinit var v: View

    private val chatBroadCastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                if (intent != null) {
                    if (intent.hasExtra("messageData")) {
                        val jsonObject = JSONObject(intent.getStringExtra("messageData")!!)

                        val chatData = ChatData(
                            jsonObject.getString("added_date_format"),
                            jsonObject.getString("chat_file"),
                            jsonObject.getInt("chat_id"),
                            jsonObject.getInt("chat_id"),
                            jsonObject.getInt("from_id"),
                            jsonObject.getString("full_image"),
                            jsonObject.getInt("id"),
                            jsonObject.getInt("ios_chat_message_id"),
                            jsonObject.getInt("is_read"),
                            jsonObject.getString("message"),
                            jsonObject.getString("thumb_image"),
                            jsonObject.getInt("to_id"),
                            jsonObject.getString("updated_date_format"),
                            false, false, false, 0,
                            0, null, null, Sender(
                                "", "", "", "", "",
                                "", "", "", "", intent.getStringExtra("fullImage")!!,
                                "", -1, -1, "", "",
                                "", "", "", "", "",
                                "", "", "", ""
                            ), jsonObject.getString("sender_type")
                        )

                        if (jsonObject.getInt("donation_id") == donationId) {
                            messageList.add(chatData)
                            messagesAdapter?.notifyDataSetChanged()
                            this@ChatFragment.rvScrollToBottom()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_chat, container, false)
        LocalBroadcastManager.getInstance(context!!)
            .registerReceiver(chatBroadCastReceiver, IntentFilter("ChatBroadCast"))

        activityParent = activity as AppCompatActivity

        initToolBar()
        initViews()
        setAdapter()
        setClickListeners()


        options = Options.init()
            .setRequestCode(Constants.PROFILE_IMAGE_REQUEST_CODE)
            .setCount(1)
            .setFrontfacing(true)
            .setExcludeVideos(true)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("/FoodDrivePro/images")

        getChatMessages()

        return v;

    }

    private fun initToolBar() {
        toolbarTitle = activity!!.findViewById(R.id.toolbarTitle)
        toolbarBtn = activity!!.findViewById(R.id.toolbarBtn)
        toolbarLogo = activity!!.findViewById(R.id.toolBarLogo)

        toolbarTitle.text = context!!.resources.getString(R.string.chatScreenTitle)
        toolbarBtn.text = context!!.resources.getString(R.string.chatBtnBack)
        toolbarTitle.visibility = View.VISIBLE
        toolbarBtn.visibility = View.VISIBLE
        toolbarLogo.visibility = View.INVISIBLE
        toolbarBtn.setOnClickListener {
            AppUtils.hideKeyboard(activity!!)
//            activity!!.supportFragmentManager.popBackStack()
            (activity as MainActivity).onBackPressed()

        }
    }

    private fun initViews() {
        donationId = arguments?.getInt("donation_id")!!
        whichTab = arguments?.getInt("whichTab")!!

        if (donationId == -1 || donationId == null || whichTab == -1) {
            activity!!.supportFragmentManager.popBackStack()
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.error_something_went_wrong),
                false
            )
        }

        if (whichTab == 0)
            donationType = "item"
        else if (whichTab == 1)
            donationType = "money"


        btSend = v.findViewById(R.id.btSend)
        rootView = v.findViewById(R.id.rootView)
        etMessage = v.findViewById(R.id.etMessage)
        btEmoji = v.findViewById(R.id.btEmoji)
        rvMessages = v.findViewById(R.id.rvMessages)
        rlGalley = v.findViewById(R.id.rlGalley)
        rlSendMsg = v.findViewById(R.id.rlSendMsg)

        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
            .build(etMessage);
        btEmoji.setOnClickListener {
            emojiPopup.toggle()
        }
    }

    private fun setAdapter() {
        messagesAdapter = ChatAdapter(activityParent, messageList, this)
        rvMessages.adapter = messagesAdapter
        mLayoutManager = LinearLayoutManager(activityParent, RecyclerView.VERTICAL, false)
        mLayoutManager = LinearLayoutManager(activityParent, RecyclerView.VERTICAL, false)
        mLayoutManager?.stackFromEnd = true
        mLayoutManager?.isSmoothScrollbarEnabled = true
        rvMessages.layoutManager = mLayoutManager

        rvMessages.setOnTouchListener { v, event ->
            val imm: InputMethodManager =
                context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        }
    }

    private fun setClickListeners() {
        rlGalley.setOnClickListener(this)
        rlSendMsg.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rlGalley -> {
                AppUtils.hideKeyboard(activityParent)
                Pix.start(this, options)
            }
            R.id.rlSendMsg -> {
                if (etMessage.text!!.isNotBlank()) {
                    sendMessage("")
                }
            }
        }
    }


    private fun getChatMessages() {
        Loader.showLoader(context!!)
        val call = RetrofitClient.getClient(context!!).getChatMessages(donationId)
        RetrofitClient.apiCall(call, this, "GetChatMessages", context!!)
    }


    private fun rvScrollToBottom() {
        if (messagesAdapter != null)
            rvMessages?.scrollToPosition(messagesAdapter?.itemCount?.minus(1)!!)
    }


    private fun removeMsgConfirmation(position: Int) {

        iRemove.rlYes.setOnClickListener {
            removeYesChat(position)
        }

        iRemove.rlNo.setOnClickListener {
            messageList[position].toDelete = false
            removeNoChat(position)
        }

    }

    private fun removeNoChat(position: Int) {
        rlFooter.visibility = View.VISIBLE
        isShowingDelete = false
        iRemove.visibility = View.GONE
        messageList[position].toDelete = false
        messagesAdapter!!.notifyDataSetChanged()
    }

    private fun removeYesChat(position: Int) {
        positionToRemove = position
        Loader.showLoader(context!!)
        val call = RetrofitClient.getClient(context!!)
            .deleteChatMessage(donationId, messageList[position].id)
        RetrofitClient.apiCall(call, this, "RemoveChat", context!!)
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> {
                if (resultCode == Activity.RESULT_OK) {
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
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result =
                    CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri: Uri = result.uri
                    sendMessage(resultUri.path.toString())
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.d("Error", result.error.toString())
                }
            }
        }
    }

    @Override
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this, options)
                } else {
                    showError("Approve permissions to select image")
                }
                return
            }
        }
    }

    private fun showError(errorMessage: String) {
        AppUtils.showToast(
            activityParent,
            errorMessage,
            false
        )
    }

    override fun removeMessage(position: Int) {
        AppUtils.hideKeyboard(activity!!)

        messageList[position].toDelete = true

        for (i in messageList.indices)
            messageList[i].toDelete = i == position

        messagesAdapter?.notifyDataSetChanged()
        rlFooter.visibility = View.GONE
        posRemove = position
        isShowingDelete = true
        iRemove.visibility = View.VISIBLE
        removeMsgConfirmation(position)
    }

    private fun sendMessage(filePath: String) {
        Loader.showLoader(context!!)

        val fileMultiPart = if (filePath == "")
            null
        else {
            val file = File(filePath)
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                RequestBody.create(
                    "image/*".toMediaTypeOrNull(),
                    file
                )
            )
        }

        val messageRB =
            RequestBody.create("text/plain".toMediaTypeOrNull(), etMessage.text.toString())

        val donationIdRB =
            RequestBody.create("text/plain".toMediaTypeOrNull(), donationId.toString())

        val donationTypeRB =
            RequestBody.create("text/plain".toMediaTypeOrNull(), donationType.toString())

        val call = RetrofitClient.getClient(context!!)
            .sendChatMessage(donationIdRB, donationTypeRB, messageRB, fileMultiPart)
        RetrofitClient.apiCall(call, this, "SendMessage", context!!)
        etMessage.setText("")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        Loader.hideLoader()
        when (tag) {
            "GetChatMessages" -> {
                val model = Gson().fromJson(jsonObject.toString(), ChatModel::class.java)

                messageList.addAll(model.data)
                messagesAdapter?.notifyDataSetChanged()
                rvScrollToBottom()
            }
            "SendMessage" -> {

                val model =
                    Gson().fromJson(jsonObject.toString(), ChatSingleObjectModel::class.java)
                messageList.add(model.data)
                messagesAdapter!!.notifyDataSetChanged()
                rvScrollToBottom()
            }
            "RemoveChat" -> {
                AppUtils.showToast(activity, jsonObject.getString("message"), true)

                messageList.removeAt(positionToRemove)
                removeNoChat(positionToRemove)
                positionToRemove = -1
            }
        }

    }

    override fun onError(message: String, tag: String) {
        Loader.hideLoader()
        AppUtils.showToast(activity, message, false)
    }

    override fun onResume() {
        super.onResume()
        isChatActive = true
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(chatBroadCastReceiver)
        isChatActive = false
    }

}