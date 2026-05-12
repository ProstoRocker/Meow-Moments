package com.ilyadev.meowmoments.presentation.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.databinding.ItemCalendarDayBinding
import java.time.LocalDate

class CalendarDayAdapter(
    private val onDayClick: (LocalDate) -> Unit
) : ListAdapter<CalendarDayItem, CalendarDayAdapter.DayViewHolder>(DayDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DayViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalendarDayItem) {
            binding.root.setOnClickListener { onDayClick(item.date) }

            if (item.isCurrentMonth) {
                binding.tvDay.text = item.date.dayOfMonth.toString()
                binding.root.isEnabled = true

                when {
                    item.isToday -> {
                        binding.root.setBackgroundResource(R.drawable.calendar_today_background)
                        binding.tvDay.setTextColor(itemView.context.getColor(R.color.calendar_today_text))
                    }

                    item.isCollected -> {
                        binding.root.setBackgroundResource(R.drawable.calendar_collected_day_background)
                        binding.tvDay.setTextColor(itemView.context.getColor(R.color.calendar_collected_day_text))
                    }

                    else -> {
                        binding.root.setBackgroundColor(itemView.context.getColor(R.color.calendar_day_background))
                        binding.tvDay.setTextColor(itemView.context.getColor(R.color.calendar_day_text))
                    }
                }
            } else {
                binding.tvDay.text = ""
                binding.root.isEnabled = false
            }
        }
    }

    object DayDiffCallback : DiffUtil.ItemCallback<CalendarDayItem>() {
        override fun areItemsTheSame(oldItem: CalendarDayItem, newItem: CalendarDayItem): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(
            oldItem: CalendarDayItem,
            newItem: CalendarDayItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}

data class CalendarDayItem(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isCollected: Boolean
)