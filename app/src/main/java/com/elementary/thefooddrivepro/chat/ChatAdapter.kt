package com.elementary.thefooddrivepro.chat

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.paris.Paris
import com.bumptech.glide.Glide
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.chat.model.ChatData
import com.elementary.thefooddrivepro.utils.AppController
import com.makeramen.roundedimageview.RoundedImageView
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(
    _activity: Activity, _messageList: MutableList<ChatData>, _onPostItemClick: OnPostItemClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnPostItemClick {
        fun removeMessage(position: Int)
    }

    private var activity = _activity
    private var allMessages = _messageList
    private var onItemClick: OnPostItemClick? = _onPostItemClick

    override fun onBindViewHolder(vHolder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {

            yourMessage -> {
                val holder = vHolder as YourMessageHolder
                var messageObj = allMessages[position]

                if (messageObj.toDelete) {
                    holder.ivCurve.setImageResource(R.drawable.ic_red_your)
                    holder.tvMsg.setTextColor(activity.resources.getColor(R.color.charcoal_light))
                    Paris.style(holder.rlMsgParent).apply(R.style.chatItemMsgBgGreenRightDelete)
                } else {
                    holder.ivCurve.setImageResource(R.drawable.ic_green_dots)
                    holder.tvMsg.setTextColor(activity.resources.getColor(R.color.white))
                    Paris.style(holder.rlMsgParent).apply(R.style.chatItemMsgBgGreenRight)
                }

                holder.ivSeen.setBackgroundResource(R.drawable.ic_chat_single_tick)
                holder.ivSeen.visibility = View.GONE


                holder.tvMsg.text = messageObj.message
                holder.tvSentTime.text = messageObj.updated_date_format

                holder.llParent.setOnLongClickListener {

                    onItemClick?.removeMessage(position)
                    return@setOnLongClickListener true
                }
                holder.ivProfile.visibility = View.GONE

                Glide.with(activity)
                    .load(AppController.userInfo?.fullImage)
                    .placeholder(R.drawable.ic_user_logo)
                    .error(R.drawable.ic_user_logo)
                    .into(holder.ivProfile)
                holder.ivProfile.visibility = View.VISIBLE

            }
            yourImage -> {
                val holder = vHolder as YourImageHolder

                var messageObj = allMessages!![position]
                holder.tvSentTime.text = messageObj.updated_date_format

                if (messageObj.toDelete) {
                    holder.ivCurve.setImageResource(R.drawable.ic_red_your)

                    Paris.style(holder.rlImgParent).apply(R.style.chatItemMsgBgGreenRightDelete)
                } else {
                    holder.ivCurve.setImageResource(R.drawable.ic_green_dots)
                    Paris.style(holder.rlImgParent).apply(R.style.chatItemBgGreenRightImage)
                }


                holder.ivSeen.setBackgroundResource(R.drawable.ic_chat_single_tick)
                holder.ivSeen.visibility = View.GONE

                Glide.with(activity)
                    .load(messageObj.full_image)
                    .placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_place_holder)
                    .into(holder.imageView)

                holder.llParent.setOnLongClickListener {

                    onItemClick?.removeMessage(position)
                    return@setOnLongClickListener true
                }
                holder.imageView.setOnLongClickListener {

                    onItemClick?.removeMessage(position)
                    return@setOnLongClickListener true
                }


                holder.imageView.setOnClickListener {
                    var intent = Intent(activity!!, ChatGallery::class.java)
                    intent.putExtra("imageUrl", messageObj.full_image)
                    activity?.startActivity(intent)
                }
                holder.ivProfile.visibility = View.GONE

                Glide.with(activity)
                    .load(AppController.userInfo?.fullImage)
                    .placeholder(R.drawable.ic_user_logo)
                    .error(R.drawable.ic_user_logo)
                    .into(holder.ivProfile)
                holder.ivProfile.visibility = View.VISIBLE

            }
            fromMessage -> {
                val holder = vHolder as FromMessageHolder


                var messageObj = allMessages!![position]
                holder.tvMsg.text = messageObj.message
                holder.tvSentTime.text = messageObj.updated_date_format
                holder.ivProfile.visibility = View.GONE

                Glide.with(activity)
                    .load(messageObj?.sender?.fullImage)
                    .placeholder(R.drawable.ic_user_logo)
                    .error(R.drawable.ic_user_logo)
                    .into(holder.ivProfile)
                holder.ivProfile.visibility = View.VISIBLE

            }
            fromImage -> {
                val holder = vHolder as FromImageHolder

                var messageObj = allMessages!![position]
                holder.tvSentTime.text = messageObj.updated_date_format

                Glide.with(activity)
                    .load(messageObj?.full_image)
                    .placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_place_holder)
                    .into(holder.imageView)

                holder.imageView.setOnClickListener {
                    var intent = Intent(activity!!, ChatGallery::class.java)
                    intent.putExtra("imageUrl", messageObj.full_image)
                    activity?.startActivity(intent)
                }

                holder.ivProfile.visibility = View.GONE
                Glide.with(activity)
                    .load(messageObj?.sender?.fullImage)
                    .placeholder(R.drawable.ic_user_logo)
                    .error(R.drawable.ic_user_logo)
                    .into(holder.ivProfile)
                holder.ivProfile.visibility = View.VISIBLE

            }


        }
    }


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(viewGroup.context)
        val v: View
        when (viewType) {
            yourVideo -> {
                v = inflater.inflate(R.layout.item_chat_your_video, viewGroup, false)
                viewHolder = YourVideoHolder(v)
            }
            yourMessage -> {
                v = inflater.inflate(R.layout.item_chat_your_message, viewGroup, false)
                viewHolder = YourMessageHolder(v)
            }
            yourImage -> {
                v = inflater.inflate(R.layout.item_chat_your_image, viewGroup, false)
                viewHolder = YourImageHolder(v)
            }

            fromVideo -> {
                v = inflater.inflate(R.layout.item_chat_from_video, viewGroup, false)
                viewHolder = FromVideoHolder(v)
            }

            fromMessage -> {
                v = inflater.inflate(R.layout.item_chat_from_message, viewGroup, false)
                viewHolder = FromMessageHolder(v)
            }
            fromImage -> {
                v = inflater.inflate(R.layout.item_chat_from_image, viewGroup, false)
                viewHolder = FromImageHolder(v)
            }

        }
        return viewHolder!!
    }

    inner class YourVideoHolder(v: View) : RecyclerView.ViewHolder(v) {

        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var ivVideoThumb: RoundedImageView = v.findViewById(R.id.ivVideoThumb)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)

    }

    inner class YourMessageHolder(v: View) : RecyclerView.ViewHolder(v) {
        var ivCurve: ImageView = v.findViewById(R.id.ivCurve)
        var rlMsgParent: RelativeLayout = v.findViewById(R.id.rlMsgParent)
        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var tvMsg: TextView = v.findViewById(R.id.tvMsg)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)

    }

    inner class YourImageHolder(v: View) : RecyclerView.ViewHolder(v) {
        var ivCurve: ImageView = v.findViewById(R.id.ivCurve)
        var rlImgParent: RelativeLayout = v.findViewById(R.id.rlImgParent)
        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var imageView: RoundedImageView = v.findViewById(R.id.imageView)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)

    }


    inner class FromVideoHolder(v: View) : RecyclerView.ViewHolder(v) {

        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var ivVideoThumb: RoundedImageView = v.findViewById(R.id.ivVideoThumb)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)

    }

    inner class FromMessageHolder(v: View) : RecyclerView.ViewHolder(v) {

        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var tvMsg: TextView = v.findViewById(R.id.tvMsg)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)
    }

    inner class FromImageHolder(v: View) : RecyclerView.ViewHolder(v) {

        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var imageView: RoundedImageView = v.findViewById(R.id.imageView)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)

    }


    override fun getItemViewType(position: Int): Int {
        if (AppController.userInfo?.id == allMessages.get(position).from_id &&
            allMessages[position].sender_type.equals("rider", true)
        ) {
            return if (allMessages[position].message == null || allMessages.get(
                    position
                ).message == "" || allMessages.get(position).message == "null"
            ) {
                yourImage
            } else {
                yourMessage
            }
        } else {
            return if (allMessages.get(position).message == null || allMessages.get(
                    position
                ).message == "" || allMessages.get(position).message == "null"
            ) {
                fromImage
            } else {
                fromMessage
            }
        }
    }

    override fun getItemCount(): Int {
        return allMessages!!.size
    }

    companion object {
        private const val yourVideo = 0
        private const val yourMessage = 1
        private const val yourImage = 2
        private const val yourAudio = 3
        private const val chatJoined = 4
        private const val fromVideo = 5
        private const val fromMessage = 6
        private const val fromImage = 7
        private const val fromAudio = 8
        private const val chatFeedback = 9
        private const val chatEnded = 10
    }
}
