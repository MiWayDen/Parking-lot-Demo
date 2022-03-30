package com.wayden.parkinglotdemo

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.*

class MapActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationPermissionGranted = false
    private val locationPermission = 1
    private val gpsPermission = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        getLocationPermission()

    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale
                (this, Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            AlertDialog.Builder(this)
                .setMessage("此應用程式，需要位置權限才能正常使用")
                .setPositiveButton("確定") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermission)
                }
                .setNegativeButton("取消") { _, _ -> requestLocationPermission() }
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
            //已獲取權限
            Toast.makeText(this, "已獲取到位置權限，可以準備開始獲取經緯度", Toast.LENGTH_LONG)
                .show()
            checkGPSState()
            locationPermissionGranted = true
        } else { //詢問要求獲取權限
            requestLocationPermission()
        }
    }

    private fun checkGPSState() {
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "已開啟GPS定位", Toast.LENGTH_LONG)
                .show()
            getLocation()
        } else {
            AlertDialog.Builder(this)
                .setTitle("未開啟GPS")
                .setMessage("需開啟GPS服務")
                .setPositiveButton("前往開啟") { _, _ ->
                    startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                        gpsPermission)
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    fun getLocation() {
        val locationRequest = com.google.android.gms.location.LocationRequest.create()
        locationRequest.priority = LocationRequest.QUALITY_HIGH_ACCURACY
        //↓更新頻率, 單位毫秒
        locationRequest.interval = 2000

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this,"未獲取完整權限",Toast.LENGTH_SHORT)
            } else {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {

                            Log.d("MapActivity",
                                "緯度:${locationResult.lastLocation.latitude}, 緯度:${locationResult.lastLocation.longitude} ");

                        }
                    }, Looper.myLooper()!!
                )

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
                Toast.makeText(this, "已獲取權限, 可進行下一步", Toast.LENGTH_LONG)
                    .show()
                checkGPSState()
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                ) {
                    //權限被拒絕
                    Toast.makeText(this, "位置權限被拒絕，功能將會無法正常使用", Toast.LENGTH_SHORT)
                        .show()
                    requestLocationPermission()
                } else {
                    //權限被永久拒絕
                    Toast.makeText(this, "位置權限已被關閉，功能將會無法正常使用", Toast.LENGTH_SHORT)
                        .show()
                    AlertDialog.Builder(this)
                        .setTitle("開啟位置權限")
                        .setMessage("此應用程式，位置權限已被永久拒絕，需開啟才能正常使用")
                        .setPositiveButton("前往開啟") { _, _ ->
                            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                locationPermission)
                        }
                        .setNegativeButton("取消") { _, _ -> requestLocationPermission() }
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

}
