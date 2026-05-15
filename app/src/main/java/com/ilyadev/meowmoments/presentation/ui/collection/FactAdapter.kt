package com.ilyadev.meowmoments.presentation.ui.collection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.databinding.ItemFactBinding
import com.ilyadev.meowmoments.domain.model.CatFact

class FactAdapter(
    private val onFactClick: (CatFact) -> Unit,
    private val onFavoriteClick: ((CatFact) -> Unit)? = null // Новый параметр для клика по звезде
) : ListAdapter<CatFact, FactAdapter.FactViewHolder>(FactDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolder {
        val binding = ItemFactBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FactViewHolder(binding, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: FactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FactViewHolder(
        private val binding: ItemFactBinding,
        private val onFavoriteClick: ((CatFact) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(fact: CatFact) {
            binding.tvFactText.text = fact.text
            binding.tvFactCategory.text = "#${fact.category}"

            // --- ОБНОВЛЕНИЕ ИКОНКИ ЗВЕЗДЫ ---
            binding.ivFavorite.setImageResource(
                if (fact.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
            )

            // --- ОБРАБОТКА КЛИКА ПО ЗВЕЗДЕ ---
            binding.ivFavorite.setOnClickListener {
                onFavoriteClick?.invoke(fact)
            }

            // --- ОБРАБОТКА КЛИКА ПО ВСЕМУ ЭЛЕМЕНТУ ---
            binding.root.setOnClickListener {
                onFactClick(fact)
            }
        }
    }

    object FactDiffCallback : DiffUtil.ItemCallback<CatFact>() {
        override fun areItemsTheSame(oldItem: CatFact, newItem: CatFact): Boolean {
            // Сравниваем по уникальному ID
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CatFact, newItem: CatFact): Boolean {
            // Сравниваем все поля, которые влияют на отображение
            // Включая isFavorite!
            return oldItem == newItem // data class автоматически сравнивает все поля
        }
    }
}