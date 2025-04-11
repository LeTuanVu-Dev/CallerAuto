package com.freelances.callerauto.presentation.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.ActivityMainBinding
import com.freelances.callerauto.model.ExcelRow
import com.freelances.callerauto.presentation.adapters.DataHomeAdapter
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.dialog.ConfirmDeleteDialog
import com.freelances.callerauto.presentation.language.LanguageActivity.Companion.selectedPosition
import com.freelances.callerauto.presentation.setting.SettingActivity
import com.freelances.callerauto.utils.ext.gone
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.ext.visible
import com.freelances.callerauto.utils.helper.CallCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private var currentIndex = 0
    private var isCalling = false

    private val dataHomeAdapter: DataHomeAdapter by lazy {
        DataHomeAdapter(::onItemDataClicked,::onItemDataChanger)
    }

    private fun onItemDataClicked(position: Int) {
        selectedPosition = position
        dataHomeAdapter.toggleSelectedItem(position)
    }

    private fun onItemDataChanger() {
        if (dataHomeAdapter.hasSelectedItem()){
            binding.tvDelete.visible()
        }
        else{
            binding.tvDelete.gone()
        }
    }

    private var isSelectAll = false
    override fun initViews() {
        initData()
        initAction()
    }

    private fun initData() {
        sharedPreference.isDoneFirstOpen = true
        setUpAdapter()
        checkShowSelectAll()
    }

    private fun checkShowSelectAll() {
        if (dataHomeAdapter.currentList.size <= 0) {
            binding.lnSelectAll.gone()
            binding.tvDelete.gone()
            binding.frCall.gone()
            binding.ivEmpty.visible()

        } else {
            binding.lnSelectAll.visible()
            binding.frCall.visible()
            binding.ivEmpty.gone()
        }
    }

    private fun setUpAdapter() {
        binding.rcvData.apply {
            adapter = dataHomeAdapter
        }
    }

    private lateinit var confirmDeleteDialog: ConfirmDeleteDialog
    private fun showDialogConfirm() {
        if (!::confirmDeleteDialog.isInitialized) {
            confirmDeleteDialog = ConfirmDeleteDialog(this) {
                dataHomeAdapter.removeAllSelectedItems()
            }
        }
        if (!confirmDeleteDialog.isShowing) {
            confirmDeleteDialog.show()
        }

    }

    private fun initAction() {
        binding.apply {
            buttonSetting.safeClick {
                navigateTo(SettingActivity::class.java)
            }
            frAdd.safeClick {
                pickExcelFile()
            }

            lnSelectAll.safeClick {
                isSelectAll = !isSelectAll
                checkboxAll.setImageResource(if (isSelectAll) R.drawable.ic_checkbox_selected else R.drawable.ic_checkbox_language)
                dataHomeAdapter.selectAllData(isSelectAll)
            }

            tvDelete.safeClick {
                showDialogConfirm()
            }
            frCall.safeClick {
                dataHomeAdapter.currentList.first().nickName?.split(",")?.first()
                    ?.let { callPhoneNumber(this@MainActivity, it) }
            }
        }
    }

    private val excelFileLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                readExcelFile1(uri)
            }
        }
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    // 👇 xử lý file Excel ở đây
                    readExcelFile1(it)
                }
            }
        }


    private fun pickExcelFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" // .xlsx
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // xlsx
                    "application/vnd.ms-excel" // xls
                ))
            }
            filePickerLauncher.launch(intent)
        }
        else{
            excelFileLauncher.launch("*/*")
        }
    }



    private fun readExcelFile1(uri: Uri) {
        lifecycleScope.launch {
            binding.frLoading.visible()
            try {
                withContext(Dispatchers.IO) {
                    val inputStream = contentResolver.openInputStream(uri)
                    val workbook = WorkbookFactory.create(inputStream)
                    val sheet = workbook.getSheetAt(0)

                    val columnNames = mutableListOf<String>()
                    val dataList = mutableListOf<ExcelRow>()

                    for ((rowIndex, row) in sheet.withIndex()) {
                        if (rowIndex == 0) {
                            // Dòng đầu tiên là tiêu đề
                            for (colIndex in 0..2) {
                                val cell = row.getCell(colIndex)
                                val columnName = cell?.toString() ?: "Column$colIndex"
                                columnNames.add(columnName)
                            }
                        } else {
                            // Các dòng còn lại là dữ liệu
                            val name = row.getCell(0)?.toString() ?: ""
                            val phoneNumber = row.getCell(1)?.toString() ?: ""
                            val nickName = row.getCell(2)?.toString() ?: ""

                            val model = ExcelRow(name, phoneNumber, nickName)
                            dataList.add(model)

                        }
                    }
                    withContext(Dispatchers.Main) {
                        dataHomeAdapter.submitList(dataList)
                        checkShowSelectAll()
                    }
                    Log.d("VuLT", "dataList = ${dataHomeAdapter.currentList}")
                    workbook.close()
                    inputStream?.close()
                }
                binding.frLoading.gone()

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("VuLT", "Exception = ${e.message}")
                binding.frLoading.gone()

                Toast.makeText(this@MainActivity, "Lỗi khi đọc file Excel", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun startAutoCall(context: Context) {
        currentIndex = 0
        isCalling = false
        lifecycleScope.launch {
            CallCoordinator.onCallFinished.collect { callOff ->
                if (callOff){
                    delay(sharedPreference.currentTimerEndWaiting * 1000L) // đợi 5s như bạn yêu cầu
                    callNextNumber(context) // Gọi số tiếp theo trong danh sách
                }
            }
        }

        callNextNumber(context)
    }



    private fun callNextNumber(context: Context) {
        if (currentIndex >= dataHomeAdapter.currentList.size) {
            Toast.makeText(context, "Đã gọi hết danh sách", Toast.LENGTH_SHORT).show()
            return
        }

        val phoneNumber = dataHomeAdapter.currentList[currentIndex].nickName?.split(",")?.first()
        if (phoneNumber != null) {
            callPhoneNumber(context,phoneNumber)
        }

        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            isCalling = true
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Chưa có quyền gọi điện thoại", Toast.LENGTH_SHORT).show()
        }
    }


    private fun callPhoneNumber(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Chưa có quyền gọi điện thoại", Toast.LENGTH_SHORT).show()
        }
    }


    private fun readExcelFile(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val workbook = WorkbookFactory.create(inputStream)

            val sheet = workbook.getSheetAt(0) // Lấy sheet đầu tiên

            for (row in sheet) {
                for (colIndex in 0..2) { // Chỉ lấy 3 cột đầu tiên
                    val cell = row.getCell(colIndex)
                    cell?.let {
                        when (cell.cellType) {
                            CellType.STRING -> Log.d("ExcelData", "String: ${cell.stringCellValue}")
                            CellType.NUMERIC -> Log.d(
                                "ExcelData",
                                "Numeric: ${cell.numericCellValue}"
                            )

                            CellType.BOOLEAN -> Log.d(
                                "ExcelData",
                                "Boolean: ${cell.booleanCellValue}"
                            )

                            else -> Log.d("ExcelData", "Other: $cell")
                        }
                    } ?: Log.d("ExcelData", "Cell at column $colIndex is null")
                }
            }

            workbook.close()
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Lỗi khi đọc file Excel", Toast.LENGTH_SHORT).show()
        }
    }
}