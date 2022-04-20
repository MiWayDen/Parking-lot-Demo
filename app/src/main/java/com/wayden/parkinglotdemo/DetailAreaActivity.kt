package com.wayden.parkinglotdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_detail_area.*
import kotlinx.android.synthetic.main.recycler_from_detail_area.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.net.URL
import java.util.ArrayList

class DetailArea : AppCompatActivity() {

    var clickPosition = 0
    var bigAreaPosition :HashMap<String,ArrayList<String>> = hashMapOf()
    var bigAreaPositionAvailableCar :HashMap<String,ArrayList<String>> = hashMapOf()
    var bigArea = arrayListOf<String>()
    val TAG = DetailArea::class.java.simpleName
    var rtParkingLotData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_area)

        //RecyclerView
        detailRecycler.setHasFixedSize(true)
        detailRecycler.layoutManager = LinearLayoutManager(this)
        detailRecycler.adapter = DetailAreaAdapter()
//        DetailAreaAdapter().notifyDataSetChanged()

        clickPosition = intent.getIntExtra("click",-1)
//        Log.d(TAG, "點擊位置:$clickPosition ");
        bigArea = intent.getStringArrayListExtra("bigArea")!!
//        Log.d(TAG, "大區域:${bigArea} ");
        rtParkingLotData = intent.getStringExtra("rtParkingLotData").toString()

        val neededList = arrayListOf<String>("Position","TotalCar","Notes","Updatetime","X","Y")
        val test = ParkingInformation(rtParkingLotData!!,bigArea,neededList)
        bigAreaPosition = test.getParkingInformation()
        //剩餘車位會有負數值(-1)需另外拉出來處理
        bigAreaPositionAvailableCar = test.getAvailableCar()
        //標題
        realTimeTitle.text = ("${bigArea.get(clickPosition)}")

        var availableCar = ""

    }

    //重新整理按鈕



    inner class DetailAreaAdapter :RecyclerView.Adapter<DetailViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
            return DetailViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_from_detail_area,parent,false))
        }


        override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
           holder.position.text ="停車場:${bigAreaPosition.get("${bigArea.get(clickPosition)}Position")!!.get(position)}"
           holder.totalCar.text ="總車位:${bigAreaPosition.get("${bigArea.get(clickPosition)}TotalCar")!!.get(position)}"
           holder.availableCar.text ="剩餘車位:${bigAreaPositionAvailableCar.get("${bigArea.get(clickPosition)}AvailableCar")!!.get(position)}"
//           holder.notes.text ="計費規則:${bigAreaPosition.get("${bigArea.get(clickPosition)}Notes")!!.get(position)}"
            holder.updateTime.text ="更新時間:${bigAreaPosition.get("${bigArea.get(clickPosition)}Updatetime")!!.get(position)}"
            holder.getNotes.text = "點擊獲取收費資訊"
            holder.getNotes.setOnClickListener {
                functionClicked(position)
            }
        }

        override fun getItemCount(): Int {
            return bigAreaPosition.get("${bigArea.get(clickPosition)}Position")!!.size
        }
    }
    //取得收費資訊按鈕
    private fun functionClicked(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("收費資訊")
            .setMessage("${bigAreaPosition.get("${bigArea.get(clickPosition)}Notes")!!.get(position)}")
            .setPositiveButton("OK",null)
            .show()
    }

    class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var position = view.position
        var totalCar = view.totalCar
        var availableCar = view.availableCar
        var updateTime = view.updatetime
        var getNotes = view.getNotes
    }

}