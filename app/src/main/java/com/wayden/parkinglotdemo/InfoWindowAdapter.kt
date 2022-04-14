package com.wayden.parkinglotdemo

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.info_window.view.*

class InfoWindowAdapter (contex : Context) : GoogleMap.InfoWindowAdapter{

    var mWindow = (contex as Activity).layoutInflater.inflate(R.layout.info_window,null)
    private fun render(marker:Marker,view: View){
        val title = view.info_title
        val availableTitle = view.info_available_title
        val availableCar = view.info_avaiable_car
        val moneyHow = view.info_button_money
        val updateTime = view.info_update_time
        val info = marker.snippet.toString().split("&")

        title.text = marker.title
        availableCar.text = info[0]
        updateTime.text = info[1]

    }

    override fun getInfoContents(marker: Marker): View? {
        render(marker,mWindow)
        return mWindow
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }
}
