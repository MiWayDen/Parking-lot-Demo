package com.wayden.parkinglotdemo




import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_main_big_area.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.URL


class MainActivity : AppCompatActivity() {
    private lateinit var rtParkingLotData: String
    var bigArea: ArrayList<String> = arrayListOf()
    var taichung: HashMap<String, MutableList<String>> = hashMapOf()
    val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //recycler view
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this)

        //連線即時剩餘車位
        getParkingInfo()

        refresh.setOnClickListener {
            bigArea = arrayListOf()
            getParkingInfo() }

        }

    private fun getParkingInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            rtParkingLotData =
                URL("https://motoretag.taichung.gov.tw/DataAPI/api/ParkingSpotListAPI").readText()
            val parkingName = Gson().fromJson(rtParkingLotData, RTparking::class.java)

            //抓出大區域名稱
            for (i in 0..parkingName.size - 1) {
                bigArea.add(parkingName.get(i).Name)
            }
            Log.d(TAG, "bigArea:${bigArea}: ")

            runOnUiThread {
                recycler.adapter = FunctionAdapter()
                FunctionAdapter().notifyDataSetChanged()
            }

            //抓取更新時間 :因更新時間在jsonArray內的array, 需要多做一次轉換才能抓到時間
            val innerArray =
                JSONArray(rtParkingLotData).getJSONObject(0).getString("ParkingLots")
            val upTime = JSONArray(innerArray).getJSONObject(0).getString("Updatetime")
            runOnUiThread {
                updateTime.text = "更新時間:$upTime"
            }
        }
    }

    //recyclerView
        inner class FunctionAdapter : RecyclerView.Adapter<FunctionHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunctionHolder {
                return FunctionHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_main_big_area, parent, false)
                )
            }

            override fun onBindViewHolder(holder: FunctionHolder, position: Int) {
                holder.area.text = bigArea.get(position)
                holder.itemView.setOnClickListener {
                    functionClicked(position)
                }
            }

            override fun getItemCount(): Int {
                return bigArea.size
            }

        }

        private fun functionClicked(position: Int) {
            val intent = Intent(this, DetailArea::class.java)
            var click = position
            intent.putExtra("click", click)
            intent.putExtra("bigArea", bigArea)
            intent.putExtra("rtParkingLotData", rtParkingLotData)
            for (i in 0..bigArea.size) {
                if (i == position) {
                    startActivity(intent)
                }
            }
            Log.d(TAG, "position: $click");
            val c = intent.getIntExtra("click", -1)
            Log.d(TAG, "click:$c ");
        }

    }

    class FunctionHolder(view: View) : RecyclerView.ViewHolder(view) {
        var area = view.area
    }





