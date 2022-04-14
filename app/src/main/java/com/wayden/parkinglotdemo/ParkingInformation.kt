package com.wayden.parkinglotdemo

import android.app.Application
import com.google.gson.Gson
import org.json.JSONArray
import java.util.ArrayList

class ParkingInformation(var rtParkingLotData: String,val bigArea: ArrayList<String>,var wantedInformation: ArrayList<String>) {
    var gpsLatLong :HashMap<String,Double> = hashMapOf()
    var taichung :HashMap<String,ArrayList<String>> = hashMapOf()
    var taichungAvailableCar :HashMap<String,ArrayList<String>> = hashMapOf()
    fun getParkingInformation(): HashMap<String, ArrayList<String>> {
        for (p in 0..wantedInformation.size-1) {
            val parkingName = Gson().fromJson(rtParkingLotData, RTparking::class.java)
            for (o in 0..parkingName.size - 1) {
                var detailArea: ArrayList<String> = arrayListOf()
                val parkingArray =
                    JSONArray(rtParkingLotData).getJSONObject(o).getJSONArray("ParkingLots")
                for (i in 0..parkingArray.length() - 1) {
                    var area = parkingArray.getJSONObject(i).getString(wantedInformation.get(p))
                    detailArea.add(area)
                }
                taichung.put("${bigArea.get(o)}${wantedInformation.get(p)}", detailArea)
            }
        }
        return taichung
    }

    fun getAvailableCar(): HashMap<String, ArrayList<String>>{

            val parkingName = Gson().fromJson(rtParkingLotData, RTparking::class.java)
            for (o in 0..parkingName.size - 1) {
                var detailArea: ArrayList<String> = arrayListOf()
                val parkingArray =
                    JSONArray(rtParkingLotData).getJSONObject(o).getJSONArray("ParkingLots")
                for (i in 0..parkingArray.length() - 1) {
                    if (parkingArray.getJSONObject(i).getString("AvailableCar").toInt() >= 0) {
                        var area = parkingArray.getJSONObject(i).getString("AvailableCar")
                        detailArea.add(area)
                    } else{
                        var area = "0"
                        detailArea.add(area)
                    }
                }
                taichungAvailableCar.put("${bigArea.get(o)}AvailableCar", detailArea)
            }

        return taichungAvailableCar
    }


    }


