package com.elementary.thefooddrivepro.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elementary.thefooddrivepro.MainActivity
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.chat.ChatFragment
import com.elementary.thefooddrivepro.dashboard.adapter.DashBoardAdapter
import com.elementary.thefooddrivepro.dashboard.models.ItemDonationData
import com.elementary.thefooddrivepro.dashboard.models.ItemDonationModel
import com.elementary.thefooddrivepro.dashboard.models.MoneyDonationData
import com.elementary.thefooddrivepro.dashboard.models.MoneyDonationModel
import com.elementary.thefooddrivepro.network.ResponseCallBack
import com.elementary.thefooddrivepro.network.RetrofitClient
import com.elementary.thefooddrivepro.utils.AppUtils
import com.elementary.thefooddrivepro.utils.Loader
import com.elementary.thefooddrivepro.utils.PaginationScrollListener
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import org.json.JSONObject


class DashboardFragment : Fragment(), ResponseCallBack {

    private var pickedPosition: Int = -1
    private lateinit var toolbarTitle: TextView
    private lateinit var toolbarBtn: TextView
    private lateinit var toolbarLogo: ImageView

    private var v: View? = null
    private lateinit var donationRadioButton: RadioButton
    private lateinit var moneyRadioButton: RadioButton
    private lateinit var dashboardRecycler: RecyclerView
    private lateinit var dashboardAdapter: DashBoardAdapter
    private lateinit var tvNoData: TextView

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var shimmer_view_container: ShimmerFrameLayout

    private var itemDonationList: MutableList<ItemDonationData> = ArrayList()
    private var moneyDonationList: MutableList<MoneyDonationData> = ArrayList()

    var whichTab = 0 // 0 for Donations 1 for Money
    private var pageNo = 1
    private var isLoading = false
    private var isLastPage: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (v == null) {
            v = inflater.inflate(R.layout.fragment_dashboard, container, false)

            initViews()
        } else {
            toolbarTitle.visibility = View.GONE
            toolbarBtn.visibility = View.GONE
            toolbarLogo.visibility = View.VISIBLE
        }

        return v
    }

    private fun initViews() {
        dashboardRecycler = v?.findViewById(R.id.dashbooardRecycler)!!
        tvNoData = v?.findViewById(R.id.tvNoData)!!
        toolbarTitle = activity!!.findViewById(R.id.toolbarTitle)
        toolbarLogo = activity!!.findViewById(R.id.toolBarLogo)
        toolbarBtn = activity!!.findViewById(R.id.toolbarBtn)
        toolbarTitle.visibility = View.GONE
        toolbarBtn.visibility = View.GONE
        toolbarLogo.visibility = View.VISIBLE


        shimmer_view_container = v?.findViewById(R.id.shimmer_view_container)!!

        swipeRefresh = v?.findViewById(R.id.swipeRefresh)!!
        swipeRefresh.setOnRefreshListener {
            moneyDonationList.clear()
            itemDonationList.clear()
//            shimmer_view_container.showShimmer(true)
            shimmer_view_container.startShimmer()
            shimmer_view_container.visibility = View.VISIBLE
            dashboardRecycler.visibility = View.GONE
            shimmer_view_container.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
            pageNo = 1
            Loader.showLoader(context!!)
            getDonationList()
        }
        donationRadioButton = v?.findViewById(R.id.donationRadioButton)!!
        moneyRadioButton = v?.findViewById(R.id.moneyRadioButton)!!

        donationRadioButton.setOnClickListener { updateDashboardList(donationRadioButton) }
        moneyRadioButton.setOnClickListener { updateDashboardList(moneyRadioButton) }

        donationRadioButton.callOnClick()

        setUpAdapter()
    }

    private fun setUpAdapter() {
        dashboardAdapter =
            DashBoardAdapter(
                this,
                itemDonationList,
                moneyDonationList
            )
        dashboardRecycler.layoutManager = LinearLayoutManager(context)
        dashboardRecycler.adapter = dashboardAdapter

        dashboardRecycler.addOnScrollListener(object :
            PaginationScrollListener(dashboardRecycler.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                if (!isLastPage) {
                    isLoading = true
                    pageNo += 1
                    getDonationList()
                }
            }
        })
    }

    private fun updateDashboardList(radioButton: RadioButton) {
        pageNo = 1
        tvNoData.visibility = View.GONE

        if (radioButton.id == R.id.donationRadioButton) {
            whichTab = 0
            donationRadioButton.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.appGreenColor
                )
            )
            donationRadioButton.setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.whiteColor
                )
            )
            moneyRadioButton.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.bigSizeLabelColor
                )
            )
            moneyRadioButton.setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.appGreyColor
                )
            )

        } else {
            whichTab = 1
            moneyRadioButton.setTextColor(ContextCompat.getColor(context!!, R.color.appGreenColor))
            moneyRadioButton.setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.whiteColor
                )
            )
            donationRadioButton.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.bigSizeLabelColor
                )
            )
            donationRadioButton.setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.appGreyColor
                )
            )
        }

        moneyDonationList.clear()
        itemDonationList.clear()
//        shimmer_view_container.showShimmer(true)
        shimmer_view_container.startShimmer()
        dashboardRecycler.visibility = View.GONE
        shimmer_view_container.visibility = View.VISIBLE

        Loader.showLoader(context!!)
        getDonationList()
    }

    private fun getDonationList() {
        if (whichTab == 0) {
            val call = RetrofitClient.getClient(context!!).getItemDonations(pageNo)
            RetrofitClient.apiCall(call, this, "ItemDonations", context!!)
        } else {
            val call = RetrofitClient.getClient(context!!).getMoneyDonations(pageNo)
            RetrofitClient.apiCall(call, this, "MoneyDonations", context!!)
        }

    }

    fun launchFragment(position: Int) {
        if (whichTab == 0) {
            val frag = DonationDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable("data", itemDonationList[position])
            frag.arguments = bundle
            activity!!.supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left,
                    R.anim.slide_in_right,
                    R.anim.slide_out_right
                )
                .replace(R.id.mainFragment, frag)
                .addToBackStack("")
                .commit()

            (activity as MainActivity).currentFragment = DonationDetailFragment()

        } else {
            val frag = MoneyDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable("data", moneyDonationList[position])
            frag.arguments = bundle
            activity!!.supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left,
                    R.anim.slide_in_right,
                    R.anim.slide_out_right
                )
                .replace(R.id.mainFragment, frag)
                .addToBackStack("")
                .commit()

            (activity as MainActivity).currentFragment = MoneyDetailFragment()

        }
    }

    fun openChatFragment(position: Int) {

        val frag = ChatFragment()
        val bundle = Bundle()
        if (whichTab == 0)
            bundle.putInt("donation_id", itemDonationList[position].id)
        else
            bundle.putInt("donation_id", moneyDonationList[position].id)
        bundle.putInt("whichTab", whichTab)

        frag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_left,
                R.anim.slide_in_right,
                R.anim.slide_out_right
            )
            .replace(R.id.mainFragment, frag)
            .addToBackStack("")
            .commit()

        (activity as MainActivity).currentFragment = frag

    }

    fun pickDonation(position: Int) {
        pickedPosition = position
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context!!, R.style.MyAlertDialogTheme)
        if (whichTab == 0)
            builder.setMessage(context!!.resources.getString(R.string.dashboardDialogLabelPickedItem))
        else
            builder.setMessage(context!!.resources.getString(R.string.dashboardDialogLabelReceiveMoney))

        builder.setPositiveButton(
            context!!.resources.getString(R.string.yes)
        ) { dialog, which ->
            dialog.dismiss()
            Loader.showLoader(context!!)
            if (whichTab == 0) {
                val call = RetrofitClient.getClient(context!!)
                    .pickItemDonation(itemDonationList[position].id)
                RetrofitClient.apiCall(call, this, "PickedItemDonation", context!!)
            } else {
                val call = RetrofitClient.getClient(context!!)
                    .receiveMoneyDonation(moneyDonationList[position].id)
                RetrofitClient.apiCall(call, this, "ReceivedMoneyDonation", context!!)
            }
        }

        builder.setNegativeButton(
            context!!.resources.getString(R.string.no)
        ) { dialog, which -> // Do nothing
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.show()

    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        Loader.hideLoader()

        if (swipeRefresh.isRefreshing)
            swipeRefresh.isRefreshing = false

        when (tag) {
            "ItemDonations" -> {
                if (shimmer_view_container.isShimmerStarted) {
                    shimmer_view_container.stopShimmer()
//                    shimmer_view_container.hideShimmer()
                    dashboardRecycler.visibility = View.VISIBLE
                    shimmer_view_container.visibility = View.GONE
                }

                val gson = Gson()
                val model = gson.fromJson(jsonObject.toString(), ItemDonationModel::class.java)

                if (pageNo > 1 && pageNo <= model.data.last_page)
                    if (itemDonationList[itemDonationList.size - 1].isLoadMore) {
                        itemDonationList.removeAt(itemDonationList.size - 1)
                        dashboardAdapter.notifyItemRemoved(itemDonationList.size - 1)
                        isLoading = false
                    }

                itemDonationList.addAll(model.data.data)

                if (!itemDonationList.isNullOrEmpty()) {
                    dashboardRecycler.visibility = View.VISIBLE
                    tvNoData.visibility = View.GONE

                    if (pageNo < model.data.last_page) {
                        val itemDonationData = ItemDonationData(
                            "", "", -1, "",
                            "", "", null, "",
                            null, -1, -1, null,
                            -1, "", -1, -1,
                            "", "", "", "",
                            null, -1, "", "",
                            "", "", "", "",
                            "", -1, "", "",
                            -1, "", "", "",
                            true
                        )
                        itemDonationList.add(itemDonationData)
                    }

                    dashboardAdapter.notifyDataSetChanged()

                    isLastPage = pageNo == model.data.last_page




                } else {
                    dashboardRecycler.visibility = View.GONE
                    tvNoData.visibility = View.VISIBLE
                }
            }
            "MoneyDonations" -> {

                if (shimmer_view_container.isShimmerStarted) {
                    shimmer_view_container.stopShimmer()
//                    shimmer_view_container.hideShimmer()
                    dashboardRecycler.visibility = View.VISIBLE
                    shimmer_view_container.visibility = View.GONE

                }

                val gson = Gson()
                val model = gson.fromJson(jsonObject.toString(), MoneyDonationModel::class.java)

                if (pageNo > 1 && pageNo <= model.data.last_page)
                    if (moneyDonationList[moneyDonationList.size - 1].isLoadMore) {
                        moneyDonationList.removeAt(moneyDonationList.size - 1)
                        dashboardAdapter.notifyItemRemoved(moneyDonationList.size - 1)
                        isLoading = false
                    }

                moneyDonationList.addAll(model.data.data)

                if (!moneyDonationList.isNullOrEmpty()) {
                    dashboardRecycler.visibility = View.VISIBLE
                    tvNoData.visibility = View.GONE

                    if (pageNo < model.data.last_page) {
                        val moneyDonationData = MoneyDonationData(
                            "", "", "", "",
                            "", -1, "", null,
                            -1, -1, -1, "",
                            "", null, -1, "",
                            null, -1, "", "",
                            "","", "", "", "",
                            true

                        )
                        moneyDonationList.add(moneyDonationData)
                    }

                    dashboardAdapter.notifyDataSetChanged()

                    isLastPage = pageNo == model.data.last_page

                } else {
                    dashboardRecycler.visibility = View.GONE
                    tvNoData.visibility = View.VISIBLE
                }
            }
            "PickedItemDonation" -> {
                itemDonationList[pickedPosition].status = "received"
                dashboardAdapter.notifyItemChanged(pickedPosition)

                AppUtils.showToast(
                    activity,
                    context!!.resources.getString(R.string.dashboardErrorDonationPicked), true
                )

                pickedPosition = -1
            }
            "ReceivedMoneyDonation" -> {
                moneyDonationList[pickedPosition].status = "received"
                dashboardAdapter.notifyItemChanged(pickedPosition)

                AppUtils.showToast(
                    activity,
                    context!!.resources.getString(R.string.dashboardErrorDonationReceived), true
                )
                pickedPosition = -1
            }
        }


    }

    override fun onError(message: String, tag: String) {
        Loader.hideLoader()
        AppUtils.showToast(activity, message, false)
    }


}