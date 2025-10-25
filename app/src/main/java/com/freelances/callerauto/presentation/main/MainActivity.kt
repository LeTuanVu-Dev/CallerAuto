package com.freelances.callerauto.presentation.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.ActivityMainBinding
import com.freelances.callerauto.databinding.LayoutMenuSortBinding
import com.freelances.callerauto.databinding.LayoutMoreBinding
import com.freelances.callerauto.di.Carrier
import com.freelances.callerauto.model.ExcelRow
import com.freelances.callerauto.presentation.adapters.DataHomeAdapter
import com.freelances.callerauto.presentation.adapters.HistoryAdapter
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.dialog.ConfirmDeleteDialog
import com.freelances.callerauto.presentation.dialog.RenameDialog
import com.freelances.callerauto.presentation.history.HistoryActivity
import com.freelances.callerauto.presentation.language.LanguageActivity.Companion.selectedPosition
import com.freelances.callerauto.presentation.setting.SettingActivity
import com.freelances.callerauto.utils.ext.gone
import com.freelances.callerauto.utils.ext.hideSoftKeyboard
import com.freelances.callerauto.utils.ext.isAPI28OrHigher
import com.freelances.callerauto.utils.ext.isDefaultDialer
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.ext.tap
import com.freelances.callerauto.utils.ext.visible
import com.freelances.callerauto.utils.helper.CallCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    companion object {
        var displayName = ""
        var phoneNumber = ""
    }

    private var currentIndex = 0
    private var currentRepeat = 0
    private var isCalling = false
    private var dataList = mutableListOf<ExcelRow>()
    private var filteredList = arrayListOf<ExcelRow>()
    private var isStopped = false

    private val dataHomeAdapter: DataHomeAdapter by lazy {
        DataHomeAdapter(::onItemDataClicked, ::onItemDataChanger) { view, item ->
            showPopupMore(view, item)
        }
    }

    private fun onItemDataClicked(position: Int) {
        selectedPosition = position
        dataHomeAdapter.toggleSelectedItem(position)
    }

    private fun onItemDataChanger() {
        if (dataHomeAdapter.hasSelectedItem()) {
            binding.tvDelete.visible()
        } else {
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
            binding.ivSort.gone()
            binding.frSearch.gone()
            binding.ivEmpty.visible()
            binding.tvSeeHistory.gone()

        } else {
            binding.lnSelectAll.visible()
            binding.frCall.visible()
            binding.frSearch.visible()
            binding.ivSort.visible()
            binding.ivEmpty.gone()
            binding.tvSeeHistory.visible()

        }
    }

    private fun setUpAdapter() {
        binding.rcvData.apply {
            adapter = dataHomeAdapter
        }
    }


    private fun showDialogCountTime() {
        countTimeEndLifted{
            lifecycleScope.launch {
                isCalling = true
                if (isActive) {
                    currentIndex++
                    sharedPreference.currentAutoCallPosition = currentIndex
                    callNextNumber(this@MainActivity) // Gọi số tiếp theo trong danh sách
                }
            }
        }

    }


    private lateinit var confirmDeleteDialog: ConfirmDeleteDialog
    private fun showDialogConfirm() {
        if (!::confirmDeleteDialog.isInitialized) {
            confirmDeleteDialog = ConfirmDeleteDialog(this) {
                val updatedList = dataList.filter { it.selected }
                dataList.removeAll(updatedList)
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

            buttonNo.tap {
                stopAutoCall()
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
                if (!dataHomeAdapter.hasSelectedItem()) {
                    Toast.makeText(this@MainActivity, "Bạn chưa chọn số nào", Toast.LENGTH_SHORT)
                        .show()
                    return@safeClick
                }
                currentRepeat = sharedPreference.currentNumberRepeat
                startAutoCall(this@MainActivity)
            }

            ivSort.tap {
                showPopupMenuSort(ivSort)
            }

            tvSeeHistory.safeClick {
                navigateTo(HistoryActivity::class.java)
            }

            frSearch.tap {
                lnSearch.visible()
                frSearch.gone()
            }

            tvCancel.tap {
                frSearch.visible()
                lnSearch.gone()
                hideSoftKeyboard(this@MainActivity)
                edtInputSearch.setText("")
                dataHomeAdapter.submitList(dataList)
            }

            edtInputSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterWithSearch(s.toString().trim())
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" // .xlsx
                putExtra(
                    Intent.EXTRA_MIME_TYPES, arrayOf(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // xlsx
                        "application/vnd.ms-excel" // xls
                    )
                )
            }
            filePickerLauncher.launch(intent)
        } else {
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
                    dataList = arrayListOf()
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
                            val type = getCarrierFromPhoneNumber(phoneNumber)
                            val model = ExcelRow(
                                name = name,
                                phoneNumber = phoneNumber,
                                nickName = nickName,
                                type = type
                            )
                            dataList.add(model)

                        }
                    }
                    withContext(Dispatchers.Main) {
                        dataHomeAdapter.submitList(dataList)
                        checkShowSelectAll()
                    }
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

    private fun getCarrierFromPhoneNumber(phone: String): Carrier {
        val normalizedPhone = phone
            .replace("[^\\d]".toRegex(), "") // Bỏ ký tự không phải số
            .let {
                when {
                    it.startsWith("84") -> it.replaceFirst("84", "0")
                    it.startsWith("0") -> it
                    else -> "0$it"
                }
            }

        val prefix = normalizedPhone.take(3)

        return when (prefix) {
            // Viettel
            "086", "096", "097", "098", "032", "033", "034", "035", "036", "037", "038", "039" -> Carrier.VIETTEL

            // Mobifone
            "089", "090", "093", "070", "076", "077", "078", "079" -> Carrier.MOBIFONE

            // Vinaphone
            "088", "091", "094", "081", "082", "083", "084", "085" -> Carrier.VINAPHONE

            // Vietnamobile
            "092", "056", "058" -> Carrier.VIETNAMOBILE

            // Gmobile
            "099", "059" -> Carrier.GMOBILE

            else -> Carrier.VIETTEL
        }
    }

    private fun startAutoCall(context: Context) {
        if (dataHomeAdapter.getListSelected().size == dataHomeAdapter.currentList.size) {
            currentIndex = sharedPreference.currentAutoCallPosition
        } else {
            currentIndex = 0
        }
        isCalling = false
        isStopped = false // Bắt đầu lại thì không dừng nữa
        lifecycleScope.launch {
            CallCoordinator.onCallFinished.collect { callOff ->
                if (callOff && !isStopped) {
                    currentEndLifted = sharedPreference.currentTimerEndWaiting
                    if (currentIndex < dataHomeAdapter.getListSelected().size || (sharedPreference.stateRepeatList && currentRepeat > 0)) {
                        binding.lnTimeOut.visible()
                        showDialogCountTime()
                    }
                }
            }
        }
        callNextNumber(context)
    }


    private fun callNextNumber(context: Context) {
        if (isStopped) return // Nếu đã dừng thì không gọi nữa
        if (currentIndex >= dataHomeAdapter.getListSelected().size) {
            Toast.makeText(context, "Đã gọi hết danh sách", Toast.LENGTH_SHORT).show()
            sharedPreference.currentAutoCallPosition = 0
            if (sharedPreference.stateRepeatList && currentRepeat > 0) {
                currentIndex = 0
                currentRepeat--
            } else {
                return
            }
        }

        phoneNumber = dataHomeAdapter.getListSelected()[currentIndex].phoneNumber.toString()
        displayName = dataHomeAdapter.getListSelected()[currentIndex].name.toString()
        if (isAPI28OrHigher){
            placeCall(context, phoneNumber)
        }
        else{
            callPhoneNumber(context, phoneNumber)
        }
    }
    private fun placeCall(context: Context, phoneNumber: String) {
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED) {

            val uri = Uri.fromParts("tel", phoneNumber, null)
            val extras = Bundle()

            telecomManager.placeCall(uri, extras)

        } else {
            Toast.makeText(context, "Chưa có quyền gọi điện thoại", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopAutoCall() {
        isStopped = true
        isCalling = false
        currentRepeat = 0
        binding.lnTimeOut.gone()
    }

    private fun callPhoneNumber(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (isDefaultDialer(this)) {
            isCalling = true
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Chưa có quyền gọi điện thoại", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showRenameDialog(textCurrent: String, item: ExcelRow, type: String) {
        val dialogInputNewDataListener = RenameDialog.newInstance { newName ->
            when (type) {
                "name" -> {
                    dataHomeAdapter.rename(item, newName)
                }

                "nickName" -> {
                    dataHomeAdapter.reNickname(item, newName)
                }

                else -> {
                    dataHomeAdapter.rePhoneNumber(item, newName)
                }
            }
        }
        dialogInputNewDataListener.setTimeCurrent(textCurrent)
        dialogInputNewDataListener.show(
            supportFragmentManager,
            "show_gotoSetting_listener"
        )
    }

    private fun showPopupMore(view: View, item: ExcelRow) {
        val layoutInflater = LayoutInflater.from(this)
        val binding1 = LayoutMoreBinding.inflate(layoutInflater)
        val popupMenu = PopupWindow(this)
        popupMenu.contentView = binding1.root
        popupMenu.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popupMenu.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popupMenu.isFocusable = true
        popupMenu.isOutsideTouchable = true
        popupMenu.elevation = 100f

        popupMenu.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Đo kích thước của PopupWindow để tính toán chiều cao cần thiết
        binding1.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = binding1.root.measuredHeight

        // Lấy vị trí của view gốc trên màn hình
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val yPos = location[1] + view.height // Vị trí y của view gốc
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        // Kiểm tra nếu không gian phía dưới không đủ để hiển thị toàn bộ PopupWindow
        if (yPos + popupHeight > screenHeight) {
            // Hiển thị PopupWindow phía trên view gốc nếu không đủ không gian bên dưới
            popupMenu.showAsDropDown(view, -350, -(popupHeight + view.height), Gravity.NO_GRAVITY)
        } else {
            // Hiển thị PopupWindow phía dưới view gốc nếu đủ không gian
            popupMenu.showAsDropDown(view, -350, 30, Gravity.NO_GRAVITY)
        }

        binding1.lnName.safeClick {
            showRenameDialog(item.name ?: "", item, "name")
            popupMenu.dismiss()
        }
        binding1.lnNickName.safeClick {
            showRenameDialog(item.nickName ?: "", item, "nickName")
            popupMenu.dismiss()
        }
        binding1.lnPhone.safeClick {
            showRenameDialog(item.phoneNumber ?: "", item, "phone")

            popupMenu.dismiss()
        }

        popupMenu.showAsDropDown(view, -350, 30, Gravity.NO_GRAVITY)
    }


    private fun showPopupMenuSort(view: View) {
        val layoutInflater = LayoutInflater.from(this)
        val binding1 = LayoutMenuSortBinding.inflate(layoutInflater)
        val popupMenu = PopupWindow(this)
        popupMenu.contentView = binding1.root
        popupMenu.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popupMenu.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popupMenu.isFocusable = true
        popupMenu.isOutsideTouchable = true
        popupMenu.elevation = 100f

        popupMenu.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Đo kích thước của PopupWindow để tính toán chiều cao cần thiết
        binding1.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = binding1.root.measuredHeight

        // Lấy vị trí của view gốc trên màn hình
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val yPos = location[1] + view.height // Vị trí y của view gốc
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        // Kiểm tra nếu không gian phía dưới không đủ để hiển thị toàn bộ PopupWindow
        if (yPos + popupHeight > screenHeight) {
            // Hiển thị PopupWindow phía trên view gốc nếu không đủ không gian bên dưới
            popupMenu.showAsDropDown(view, -350, -(popupHeight + view.height), Gravity.NO_GRAVITY)
        } else {
            // Hiển thị PopupWindow phía dưới view gốc nếu đủ không gian
            popupMenu.showAsDropDown(view, -350, 30, Gravity.NO_GRAVITY)
        }

        binding1.lnAll.safeClick {
            dataHomeAdapter.submitList(dataList)
            popupMenu.dismiss()
        }
        binding1.lnGmobile.safeClick {
            val lit = dataList.filter { it.type == Carrier.GMOBILE }
            dataHomeAdapter.submitList(lit)
            popupMenu.dismiss()
        }

        binding1.lnVt.safeClick {
            val lit = dataList.filter { it.type == Carrier.VIETTEL }
            dataHomeAdapter.submitList(lit)
            popupMenu.dismiss()
        }

        binding1.lnVNM.safeClick {
            val lit = dataList.filter { it.type == Carrier.VIETNAMOBILE }
            dataHomeAdapter.submitList(lit)
            popupMenu.dismiss()
        }

        binding1.lnVina.safeClick {
            val lit = dataList.filter { it.type == Carrier.VINAPHONE }
            dataHomeAdapter.submitList(lit)
            popupMenu.dismiss()
        }

        binding1.lnMB.safeClick {
            val lit = dataList.filter { it.type == Carrier.MOBIFONE }
            dataHomeAdapter.submitList(lit)
            popupMenu.dismiss()
        }

        popupMenu.showAsDropDown(view, -350, 30, Gravity.NO_GRAVITY)
    }

    private fun filterWithSearch(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(dataList)
        } else {
            val lowerCaseQuery = query.lowercase()
            for (item in dataList) {
                if (item.name?.lowercase()?.contains(lowerCaseQuery) == true) {
                    filteredList.add(item)
                }
            }
        }
        dataHomeAdapter.submitList(filteredList.toMutableList())
    }

    private var jobEndLifted: Job? = null
    private var currentEndLifted = 0

    private fun countTimeEndLifted(callback:()->Unit) {
        jobEndLifted?.cancel()
        jobEndLifted = CoroutineScope(Dispatchers.Default + Job()).launch {
            while (isActive) {
                delay(1_000L)
                currentEndLifted--
                withContext(Dispatchers.Main) {
                    binding.tvContent.text = currentEndLifted.toString()
                    if (currentEndLifted == 0) {
                        callback()
                        cleanUp()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        cleanUp()
        super.onDestroy()
    }

    private fun cleanUp() {
        binding.lnTimeOut.gone()
        jobEndLifted?.cancel()
        jobEndLifted = null
        displayName = ""
        phoneNumber = ""
    }
}