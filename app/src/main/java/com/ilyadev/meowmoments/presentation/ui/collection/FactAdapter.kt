package com.ilyadev.meowmoments.presentation.ui.collection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ilyadev.meowmoments.databinding.ItemFactBinding
import com.ilyadev.meowmoments.domain.model.CatFact

class FactAdapter : ListAdapter<CatFact, FactAdapter.FactViewHolder>(FactDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolder {
        val binding = ItemFactBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FactViewHolder(private val binding: ItemFactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(fact: CatFact) {
            binding.tvFactText.text = fact.text
            binding.tvFactCategory.text = "#${fact.category}"
        }
    }

    object FactDiffCallback : DiffUtil.ItemCallback<CatFact>() {
        override fun areItemsTheSame(oldItem: CatFact, newItem: CatFact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CatFact, newItem: CatFact): Boolean {
            return oldItem == newItem
        }
    }
}