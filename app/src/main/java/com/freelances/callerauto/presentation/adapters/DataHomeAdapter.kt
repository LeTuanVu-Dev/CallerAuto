package com.freelances.callerauto.presentation.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.ItemDataBinding
import com.freelances.callerauto.model.ExcelRow
import com.freelances.callerauto.presentation.adapters.DataHomeAdapter.Companion.KEY_NAME
import com.freelances.callerauto.presentation.adapters.DataHomeAdapter.Companion.KEY_NICK_NAME
import com.freelances.callerauto.presentation.adapters.DataHomeAdapter.Companion.KEY_PHONE_NUMBER
import com.freelances.callerauto.utils.ext.safeClick

class DataHomeAdapter(
    private val onClickItem: (Int) -> Unit,
    private val onListChanged: () -> Unit,
    private val onClickMore: (View,ExcelRow) -> Unit,
) : ListAdapter<ExcelRow, DataHomeAdapter.DataViewHolder>(TaskDiffCallbackData) {

    companion object {
        const val KEY_SELECTED = "KEY_SELECTED"
        const val KEY_NAME = "KEY_NAME"
        const val KEY_NICK_NAME = "KEY_NICK_NAME"
        const val KEY_PHONE_NUMBER = "KEY_PHONE_NUMBER"
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
            if (bundle.containsKey(KEY_NAME)) {
                bundle.getString(KEY_NAME)?.let { holder.updateName(it) }
            }
            if (bundle.containsKey(KEY_NICK_NAME)) {
                bundle.getString(KEY_NICK_NAME)?.let { holder.updateNickName(it) }
            }
            if (bundle.containsKey(KEY_PHONE_NUMBER)) {
                bundle.getString(KEY_PHONE_NUMBER)?.let { holder.updatePhone(it) }
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
        onListChanged.invoke()
    }

    fun rename(item: ExcelRow, newName: String) {
        val updatedList = currentList.map {
            if (it == item) {
                it.copy(name = newName)
            } else it
        }
        submitList(updatedList)
    }

    fun reNickname(item: ExcelRow, newNickname: String) {
        val updatedList = currentList.map {
            if (it == item) {
                Log.d("VuLT", "reNickname: $newNickname")
                it.copy(nickName = newNickname)
            } else it
        }
        submitList(updatedList)
    }

    fun rePhoneNumber(item: ExcelRow, newPhoneNumber: String) {
        val updatedList = currentList.map {
            if (it == item) {
                it.copy(phoneNumber = newPhoneNumber)
            } else it
        }
        submitList(updatedList)
    }



    fun hasSelectedItem(): Boolean {
        return currentList.any { it.selected }
    }

    fun getListSelected(): List<ExcelRow> {
        return currentList.filter { it.selected }
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
            binding.ivMore.setOnClickListener { view->
                onClickMore(view,getItem(adapterPosition))
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: ExcelRow) {
            val nameNick = if (item.nickName?.isNotEmpty() == true) {item.nickName} else ""
            updateSelected(item.selected)
            updateName(item.name?:"")
            updateNickName(nameNick)
            updatePhone(item.phoneNumber?:"")
        }

        fun updateName(newName: String) {
            binding.tvName.text = newName
        }
        fun updateNickName(newName: String) {
            binding.tvNickName.text = newName
        }
        fun updatePhone(newName: String) {
            binding.tvPhoneNumber.text = newName
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
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ExcelRow, newItem: ExcelRow): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: ExcelRow, newItem: ExcelRow): Any? {
        val diffBundle = Bundle()
        if (oldItem.selected != newItem.selected) {
            diffBundle.putBoolean(DataHomeAdapter.KEY_SELECTED, newItem.selected)
        }
        if (oldItem.name != newItem.name) {
            diffBundle.putString(KEY_NAME, newItem.name)
        }
        if (oldItem.nickName != newItem.nickName) {
            diffBundle.putString(KEY_NICK_NAME, newItem.nickName)
        }
        if (oldItem.phoneNumber != newItem.phoneNumber) {
            diffBundle.putString(KEY_PHONE_NUMBER, newItem.phoneNumber)
        }
        return if (diffBundle.isEmpty) null else diffBundle
    }
}
