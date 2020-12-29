package com.example.plantbuddy.recyclerView

import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantbuddy.R
import com.example.plantbuddy.model.Plant
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError

class FirebasePlantsRecyclerViewAdapter(options: FirebaseRecyclerOptions<Plant>, val interaction: Interaction) : FirebaseRecyclerAdapter<Plant, FirebasePlantsRecyclerViewAdapter.FirebasePlantsViewHolder>(options) {
    interface Interaction
    {
        fun onDeletePlant(plant:Plant)
        fun onEditPlant(plant:Plant)
        fun onDataChanged()
    }

    class FirebasePlantsViewHolder(itemView: View, val interaction: Interaction) : RecyclerView.ViewHolder(itemView)
    {
        private val name: TextView = itemView.findViewById(R.id.plant_name_plant_list)
        private val wateringFreq: TextView = itemView.findViewById(R.id.watering_freq_plant_list)
        private val  timeInterval: TextView = itemView.findViewById(R.id.time_plant_list)
        private val temp: TextView = itemView.findViewById(R.id.default_temp_plant_list)
        private val  habitat: TextView = itemView.findViewById(R.id.living_habitat_plant_list)
        private val sun: TextView = itemView.findViewById(R.id.sun_exposure_plant_list)
        private val coverImage: ImageView = itemView.findViewById(R.id.iv_plant_list_cover)
        private val moreMenu: ImageView = itemView.findViewById(R.id.iv_more_menu)

        private lateinit var item:Plant
        private lateinit var dialog: AlertDialog

        val plantTypeList = itemView.context.resources.getStringArray(R.array.plant_indoor_outdoor)
        val sunExposureLevelList = itemView.context.resources.getStringArray(R.array.sun_exposure_level)

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
            this.item = item
            createMenuDialog()

            name.text = item.plantName
            wateringFreq.text = if(item.wateringFreq == "1")
                "Every ${item.wateringFreq} day"
            else
                "Every ${item.wateringFreq} days"

            timeInterval.text = "Preferred time: ${item.startTime} - ${item.endTime}"
            temp.text = "${item.temperature} \u2103"
            habitat.text = plantTypeList[item.livingHabitat]
            sun.text = sunExposureLevelList[item.sunExposure]

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

            moreMenu.setOnClickListener(View.OnClickListener {
                dialog.show()
            })
        }

        private fun createMenuDialog()
        {
            val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
            val options = arrayOf("Delete ${item.plantName}", "Edit ${item.plantName}")
            builder.setItems(options, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when(which)
                    {
                        0 -> interaction.onDeletePlant(item)
                        1 -> interaction.onEditPlant(item)
                    }
                }
            })
            dialog = builder.create()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirebasePlantsViewHolder {
        return FirebasePlantsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.plant_list_item, parent, false), interaction
        )
    }

    override fun onBindViewHolder(viewHolder: FirebasePlantsViewHolder, pos: Int, item: Plant) {
        viewHolder.bind(item)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        interaction.onDataChanged()
    }

    override fun onError(error: DatabaseError) {
        super.onError(error)
        Log.d("Read from Firebase Error", error.getMessage())
    }
}