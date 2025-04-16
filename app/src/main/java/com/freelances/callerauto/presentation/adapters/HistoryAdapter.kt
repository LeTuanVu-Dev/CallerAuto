package com.freelances.callerauto.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.freelances.callerauto.databinding.ItemHistoryBinding
import com.freelances.callerauto.model.CallLogItem
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : ListAdapter<CallLogItem, HistoryAdapter.DataViewHolder>(TaskDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: CallLogItem) {

            binding.tvNumber.text = item.number
            binding.tvType.text = item.callTypeText
            binding.tvDate.text = item.formattedDate
        }

    }

    companion object TaskDiffCallback : DiffUtil.ItemCallback<CallLogItem>() {
        override fun areItemsTheSame(oldItem: CallLogItem, newItem: CallLogItem): Boolean {
            return oldItem.number == newItem.number && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CallLogItem, newItem: CallLogItem): Boolean {
            return oldItem == newItem
        }
    }
}
