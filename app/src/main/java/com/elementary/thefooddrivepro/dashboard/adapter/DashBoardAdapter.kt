package com.elementary.thefooddrivepro.dashboard.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.dashboard.DashboardFragment
import com.elementary.thefooddrivepro.dashboard.models.ItemDonationData
import com.elementary.thefooddrivepro.dashboard.models.MoneyDonationData
import com.elementary.thefooddrivepro.utils.AppUtils


class DashBoardAdapter(
    _fragment: DashboardFragment,
    _itemDonationList: MutableList<ItemDonationData>,
    _moneyDonationList: MutableList<MoneyDonationData>
)
    :RecyclerSwipeAdapter<RecyclerView.ViewHolder?>() {

    val viewBinderHelper = ViewBinderHelper()
    private val fragment = _fragment
    private val itemDonationList = _itemDonationList
    private val moneyDonationList = _moneyDonationList

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        viewBinderHelper.setOpenOnlyOne(true)
        context = parent.context
        return if (viewType == MENU_ITEM_VIEW_TYPE)
            Item(
                LayoutInflater.from(parent.context).inflate(R.layout.item_donations, parent, false)
            )
        else
            Loading(
                LayoutInflater.from(parent.context).inflate(R.layout.item_load_more, parent, false)
            )
    }

    override fun getItemCount(): Int {
        return if (fragment.whichTab == 0)
            itemDonationList.size
        else
            moneyDonationList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (fragment.whichTab == 0) {
            if (itemDonationList[position].isLoadMore)
                LOADER_VIEW_TYPE
            else
                MENU_ITEM_VIEW_TYPE
        } else {
            if (moneyDonationList[position].isLoadMore)
                LOADER_VIEW_TYPE
            else
                MENU_ITEM_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Item) {
//            viewBinderHelper.bind(holder.swipeLayout, position.toString())
            holder.bind(position)
        }
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe_layout
    }

    inner class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val swipeLayout: SwipeLayout = itemView.findViewById(R.id.swipe_layout)
        private val chatLayout: RelativeLayout = itemView.findViewById(R.id.chatLayout)
        private val pickedLayout: RelativeLayout = itemView.findViewById(R.id.pickedLayout)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val menuIcon: ImageView = itemView.findViewById(R.id.menuIcon)
        private val itemMainLayout: LinearLayout = itemView.findViewById(R.id.itemMainLayout)
        private val dateTime: TextView = itemView.findViewById(R.id.dateTime)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val address: TextView = itemView.findViewById(R.id.address)
        private val number: TextView = itemView.findViewById(R.id.number)
        private val tvStatus: TextView = itemView.findViewById(R.id.status)


        fun bind(position: Int) {
            mItemManger.bindView(itemView, position);
            swipeLayout.visibility = View.VISIBLE
            itemMainLayout.visibility = View.VISIBLE
            swipeLayout.close(true)

            swipeLayout.showMode= SwipeLayout.ShowMode.PullOut
            swipeLayout.addDrag(
                SwipeLayout.DragEdge.Right,swipeLayout.findViewById(R.id.sToRight)
            )

            swipeLayout.addSwipeListener(object : SwipeLayout.SwipeListener {
                override fun onStartOpen(layout: SwipeLayout) {
                    mItemManger.closeAllExcept(layout)
                }

                override fun onOpen(layout: SwipeLayout) {}
                override fun onStartClose(layout: SwipeLayout) {}
                override fun onClose(layout: SwipeLayout) {}
                override fun onUpdate(
                    layout: SwipeLayout,
                    leftOffset: Int,
                    topOffset: Int
                ) {
                }

                override fun onHandRelease(
                    layout: SwipeLayout,
                    xvel: Float,
                    yvel: Float
                ) {
                }
            })

            if (fragment.whichTab == 0) {
                image.visibility = View.VISIBLE

                dateTime.text = itemDonationList[position].updated_date_format
                name.text = itemDonationList[position].donor?.full_name
                address.text = itemDonationList[position].donor?.address
                number.text = itemDonationList[position].donor?.phone_number

                setStatus(itemDonationList[position].status, tvStatus, menuIcon)

                if (itemDonationList[position].donation_files?.isNotEmpty()!!)
                    Glide.with(context)
                        .load(itemDonationList[position].donation_files!![0].fullImage)
                        .into(image)
                else
                    Glide.with(context).load(R.drawable.ic_place_holder).into(image)


                when (itemDonationList[position].status) {
                    "pending" -> {
                        chatLayout.visibility = View.VISIBLE
                        pickedLayout.visibility = View.GONE
                    }
                    "confirmed" -> {
                        chatLayout.visibility = View.VISIBLE
                        pickedLayout.visibility = View.VISIBLE
                    }
                    "received" -> {
                        chatLayout.visibility = View.GONE
                        pickedLayout.visibility = View.GONE
                    }
                    else -> {
                        chatLayout.visibility = View.GONE
                        pickedLayout.visibility = View.GONE
                    }
                }

            }

            else {
                image.visibility = View.GONE

                dateTime.text = moneyDonationList[position].updated_date_format
                name.text = moneyDonationList[position].donor?.full_name
                address.text = moneyDonationList[position].donor?.address
                number.text = moneyDonationList[position].donor?.phone_number

                setStatus(moneyDonationList[position].status, tvStatus, menuIcon)


                when (moneyDonationList[position].status) {
                    "pending" -> {
                        chatLayout.visibility = View.GONE
                        pickedLayout.visibility = View.VISIBLE
                    }
                    "confirmed" -> {
                        chatLayout.visibility = View.GONE
                        pickedLayout.visibility = View.GONE
                    }
                    "received" -> {
                        chatLayout.visibility = View.GONE
                        pickedLayout.visibility = View.GONE
                    }
                    else -> {
                        chatLayout.visibility = View.GONE
                        pickedLayout.visibility = View.GONE
                    }
                }

            }

            itemMainLayout.setOnClickListener {
                swipeLayout.close(true)
                fragment.launchFragment(position)
            }

            chatLayout.setOnClickListener {
                swipeLayout.close(true)
                fragment.openChatFragment(adapterPosition)
            }
            pickedLayout.setOnClickListener {
                swipeLayout.close(true)
                fragment.pickDonation(position)
            }


        }

        private fun setStatus(status: String, tvStatus: TextView, menuIcon: ImageView) {
            when (status) {
                "pending" -> {
                    tvStatus.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                    tvStatus.text =
                        AppUtils.getFirstLetterCapital(status)

                    tvStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.gray_dark
                        )
                    )

                    menuIcon.visibility = View.VISIBLE

                }
                "confirmed" -> {
                    tvStatus.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.itemStatusBlueColor
                        )
                    )
                    tvStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.whiteColor
                        )
                    )
                    tvStatus.text =
                        AppUtils.getFirstLetterCapital(status)
                    menuIcon.visibility = View.VISIBLE

                }
                "received" -> {
                    tvStatus.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.appGreenColor
                        )
                    )
                    tvStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.whiteColor
                        )
                    )
                    tvStatus.text =
                        AppUtils.getFirstLetterCapital(status)
                    menuIcon.visibility = View.GONE
                }
            }
        }

    }

    inner class Loading(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
    }

    companion object {
        // A menu item view type.
        private const val MENU_ITEM_VIEW_TYPE = 0
        private const val LOADER_VIEW_TYPE = 1
        private const val BANNER_AD_VIEW_TYPE = 2
    }

}