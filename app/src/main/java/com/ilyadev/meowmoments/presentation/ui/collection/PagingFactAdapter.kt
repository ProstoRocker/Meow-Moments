package com.ilyadev.meowmoments.presentation.ui.collection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.databinding.ItemFactBinding
import com.ilyadev.meowmoments.domain.model.CatFact

class PagingFactAdapter(
    private val onFactClick: (CatFact) -> Unit,
    private val onFavoriteClick: ((CatFact) -> Unit)? = null
) : PagingDataAdapter<CatFact, PagingFactAdapter.FactViewHolder>(FactComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolder {
        val binding = ItemFactBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FactViewHolder(binding, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: FactViewHolder, position: Int) {
        val fact = getItem(position)
        if (fact != null) {
            holder.bind(fact)
        }
    }

    inner class FactViewHolder(
        private val binding: ItemFactBinding,
        private val onFavoriteClick: ((CatFact) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(fact: CatFact) {
            binding.tvFactText.text = fact.text
            binding.tvFactCategory.text = "#${fact.category}"

            binding.ivFavorite.setImageResource(
                if (fact.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
            )

            binding.ivFavorite.setOnClickListener {
                onFavoriteClick?.invoke(fact)
            }

            binding.root.setOnClickListener {
                onFactClick(fact)
            }

            if (fact.imageUrl != null) {
                binding.ivFactImage.visibility = View.VISIBLE
                binding.ivFactImage.load(fact.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_cat)
                    error(R.drawable.error_cat)
                }
            } else {
                binding.ivFactImage.visibility = View.GONE
            }
        }
    }

    object FactComparator : DiffUtil.ItemCallback<CatFact>() {
        override fun areItemsTheSame(oldItem: CatFact, newItem: CatFact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CatFact, newItem: CatFact): Boolean {
            return oldItem == newItem
        }
    }
}