package com.freelances.callerauto.presentation.main

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.freelances.callerauto.databinding.ActivityMainBinding
import com.freelances.callerauto.model.ExcelRow
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.setting.SettingActivity
import com.freelances.callerauto.utils.ext.gone
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.ext.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun initViews() {
        initData()
        initAction()
    }

    private fun initData() {
        sharedPreference.isDoneFirstOpen = true
    }

    private fun initAction() {
        binding.apply {
            buttonSetting.safeClick {
                navigateTo(SettingActivity::class.java)
            }
            frAdd.safeClick {
                pickExcelFile()
            }
        }
    }

    private val excelFileLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                Log.d("ExcelFile", "Selected file URI: $uri")
                // TODO: xử lý file Excel
                readExcelFile1(uri)
            }
        }

    private fun pickExcelFile() {
        excelFileLauncher.launch("*/*")
    }

    private fun readExcelFile1(uri: Uri) {
        lifecycleScope.launch {
            binding.frLoading.visible()
            withContext(Dispatchers.IO){
                try {
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
                                Log.d("ExcelHeader", "col${'A' + colIndex} = $columnName")
                            }
                        } else {
                            // Các dòng còn lại là dữ liệu
                            val colA = row.getCell(0)?.toString() ?: ""
                            val colB = row.getCell(1)?.toString() ?: ""
                            val colC = row.getCell(2)?.toString() ?: ""

                            val model = ExcelRow(colA, colB, colC)
                            dataList.add(model)

                            Log.d("ExcelData", "Row ${rowIndex}: $model")
                        }
                    }

                    workbook.close()
                    inputStream?.close()

                    withContext(Dispatchers.Main){
                        binding.frLoading.gone()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main){
                        binding.frLoading.gone()
                    }
                    Toast.makeText(this@MainActivity, "Lỗi khi đọc file Excel", Toast.LENGTH_SHORT).show()
                }
            }
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
                            CellType.NUMERIC -> Log.d("ExcelData", "Numeric: ${cell.numericCellValue}")
                            CellType.BOOLEAN -> Log.d("ExcelData", "Boolean: ${cell.booleanCellValue}")
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