package com.wayden.parkinglotdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_from_detail_area.view.*


class adapter : RecyclerView.Adapter<MyAdapter>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter {
        return MyAdapter(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_from_detail_area, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyAdapter, position: Int) {

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}

class MyAdapter(view:View):RecyclerView.ViewHolder(view){
    val position = view.position
    val totalCar = view.totalCar
    val availableCar = view.availableCar
//    val notes = view.notes
    val updateTime = view.updatetime
}