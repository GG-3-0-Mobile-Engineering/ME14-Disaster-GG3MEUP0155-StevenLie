package com.steven.disaster.view

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.steven.disaster.R
import com.steven.disaster.data.response.GeometriesItem
import com.steven.disaster.databinding.DisasterItemBinding
import com.steven.disaster.utils.SupportedArea

class DisasterAdapter : RecyclerView.Adapter<DisasterAdapter.DisasterViewHolder>() {
    private var listDisaster = ArrayList<GeometriesItem?>()

    fun setDisaster(disaster: List<GeometriesItem?>?) {
        if (disaster == null) return
        this.listDisaster.clear()
        this.listDisaster.addAll(disaster)
    }

    inner class DisasterViewHolder(private val binding: DisasterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(disaster: GeometriesItem?) {
            with(binding) {
                tvDisasterType.text = disaster?.properties?.disasterType
                tvCreatedBy.text =
                    itemView.context.getString(R.string.created_by, disaster?.properties?.source)
                tvCreatedAt.text =
                    itemView.context.getString(
                        R.string.created_at,
                        disaster?.properties?.createdAt?.slice(0..9)
                    )
                tvLocation.text = itemView.context.getString(
                    R.string.location,
                    SupportedArea.area[disaster?.properties?.tags?.instanceRegionCode]
                )
                tvStatus.text =
                    itemView.context.getString(R.string.status, disaster?.properties?.status)
                if (disaster?.properties?.imageUrl != null) {
                    Glide.with(itemView.context)
                        .load(disaster.properties.imageUrl)
                        .into(imgDisaster)
                } else {
                    Glide.with(itemView.context)
                        .load(ColorDrawable(itemView.context.getColor(R.color.light_grey)))
                        .into(imgDisaster)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisasterViewHolder =
        DisasterViewHolder(
            DisasterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = listDisaster.size

    override fun onBindViewHolder(holder: DisasterViewHolder, position: Int) {
        val disaster = listDisaster[position]
        holder.bind(disaster)
    }
}