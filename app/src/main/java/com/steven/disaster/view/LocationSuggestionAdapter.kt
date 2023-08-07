package com.steven.disaster.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.steven.disaster.databinding.LocationSuggestionItemBinding

class LocationSuggestionAdapter (private val onItemClicked: (String) -> Unit) :
    ListAdapter<String, LocationSuggestionAdapter.SuggestionLocationViewHolder>(
        object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    inner class SuggestionLocationViewHolder(
        private val binding: LocationSuggestionItemBinding,
        onItemClicked: (String) -> Unit
    ) :
        ViewHolder(binding.root) {
        private val tvLocationSuggestion by lazy { binding.tvLocationSuggestion }

        init {
            binding.tvLocationSuggestion.setOnClickListener {
                onItemClicked(tvLocationSuggestion.text.toString())
            }
        }

        fun bind(location: String?) {
            tvLocationSuggestion.text = location
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuggestionLocationViewHolder {
        val view = LocationSuggestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuggestionLocationViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: SuggestionLocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}