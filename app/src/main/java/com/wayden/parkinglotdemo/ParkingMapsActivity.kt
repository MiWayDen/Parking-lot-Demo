package com.wayden.parkinglotdemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.wayden.parkinglotdemo.databinding.ActivityParkingMapsBinding
import kotlinx.android.synthetic.main.activity_parking_maps.*
import kotlinx.android.synthetic.main.info_window.*
import kotlinx.android.synthetic.main.recycler_from_detail_area.*
import java.util.ArrayList

class ParkingMapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener {

    var bigArea: ArrayList<String> = arrayListOf()
    private lateinit var gpsLatLong: HashMap<String, ArrayList<String>>
    private lateinit var availableCar: HashMap<String, ArrayList<String>>
    private lateinit var currentLocation: LatLng
    private  var parkingRaw: String = ""
    private val mContext: Context = this
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationPermissionGranted = false
    private val locationPermission = 1
    private val gpsPermission = 2
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityParkingMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParkingMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getParkingPositionMap()
        getLocationPermission()

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getGPSLocation()
        addParkingMarker()
        //???????????????????????????????????????
        val market = LatLng (24.165261,120.643679)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(market,15F))
    }
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale
                (this, Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            AlertDialog.Builder(this)
                .setMessage("??????????????????????????????????????????????????????")
                .setPositiveButton("??????") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermission)
                }
                .setNegativeButton("??????") { _, _ -> requestLocationPermission() }
                .show()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf
                    (Manifest.permission.ACCESS_FINE_LOCATION), locationPermission
            )
        }
    }

    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            //???????????????
            Toast.makeText(this, "????????????????????????????????????????????????????????????", Toast.LENGTH_LONG)
                .show()
            checkGPSState()
            locationPermissionGranted = true
        } else { //????????????????????????
            requestLocationPermission()
        }
    }

    private fun checkGPSState() {
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "?????????GPS??????", Toast.LENGTH_LONG)
                .show()
//            getGPSLocation()
        } else {
            AlertDialog.Builder(this)
                .setTitle("?????????GPS")
                .setMessage("?????????GPS??????")
                .setPositiveButton("????????????") { _, _ ->
                    startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                        gpsPermission)
                }
                .setNegativeButton("??????", null)
                .show()
        }
    }

    fun getGPSLocation() {
        val locationRequest = com.google.android.gms.location.LocationRequest.create()
        locationRequest.priority = LocationRequest.QUALITY_HIGH_ACCURACY
        //???????????????, ????????????
        locationRequest.interval = 2000

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT)
        } else {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {

                    @SuppressLint("MissingPermission")

                    override fun onLocationResult(locationResult: LocationResult) {

                        currentLocation = LatLng(
                            locationResult.lastLocation.latitude,
                            locationResult.lastLocation.longitude
                        )
                        mMap.isMyLocationEnabled = true
                        HomeButton.setOnClickListener{_->
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15F))
                        }
                        //????????????????????????
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15F))

/*                        mMap.addMarker(MarkerOptions().position(currentLocation).title("????????????")
                            .snippet("?????????????????????"))*/
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15F))



                    }

                }, Looper.getMainLooper())
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermission) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "???????????????, ??????????????????", Toast.LENGTH_LONG)
                    .show()
                checkGPSState()
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                ) {
                    //???????????????
                    Toast.makeText(this, "??????????????????????????????????????????????????????", Toast.LENGTH_SHORT)
                        .show()
                    requestLocationPermission()
                } else {
                    //?????????????????????
                    Toast.makeText(this, "?????????????????????????????????????????????????????????", Toast.LENGTH_SHORT)
                        .show()
                    AlertDialog.Builder(this)
                        .setTitle("??????????????????")
                        .setMessage("??????????????????????????????????????????????????????????????????????????????")
                        .setPositiveButton("????????????") { _, _ ->
                            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                locationPermission)
                        }
                        .setNegativeButton("??????") { _, _ -> requestLocationPermission() }
                        .show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            gpsPermission -> checkGPSState()
            locationPermission -> requestLocationPermission()
        }
    }

    private fun getParkingPositionMap() {
        parkingRaw = intent.getStringExtra("parkingRaw").toString()
        val parkingName = Gson().fromJson(parkingRaw, RTparking::class.java)
        //?????????????????????
        for (i in 0..parkingName.size - 1) {
            bigArea.add(parkingName.get(i).Name)
        }
        val neededList = arrayListOf("Position","X","Y","Notes","Updatetime")
        gpsLatLong = ParkingInformation(parkingRaw!!,bigArea,neededList).getParkingInformation()
        availableCar = ParkingInformation(parkingRaw!!,bigArea,neededList).getAvailableCar()

    }

    private fun addParkingMarker(){

        mMap.setInfoWindowAdapter(InfoWindowAdapter(mContext))
        mMap.setOnInfoWindowClickListener(this)
        for (i in 0..bigArea.size-1){
            for (o in 0..gpsLatLong.get("${bigArea.get(i)}X")!!.size-1){
                val pinMarker = mMap.addMarker(MarkerOptions()
                    .title(gpsLatLong.get("${bigArea.get(i)}Position")!!.get(o))
                    .position(LatLng(gpsLatLong.get("${bigArea.get(i)}Y")!!.get(o).toDouble(),
                        gpsLatLong.get("${bigArea.get(i)}X")!!.get(o).toDouble()))
                    .snippet("${availableCar.get("${bigArea.get(i)}AvailableCar")!!.get(o)}" +
                            "&????????????: ${gpsLatLong.get("${bigArea.get(i)}Updatetime")!!.get(o)}" +
                            "&${gpsLatLong.get("${bigArea.get(i)}Notes")!!.get(o)}")
                )
            }
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        val money = marker.snippet.toString().split("&")
        AlertDialog.Builder(this)
            .setTitle("????????????")
            .setMessage("${money[2]}")
            .setPositiveButton("OK",null)
            .show()
    }
}