package com.freelances.callerauto.presentation.main

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.freelances.callerauto.databinding.ActivityMainBinding
import com.freelances.callerauto.model.ExcelRow
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.setting.SettingActivity
import com.freelances.callerauto.utils.ext.safeClick
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
                readExcelFile(uri)
            }
        }

    private fun pickExcelFile() {
        excelFileLauncher.launch("*/*")
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