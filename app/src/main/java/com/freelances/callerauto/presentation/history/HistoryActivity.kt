package com.freelances.callerauto.presentation.history

import androidx.lifecycle.lifecycleScope
import com.freelances.callerauto.databinding.ActivityHistoryBinding
import com.freelances.callerauto.model.CallLogItem
import com.freelances.callerauto.presentation.adapters.HistoryAdapter
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.helper.CallCoordinator.getCallLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : BaseActivity<ActivityHistoryBinding>(ActivityHistoryBinding::inflate) {
    private val historyAdapter: HistoryAdapter by lazy {
        HistoryAdapter()
    }
    private var dataList = mutableListOf<CallLogItem>()

    override fun initViews() {
        setUpAdapter()
    }

    private fun setUpAdapter() {
        binding.buttonBack.safeClick {
            finish()
        }
        binding.rcvHistory.apply {
            adapter = historyAdapter
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                dataList.addAll(getCallLog(this@HistoryActivity))
            }
            historyAdapter.submitList(dataList)
        }
    }
}