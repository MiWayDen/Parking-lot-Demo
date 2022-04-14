package com.wayden.parkinglotdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.net.URL
import java.util.ArrayList
import kotlin.coroutines.CoroutineContext

class HomePageActivity : AppCompatActivity() ,CoroutineScope{
    private val TAG = "HomePageActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val goGoogleMap = Intent(this,ParkingMapsActivity::class.java)
        launch {
            var parkingRaw =
                URL("https://motoretag.taichung.gov.tw/DataAPI/api/ParkingSpotListAPI").readText()
            goGoogleMap.putExtra("parkingRaw",parkingRaw)
        }
        gps_button.setOnClickListener {_ -> startActivity(goGoogleMap) }

        val goRealtime = Intent(this,MainActivity::class.java)
        realtime_button.setOnClickListener { _->startActivity(goRealtime) }

    }

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

}