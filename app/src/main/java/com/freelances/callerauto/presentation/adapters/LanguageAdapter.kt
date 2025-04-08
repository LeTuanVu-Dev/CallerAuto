package com.freelances.callerauto.presentation.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.ItemLanguageBinding
import com.freelances.callerauto.model.LanguageModel
import com.freelances.callerauto.presentation.adapters.LanguageAdapter.Companion.KEY_SELECTED
import com.freelances.callerauto.utils.ext.safeClick

class LanguageAdapter(private val onClickItemLanguage: (Int) -> Unit) :
    ListAdapter<LanguageModel, LanguageAdapter.LanguageViewHolder>(TaskDiffCallback()) {
    companion object {
        const val KEY_SELECTED = "KEY_SELECTED"
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LanguageViewHolder {
        return LanguageViewHolder(
            ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: LanguageViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val bundle = payloads[0] as Bundle
            if (bundle.containsKey(KEY_SELECTED)) {
                holder.updateSelected(bundle.getBoolean(KEY_SELECTED))
            }
        }
    }

    fun setSelectedLanguage(iso: String) {
        val updatedList =
            currentList.map { it.copy(isSelected = it.iso == iso) }
        // Đảm bảo item thứ 3 hiển thị `animTutorial` nếu không có item nào được chọn
        val finalList = updatedList.mapIndexed { _, item ->
            item.copy()
        }

        submitList(finalList)
    }

    fun setSelectedLanguage(position: Int) {
        if (position < 0 || position >= currentList.size) return // Tránh lỗi nếu index sai

        val updatedList = currentList.mapIndexed { index, item ->
            item.copy(isSelected = index == position)
        }
        // Đảm bảo item thứ 3 hiển thị `animTutorial` nếu không có item nào được chọn
        val finalList = updatedList.mapIndexed { _, item ->
            item.copy()
        }

        submitList(finalList)
    }


    inner class LanguageViewHolder(private val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.safeClick {
                onClickItemLanguage(layoutPosition)
            }
        }

        fun bind(languageModel: LanguageModel) {
            // Update your view holder with the languageModel data
            binding.ivFlag.setImageResource(languageModel.flag)
            binding.tvLanguageName.text = languageModel.localeName
            updateSelected(languageModel.isSelected)
        }

        // Define your view holder layout here
        fun updateSelected(isSelected: Boolean) {
            binding.checkboxLanguage.setImageResource(if (isSelected) R.drawable.ic_checkbox_selected else R.drawable.ic_checkbox_language)
        }
    }

}

class TaskDiffCallback : DiffUtil.ItemCallback<LanguageModel>() {
    override fun areItemsTheSame(oldItem: LanguageModel, newItem: LanguageModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LanguageModel, newItem: LanguageModel): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: LanguageModel, newItem: LanguageModel): Any? {
        val diffBundle = Bundle()
        if (oldItem.isSelected != newItem.isSelected) {
            diffBundle.putBoolean(KEY_SELECTED, newItem.isSelected)
        }
        return if (diffBundle.size() == 0) null else diffBundle
    }
}