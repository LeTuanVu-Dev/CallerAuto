package com.freelances.callerauto.presentation.history

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CALL_LOG),
                    213
                )
            } else {
                // Quyền đã được cấp → tiếp tục lấy call log
                getData()
            }
        } else {
            getData()
        }
    }

    private fun getData() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                dataList.addAll(getCallLog(this@HistoryActivity))
            }
            historyAdapter.submitList(dataList)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 213 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getData()
        } else {
            Toast.makeText(this, "Bạn chưa cấp quyền xem lịch sử cuộc gọi", Toast.LENGTH_SHORT)
                .show()
        }
    }

}
