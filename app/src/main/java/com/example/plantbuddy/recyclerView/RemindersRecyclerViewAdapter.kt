package com.example.plantbuddy.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantbuddy.R
import com.example.plantbuddy.model.Plant
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.ObservableSnapshotArray
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.function.Predicate

class RemindersRecyclerViewAdapter(val interaction: Interaction) : RecyclerView.Adapter<RemindersRecyclerViewAdapter.ReminderViewHolder>() {
    interface Interaction
    {
        fun onDeleteAlarm(plant:Plant)
        fun onDataChanged()
    }

    var itemList:ArrayList<Plant> = ArrayList()

    class ReminderViewHolder(itemView: View, val interaction: Interaction) : RecyclerView.ViewHolder(itemView)
    {
        private val name: TextView = itemView.findViewById(R.id.plant_name_plant_list)
        private val coverImage: ImageView = itemView.findViewById(R.id.iv_plant_list_cover)
        private val deleteImage: ImageView = itemView.findViewById(R.id.iv_delete)
        private val freqency: TextView = itemView.findViewById(R.id.tv_freq)

        private lateinit var item:Plant
        private lateinit var dialog: AlertDialog

        fun bind(item: Plant){
            this.item = item

            name.text = item.plantName
            freqency.text = if (item.wateringFreq == "1")
                                "Every ${item.wateringFreq} day at ${item.startTime}"
                            else
                                "Every ${item.wateringFreq} days at ${item.startTime}"
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

            deleteImage.setOnClickListener(View.OnClickListener {
                interaction.onDeleteAlarm(item)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.reminder_list_item, parent, false), interaction
        )
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }
}