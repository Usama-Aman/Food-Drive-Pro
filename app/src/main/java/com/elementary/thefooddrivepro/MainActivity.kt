package com.elementary.thefooddrivepro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.elementary.thefooddrivepro.auth.Login
import com.elementary.thefooddrivepro.chat.ChatFragment
import com.elementary.thefooddrivepro.dashboard.DashboardFragment
import com.elementary.thefooddrivepro.edit_profile.EditProfileFragment
import com.elementary.thefooddrivepro.network.ResponseCallBack
import com.elementary.thefooddrivepro.network.RetrofitClient
import com.elementary.thefooddrivepro.utils.*
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.lang.Exception


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    ResponseCallBack {

    var doubleBackToExitPressedOnce = false
    private lateinit var toolbarMenu: RelativeLayout

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navProfileImageView: CircleImageView
    private lateinit var navUserName: TextView
    private lateinit var navUserEmail: TextView
    private lateinit var navCross: RelativeLayout

    lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppController.isAppRunning = true
        setStatusBarColor(this)

        initViews()
        setUpUserData()

        launchFragment()
    }

    private fun initViews() {
        toolbarMenu = findViewById(R.id.toolbarMenu)
        toolbarMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START, true) }

        drawerLayout = findViewById(R.id.drawerLayout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        val navHeaderView = navigationView.getHeaderView(0)
        navProfileImageView = navHeaderView.findViewById(R.id.navProfileImage)
        navUserName = navHeaderView.findViewById(R.id.navUserName)
        navUserEmail = navHeaderView.findViewById(R.id.navUserEmail)
        navCross = navHeaderView.findViewById(R.id.navCross)
        navCross.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START, true)
        }

        navHeaderView.setOnClickListener {
            onNavigationItemSelected(navigationView.menu.getItem(1))
        }
    }

    private fun launchFragment() {
        navigationView.menu.getItem(0).isChecked = true

        if (intent.getBooleanExtra("isComingFromNotification", false)) {
            SharedPreference.saveBoolean(this, Constants.isComingFromNotification, true)

            if (intent.hasExtra("messageData")) {
                val messageData = JSONObject(intent.getStringExtra("messageData")!!)

                val frag = ChatFragment()
                val bundle = Bundle()
                bundle.putInt("donation_id", messageData.getInt("donation_id"))

                if (messageData.getString("donation_type").equals("item", true))
                    bundle.putInt("whichTab", 0)
                else
                    bundle.putInt("whichTab", 1)

                frag.arguments = bundle

                if (this::currentFragment.isInitialized)
                    if (currentFragment is ChatFragment)
                        supportFragmentManager.popBackStack()

                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_left,
                        R.anim.slide_in_right,
                        R.anim.slide_out_right
                    )
                    .add(R.id.mainFragment, frag)
                    .commit()

                currentFragment = frag
            }
        } else {

            currentFragment = DashboardFragment()
            supportFragmentManager.beginTransaction().add(R.id.mainFragment, currentFragment)
                .commit()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START, true)
        item.isChecked = true

        Handler().postDelayed({
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    if (currentFragment !is DashboardFragment) {

                        supportFragmentManager.popBackStack(
                            null,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )

                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.slide_in_left,
                                R.anim.slide_out_left,
                                R.anim.slide_in_right,
                                R.anim.slide_out_right
                            )
                            .replace(R.id.mainFragment, DashboardFragment())
                            .commit()

                    }
                    currentFragment = DashboardFragment()
                }
                R.id.nav_profile -> {
                    if (currentFragment !is EditProfileFragment) {

                        supportFragmentManager.popBackStack(
                            null,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )

                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.slide_in_left,
                                R.anim.slide_out_left,
                                R.anim.slide_in_right,
                                R.anim.slide_out_right
                            )
                            .replace(
                                R.id.mainFragment,
                                EditProfileFragment()
                            )
                            .commit()
                    }

                    currentFragment =
                        EditProfileFragment()
                }
                R.id.nav_logout -> {
                    Loader.showLoader(this)
                    val call = RetrofitClient.getClient(this).logout()
                    RetrofitClient.apiCall(call, this, "Logout", this)
                }
            }

        }, 300)


        return true

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.drawer_menu, menu)
        return true
    }

    override fun onBackPressed() {
        val fragments = supportFragmentManager.backStackEntryCount
        when {

            drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                drawerLayout.closeDrawer(
                    GravityCompat.START,
                    true
                )
            }
            fragments >= 1 -> {
                supportFragmentManager.popBackStack()
            }
            fragments == 0 -> {
                when {
                    currentFragment is ChatFragment -> {
                        drawerLayout.openDrawer(
                            GravityCompat.START,
                            true
                        )

                    }
                    doubleBackToExitPressedOnce -> {
                        super.onBackPressed()
                    }
                    else -> {
                        this.doubleBackToExitPressedOnce = true
                        Toast.makeText(
                            this@MainActivity,
                            resources.getString(R.string.back_pressed_again),
                            Toast.LENGTH_SHORT
                        ).show()
                        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
                    }
                }
            }
            doubleBackToExitPressedOnce -> {
                super.onBackPressed()
                return
            }
            else -> {
                this.doubleBackToExitPressedOnce = true
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.back_pressed_again),
                    Toast.LENGTH_SHORT
                ).show()
                Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            }
        }
    }

    fun setUpUserData() {
        val loginModel = AppUtils.getLoginModel(this)

        AppController.userInfo = loginModel

        navUserName.text = Html.fromHtml("<b>${loginModel.name}</b>")
        navUserEmail.text = loginModel.email

        Glide.with(this).load(loginModel.fullImage).placeholder(R.drawable.ic_place_holder)
            .into(navProfileImageView)

    }

    fun launchDashBoard() {
        navigationView.menu.getItem(0).isChecked = true
        navigationView.menu.performIdentifierAction(R.id.nav_dashboard, 0)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent != null) {
            try {
                if (intent.hasExtra("isComingFromNotification")) {
                    if (intent.hasExtra("messageData")) {
                        val messageData = JSONObject(intent.getStringExtra("messageData")!!)

                        SharedPreference.saveBoolean(this, Constants.isComingFromNotification, true)

                        val frag = ChatFragment()
                        val bundle = Bundle()
                        bundle.putInt("donation_id", messageData.getInt("donation_id"))

                        if (messageData.getString("donation_type").equals("item", true))
                            bundle.putInt("whichTab", 0)
                        else
                            bundle.putInt("whichTab", 1)

                        frag.arguments = bundle

                        if (currentFragment is ChatFragment)
                            supportFragmentManager.popBackStack()

                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.slide_in_left,
                                R.anim.slide_out_left,
                                R.anim.slide_in_right,
                                R.anim.slide_out_right
                            )
                            .replace(R.id.mainFragment, frag)
                            .addToBackStack("")
                            .commit()

                        currentFragment = frag

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        Loader.hideLoader()

        SharedPreference.saveBoolean(this, Constants.IsLoggedIn, false)
        SharedPreference.saveSimpleString(this, Constants.userData, "")
        SharedPreference.saveSimpleString(this, Constants.accessToken, "")

        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()

    }

    override fun onError(message: String, tag: String) {
        Loader.hideLoader()
        AppUtils.showToast(this, message, false)
    }

}
