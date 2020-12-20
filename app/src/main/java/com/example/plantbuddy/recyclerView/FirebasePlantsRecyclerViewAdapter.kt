package com.example.plantbuddy.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantbuddy.R
import com.example.plantbuddy.model.Plant
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError

class FirebasePlantsRecyclerViewAdapter(options: FirebaseRecyclerOptions<Plant>) : FirebaseRecyclerAdapter<Plant, FirebasePlantsRecyclerViewAdapter.FirebasePlantsViewHolder>(options) {

    class FirebasePlantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val name: TextView = itemView.findViewById(R.id.plant_name_plant_list)
        private val wateringFreq: TextView = itemView.findViewById(R.id.watering_freq_plant_list)
        private val  startTime: TextView = itemView.findViewById(R.id.start_time_plant_list)
        private val  endTime: TextView = itemView.findViewById(R.id.end_time_plant_list)
        private val temp: TextView = itemView.findViewById(R.id.default_temp_plant_list)
        private val  habitat: TextView = itemView.findViewById(R.id.living_habitat_plant_list)
        private val sun: TextView = itemView.findViewById(R.id.sun_exposure_plant_list)

        fun bind(item: Plant){
            name.text = item.plantName
            wateringFreq.text = item.wateringFreq
            startTime.text = item.startTime
            endTime.text = item.endTime
            temp.text = item.temperature
            habitat.text = item.livingHabitat
            sun.text = item.sunExposure
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirebasePlantsViewHolder {
        return FirebasePlantsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.plant_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: FirebasePlantsViewHolder, pos: Int, item: Plant) {
        viewHolder.bind(item)
    }

    override fun onDataChanged() {
        super.onDataChanged()
    }

    override fun onError(error: DatabaseError) {
        super.onError(error)
        Log.d("Read from Firebase Error", error.getMessage())
    }
}