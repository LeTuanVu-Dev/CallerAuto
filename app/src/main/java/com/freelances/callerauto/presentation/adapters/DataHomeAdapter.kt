package com.freelances.callerauto.presentation.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.ItemDataBinding
import com.freelances.callerauto.model.ExcelRow
import com.freelances.callerauto.utils.ext.safeClick

class DataHomeAdapter(
    private val onClickItem: (Int) -> Unit,
    private val onListChanged: () -> Unit,
) : ListAdapter<ExcelRow, DataHomeAdapter.DataViewHolder>(TaskDiffCallbackData) {

    companion object {
        const val KEY_SELECTED = "KEY_SELECTED"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val bundle = payloads[0] as Bundle
            if (bundle.containsKey(KEY_SELECTED)) {
                holder.updateSelected(bundle.getBoolean(KEY_SELECTED))
            }
        }
    }

    fun selectAllData(isSelected: Boolean) {
        val updatedList = currentList.map { item ->
            item.copy(selected = isSelected)
        }
        submitList(updatedList)
    }

    fun removeAllSelectedItems() {
        val updatedList = currentList.filter { !it.selected }
        submitList(updatedList)
    }

    override fun onCurrentListChanged(
        previousList: MutableList<ExcelRow>,
        currentList: MutableList<ExcelRow>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        onListChanged?.invoke()
    }


    fun hasSelectedItem(): Boolean {
        return currentList.any { it.selected }
    }

    fun toggleSelectedItem(position: Int) {
        if (position < 0 || position >= currentList.size) return

        val updatedList = currentList.mapIndexed { index, item ->
            if (index == position) {
                item.copy(selected = !item.selected) // toggle trạng thái
            } else {
                item
            }
        }

        submitList(updatedList)
    }

    inner class DataViewHolder(private val binding: ItemDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.safeClick {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickItem(position)
                }
            }
        }

        fun bind(item: ExcelRow) {
            binding.tvName.text = item.name?: ""
            binding.tvPhoneNumber.text = item.nickName?.split(",")?.first() ?: ""
            updateSelected(item.selected)
        }

        fun updateSelected(isSelected: Boolean) {
            binding.checkboxData.setImageResource(
                if (isSelected) R.drawable.ic_checkbox_selected else R.drawable.ic_checkbox_language
            )
        }
    }
}

object TaskDiffCallbackData : DiffUtil.ItemCallback<ExcelRow>() {
    override fun areItemsTheSame(oldItem: ExcelRow, newItem: ExcelRow): Boolean {
        return oldItem.phoneNumber == newItem.phoneNumber
    }

    override fun areContentsTheSame(oldItem: ExcelRow, newItem: ExcelRow): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: ExcelRow, newItem: ExcelRow): Any? {
        val diffBundle = Bundle()
        if (oldItem.selected != newItem.selected) {
            diffBundle.putBoolean(DataHomeAdapter.KEY_SELECTED, newItem.selected)
        }
        return if (diffBundle.isEmpty) null else diffBundle
    }
}
