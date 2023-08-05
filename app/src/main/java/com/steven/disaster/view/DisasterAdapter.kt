package com.steven.disaster.view

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.steven.disaster.R
import com.steven.disaster.data.response.GeometriesItem
import com.steven.disaster.databinding.DisasterItemBinding
import com.steven.disaster.utils.SupportedArea

class DisasterAdapter : ListAdapter<GeometriesItem?, DisasterAdapter.DisasterViewHolder>(
    object : DiffUtil.ItemCallback<GeometriesItem?>() {
        override fun areItemsTheSame(oldItem: GeometriesItem, newItem: GeometriesItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: GeometriesItem, newItem: GeometriesItem): Boolean {
            return oldItem == newItem
        }
    }
) {
    inner class DisasterViewHolder(private val binding: DisasterItemBinding) :
        ViewHolder(binding.root) {
        private val imgDisaster by lazy { binding.imgDisaster }
        private val tvDisasterType by lazy { binding.tvDisasterType }
        private val tvCreatedBy by lazy { binding.tvCreatedBy }
        private val tvCreatedAt by lazy { binding.tvCreatedAt }
        private val tvLocation by lazy { binding.tvLocation }
        private val tvStatus by lazy { binding.tvStatus }

        fun bind(disaster: GeometriesItem?) {
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

            Glide.with(itemView.context)
                .load(
                    disaster?.properties?.imageUrl
                        ?: ColorDrawable(itemView.context.getColor(R.color.light_grey))
                )
                .into(imgDisaster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisasterViewHolder =
        DisasterViewHolder(
            DisasterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: DisasterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}