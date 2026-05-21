package com.ilyadev.meowmoments.presentation.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ilyadev.meowmoments.databinding.ItemFactBinding
import com.ilyadev.meowmoments.domain.model.CatFact

class FactListAdapter(
    private val onFactClick: (CatFact) -> Unit
) : ListAdapter<CatFact, FactListAdapter.FactViewHolder>(FactDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolder {
        val binding = ItemFactBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FactViewHolder(binding, onFactClick)
    }

    override fun onBindViewHolder(holder: FactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FactViewHolder(
        private val binding: ItemFactBinding,
        private val onFactClick: (CatFact) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(fact: CatFact) {
            binding.tvFactText.text = fact.text
            binding.tvFactCategory.text = "#${fact.category}"

            binding.root.setOnClickListener {
                onFactClick(fact)
            }
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