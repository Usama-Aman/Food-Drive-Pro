package com.elementary.thefooddrivepro


import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import com.elementary.thefooddrivepro.utils.BaseActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class AddressActivity : BaseActivity(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks {

    private lateinit var mMap: GoogleMap
    private lateinit var googleApiClient: GoogleApiClient

    private lateinit var donationLatLng: LatLng

    private lateinit var btnBack: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setStatusBarColor(this)

        initViews()

    }

    private fun initViews() {

        val bundle = intent.extras
        donationLatLng = LatLng(bundle!!.getDouble("lat"), bundle.getDouble("lng"))

        btnBack = findViewById(R.id.toolbarMenu)
        btnBack.setOnClickListener { finish() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this,
                R.raw.maps_style
            )
        )

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(donationLatLng, 15f)
        mMap.animateCamera(cameraUpdate)
        updateMarker()
    }

    private fun updateMarker() {
        val markerOptions = MarkerOptions()
        markerOptions.position(donationLatLng)
        markerOptions.draggable(true)
        mMap.clear()
        mMap.addMarker(markerOptions)
    }


    override fun onConnected(p0: Bundle?) {
        Log.d(
            "OnConnected",
            ": ${p0.toString()}"
        )
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d(
            "Error",
            "onStatusChanged: ${p0.toString()}"
        )
    }


    override fun onStart() {
        super.onStart()
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build()
    }

    override fun onStop() {
        googleApiClient.disconnect()
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        if (googleApiClient.isConnected) {
            googleApiClient.disconnect()
        }
    }


}
