package com.example.plantbuddy.recyclerView

import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantbuddy.R
import com.example.plantbuddy.model.Plant
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.blog_list_item.view.*

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
        private val coverImage: ImageView = itemView.findViewById(R.id.iv_plant_list_cover)

        val dropdown_container: LinearLayout = itemView.findViewById(R.id.dropdown_container)
        val expand_button: ImageView = itemView.findViewById(R.id.expand_activities_button)
        init {
            itemView.setOnClickListener {
                dropdown_container.visibility = if (dropdown_container.visibility == View.VISIBLE)
                    View.GONE
                else View.VISIBLE
                expand_button.visibility = if (expand_button.visibility == View.VISIBLE)
                    View.GONE
                else View.VISIBLE
            }
        }

        fun bind(item: Plant){
            name.text = item.plantName
            wateringFreq.text = item.wateringFreq
            startTime.text = item.startTime
            endTime.text = item.endTime
            temp.text = item.temperature
            habitat.text = item.livingHabitat
            sun.text = item.sunExposure

            if (item.imageUrl.isNullOrEmpty())
            {
                coverImage.visibility = View.GONE
            }
            else
            {
                coverImage.visibility = View.VISIBLE
                Glide
                    .with(itemView.context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .into(coverImage);
            }
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