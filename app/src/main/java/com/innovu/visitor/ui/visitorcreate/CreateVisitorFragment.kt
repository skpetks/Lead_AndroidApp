package com.innovu.visitor.ui.dashboard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.innovu.visitor.ImageAdapter
import com.innovu.visitor.MainActivity
import com.innovu.visitor.R
import com.innovu.visitor.data.api.RetrofitClient
import com.innovu.visitor.databinding.FragmentCreatevisitorBinding
import com.innovu.visitor.model.Department
import com.innovu.visitor.model.MeetingType
import com.innovu.visitor.model.SearchRequest
import com.innovu.visitor.model.User
import com.innovu.visitor.model.VisitorRequest
import com.innovu.visitor.model.VisitorType
import com.innovu.visitor.services.ImageUploadService
import com.innovu.visitor.ui.visitorcreate.DepartmentAdapter
import com.innovu.visitor.ui.visitorcreate.Student
import com.innovu.visitor.ui.visitorcreate.StudentAdapter
import com.innovu.visitor.utlis.StorePrefData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.collections.map
import kotlin.getValue

class CreateVisitorFragment : Fragment() {

    private var _binding: FragmentCreatevisitorBinding? = null
    private val imageUris = mutableListOf<Uri>()
    private lateinit var photoUri: Uri
    // This property is only valid between onCreateView and
    private lateinit var imageAdapter: ImageAdapter

    private val visitorViewModel: VisitorViewModel by viewModels()
    private  var meetingUserID=0
    private  var meetingTypeID=0
    private  var visitorTypeID=0
    private  var DepartmentID=0
    val departmentList: MutableList<Department> = mutableListOf()




    // onDestroyView.
    private val binding get() = _binding!!



    val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            compressImage(requireContext(), photoUri)?.let { compressedFile ->
                val compressedUri = Uri.fromFile(compressedFile)
                imageUris.add(compressedUri)

                imageAdapter.submitList(imageUris.toList())
            }
        }
    }
    fun uriToFile(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }
        return tempFile
    }
    fun convertUrisToFilePaths(context: Context, uris: List<Uri>): ArrayList<String> {
        val pathList = arrayListOf<String>()

        uris.forEach { uri ->
            val file = uriToFile(context, uri)
            file?.absolutePath?.let { pathList.add(it) }
        }

        return pathList
    }
    val allStudents = mutableListOf<Student>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatevisitorBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        imageAdapter = ImageAdapter { uriToDelete ->
            imageUris.remove(uriToDelete)
            imageAdapter.submitList(imageUris.toList()) // Use toList() to trigger DiffUtil or recreate
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }

        binding.btnCaptureImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            } else {
                requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
        binding.tvOperationdate.setOnClickListener {
            showDatePicker()
        }
        binding.tvMeetingType.setOnClickListener {
            visitorViewModel.meetingType.value?.let { list ->
                showSearchableMeetingDialog(list)
            }
        }


        binding.tvVisitorType.setOnClickListener {
            visitorViewModel.VisitorType.value?.let { list ->
                showSearchableVisitorDialog(list)
            }
        }


        binding.tvMeetingUser.setOnClickListener {
            visitorViewModel.userlist.value?.let { list ->
                val filteredList = if (DepartmentID == 0) {
                    list // Show all users
                } else {
                    list.filter { it.departmentID == DepartmentID }
                }
                showUserList(filteredList)

            }
        }

        binding.edStudentid.setOnClickListener{
            showStudentSearchPopup()

        }
        binding.tvDepartment.setOnClickListener{
            context?.let { it1 -> showDepartmentPopup(it1,it, departmentList) }
        }

        binding.tvStarttime.setOnClickListener {
            showTimePicker(true)
        }
        binding.tvEndtime.setOnClickListener {
            showTimePicker(false)
        }
        binding.btnCreate.setOnClickListener {

            CreateVisitor();
        }
        binding.imgVerify.setOnClickListener {

                if ( binding.edContact.text.toString().length==10){
                    visitorViewModel.generateOtp(binding.edContact.text.toString())
                }


            showOtpPopupWindow(it) { otp ->
                Toast.makeText(requireContext(), "OTP Entered: $otp", Toast.LENGTH_SHORT).show()
                // Perform OTP verification
            }
        }
        visitorViewModel.DepartmentList.observe(viewLifecycleOwner) { res ->
            res?.let {
                departmentList.clear() // Optional: clear previous data if needed
                departmentList.addAll(it)
                // Example: notify adapter if using RecyclerView

            }
        }


        visitorViewModel.result.observe(viewLifecycleOwner) { res ->
            if(res){
                Toast.makeText(requireContext(),"new Meeting Created", Toast.LENGTH_SHORT).show();
                val navController = requireActivity()
                    .findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigate(R.id.navigation_home)
            }
        }

        visitorViewModel.visitorCreateResponse.observe(viewLifecycleOwner) { res ->
          StorePrefData.visitorID= res.visitorID;
            if(imageUris.size>0) {
                val imagePaths: ArrayList<String> =
                    convertUrisToFilePaths(requireContext(), imageUris)
                val intent = Intent(context, ImageUploadService::class.java).apply {
                    putStringArrayListExtra("imagePaths", imagePaths)
                }
                requireContext().startService(intent)
            }
            val navController = requireActivity()
                .findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_dashboard)
        }
        visitorViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()

        }

        visitorViewModel.getVisitorTypes()
        visitorViewModel.getMeetingTypes()
        visitorViewModel.getUserList()
        visitorViewModel.getDepartments()

        (requireActivity() as AppCompatActivity).let {
            if (it is MainActivity) {
                it.updateTitleWithColor("Create New Meeting", Color.BLACK)
            }
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_left)
    }

    private fun showDepartmentPopup(context: Context, anchor: View, departments: List<Department>) {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_departments, null)
        val popupWindow = PopupWindow(popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        popupWindow.elevation = 10f

        val recyclerView = popupView.findViewById<RecyclerView>(R.id.departmentRecyclerView)
        val searchEditText = popupView.findViewById<EditText>(R.id.searchEditText)

        val adapter = DepartmentAdapter(departments.toMutableList()) { selectedDept ->

            DepartmentID=selectedDept.departmentID;
            binding.tvDepartment.text=selectedDept.departmentName


            Toast.makeText(context, "Selected: ${selectedDept.departmentName}", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener {
            val query = it.toString().trim()
            adapter.filter(query)
        }

        popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0)
    }



    private fun showStudentSearchPopup() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search_student, null)
        val etSearch = dialogView.findViewById<AppCompatEditText>(R.id.etSearch)
        val rvStudents = dialogView.findViewById<RecyclerView>(R.id.rvStudents)

        val tvNoRecord = dialogView.findViewById<TextView>(R.id.tvNoRecord)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val adapter = StudentAdapter(allStudents) { selected ->
            binding.edStudentid.setText("${selected.grNo}")
            binding.edContact.setText("${selected.contactNumber}")
            binding.edVisitor.setText("${selected.fatherName}")
            dialog.dismiss()
        }

        rvStudents.layoutManager = LinearLayoutManager(requireContext())
        rvStudents.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                val filtered = allStudents.filter {
                    it!!.firstName.lowercase().contains(query) || it.contactNumber.contains(query)
                }
                adapter.filterList(filtered)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etSearch.debounceTextChanged(viewLifecycleOwner.lifecycleScope) { keyword ->
            if (keyword.length >= 2) {

                    try {
                        val searchrequest = SearchRequest(
                            OrganizationID = StorePrefData.OrgId,
                            BranchID = StorePrefData.BranchId,
                            keyword=keyword
                        )
                        viewLifecycleOwner.lifecycleScope.launch {
                            val response = RetrofitClient.instance.searchStudents(searchrequest)
                            if (response.isSuccessful) {
                                withContext(Dispatchers.Main) {
                                    if(response!!.body()!!.data!=null) {
                                        adapter.filterList(response!!.body()!!.data)
                                        rvStudents.visibility=View.VISIBLE
                                        tvNoRecord.visibility=View.GONE
                                    }else{
                                        tvNoRecord.visibility=View.VISIBLE
                                        rvStudents.visibility=View.GONE
                                    }
                                }
                            }
                        }

                    } catch (e: Exception) {

                    }

            }
        }

        dialog.show()

        etSearch.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

    }




    fun VisitorRequest.validate(): List<String> {
        val errors = mutableListOf<String>()


        if (visitorName.isBlank()) errors.add("Visitor Name is required.")

        if (visitorTypeID <= 0) errors.add("Visitor Type ID is required.")

//        if (startTime.isNullOrBlank()) errors.add("Start Time is required.")
//        if (endTime.isNullOrBlank()) errors.add("End Time is required.")
        if (meetingTypeID <= 0) errors.add("Vistor Purpose is required.")
        if (parentContact.isBlank()) errors.add("Parent contact is required.")
//        if (operationDate.isBlank()) errors.add("Operation Date is required.")
        if (recordStatusID <= 0) errors.add("Record Status ID is required.")
        if (meetingUserID <= 0) errors.add("Meeting User ID is required.")
        // Optional: Logical validations (e.g., endTime after startTime)
//        if (!startTime.isNullOrBlank() && !endTime.isNullOrBlank()) {
//            try {
//                val format = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
//                val start = format.parse(startTime)
//                val end = format.parse(endTime)
//                if (start != null && end != null && end.before(start)) {
//                    errors.add("End Time must be after Start Time.")
//                }
//            } catch (e: Exception) {
//                errors.add("Date format invalid.")
//            }
//        }

        return errors
    }

    private fun studentsearch(){

    }
    private fun showTimePicker(status: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                    set(Calendar.SECOND, 0)
                }

                if (status) {
                    // Start time selection — no validation needed
                    val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    binding.tvStarttime.text = formattedTime
                } else {
                    // End time selection — validate against selected start time
                    val startTimeText = binding.tvStarttime.text.toString()

                    if (startTimeText.isNotEmpty()) {
                        val parts = startTimeText.split(":")
                        if (parts.size == 2) {
                            val startHour = parts[0].toInt()
                            val startMinute = parts[1].toInt()

                            val startTime = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, startHour)
                                set(Calendar.MINUTE, startMinute)
                                set(Calendar.SECOND, 0)
                            }

                            if (selectedTime.timeInMillis <= startTime.timeInMillis) {
                                Toast.makeText(
                                    requireContext(),
                                    "End time must be after start time",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showTimePicker(status) // reopen
                                return@TimePickerDialog
                            }
                        }
                    }

                    val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    binding.tvEndtime.text = formattedTime
                }
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }


    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera() // Permission granted
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timestamp}_", ".jpg", storageDir)
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        cameraLauncher.launch(photoUri)
    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.tvOperationdate.text = formattedDate
            },
            year, month, day
        )

        // Set minimum date to today
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun CreateVisitor(){

        val visitorRequest = VisitorRequest(
            visitorID = 0,
            visitorCode = null,
            visitorName = binding.edVisitor.text.toString(),
            studentID = 0,
            visitorTypeID = visitorTypeID,
            startTime =getCurrentDateIsoUtc(),// convertToUtcDateTime(binding.tvOperationdate.text.toString(),binding.tvStarttime.text.toString()),
            endTime =getCurrentDateIsoUtc(),//convertToUtcDateTime(binding.tvOperationdate.text.toString(),binding.tvEndtime.text.toString()),
            venue = binding.edNote.text.toString(),
            checkINtime = null,
            checkOutTime = null,
            meetingTypeID = meetingTypeID,
            parentContact = binding.edContact.text.toString(),
            checkinUserID = null,
            approveUserID = null,
            checkoutUserID = null,
            operationDate = getCurrentDateIsoUtc(),//convertDateToIsoUtc(binding.tvOperationdate.text.toString()),
            smsStatus = "Pending",
            smsResponse = "",
            recordStatusID = 3,
            meetingUserID = meetingUserID,
            note = "Bring ID",
            branchID = StorePrefData.BranchId,
            organizationID = StorePrefData.OrgId,
            inGateID = null,
            outGateID = null,
            createdAt = getDeviceCurrentDateTime(),
            updatedAt = getDeviceCurrentDateTime(),
            cancelUserID = null,
            cancelDatetime = null,
            qrPath = null,
            grNo = binding.edStudentid.text.toString(),
        )

        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(visitorRequest)
        Log.d("VisitorRequest", json)


        val errors = visitorRequest.validate()

        if (errors.isEmpty()) {
            Toast.makeText(context, "Visitor Meeting! Proceeding...", Toast.LENGTH_SHORT).show()

            visitorViewModel.CreateVisitorMain(visitorRequest)
            // Send to API
        } else {
            // Optional: show all errors in Toast or Snackbar
            Snackbar.make(binding.btnCreate, errors.joinToString("\n"), Snackbar.LENGTH_LONG).show()
        }
    }


    fun getCurrentDateIsoUtc(): String {
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
//        outputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val currentDate = Date()
        return outputFormat.format(currentDate)
    }
    fun convertDateToIsoUtc(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // interpret as UTC

        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("UTC") // output in UTC

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }
    private fun showUserList(meetingList: List<User>) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_searchable_list, null)

        val searchView = dialogView.findViewById<SearchView>(R.id.search_view)
        val listView = dialogView.findViewById<ListView>(R.id.list_view)

        val meetingNames = meetingList.map { it.userName }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, meetingNames)
        listView.adapter = adapter

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedName = adapter.getItem(position)
            binding.tvMeetingUser.text = selectedName
            val selectedObject = meetingList.firstOrNull { it.userName == selectedName }
             meetingUserID=selectedObject!!.userID

            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun showOtpPopupWindow(anchorView: View, onOtpConfirmed: (String) -> Unit) {
        val context = requireContext()
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.dialog_otp_verify, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        ).apply {
            isOutsideTouchable = true
            isFocusable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            animationStyle = android.R.style.Animation_Dialog
        }

        val otpBoxes = listOf<EditText>(
            popupView.findViewById(R.id.otp_1),
            popupView.findViewById(R.id.otp_2),
            popupView.findViewById(R.id.otp_3),
            popupView.findViewById(R.id.otp_4),
            popupView.findViewById(R.id.otp_5),
            popupView.findViewById(R.id.otp_6)
        )

        val btnConfirm = popupView.findViewById<AppCompatButton>(R.id.btn_confirm)

        val img_status= popupView.findViewById<AppCompatImageView>(R.id.img_status)


        // OTP focus movement
        otpBoxes.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < otpBoxes.lastIndex) {
                        otpBoxes[index + 1].requestFocus()
                    } else if (s?.isEmpty() == true && index > 0) {
                        otpBoxes[index - 1].requestFocus()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL &&
                    event.action == KeyEvent.ACTION_DOWN &&
                    editText.text.isEmpty() &&
                    index > 0
                ) {
                    otpBoxes[index - 1].requestFocus()
                    otpBoxes[index - 1].setSelection(otpBoxes[index - 1].text?.length ?: 0)
                }
                false
            }
        }

        // Confirm button
        btnConfirm.setOnClickListener {
            val otp = otpBoxes.joinToString("") { it.text.toString().trim() }

            if (otp.length == 6) {


                    if (otp == "123456") {

                        img_status.setImageResource(R.drawable.tick_correct)

                        Toast.makeText(context, "OTP Verified!", Toast.LENGTH_SHORT).show()
                        onOtpConfirmed(otp)
                       //
                        Handler(Looper.getMainLooper()).postDelayed({
                            popupWindow.dismiss()
                        }, 2000) // 2000 ms = 2 seconds
                    } else {
                          img_status.setImageResource(R.drawable.tick_wrong)
                        Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }




              //  popupWindow.dismiss()
            } else {
                Toast.makeText(context, "Enter 6-digit OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // Show the popup
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    }



    private fun showSearchableMeetingDialog(meetingList: List<MeetingType>) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_searchable_list, null)

        val searchView = dialogView.findViewById<SearchView>(R.id.search_view)
        val listView = dialogView.findViewById<ListView>(R.id.list_view)

        val meetingNames = meetingList.map { it.meetingName }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, meetingNames)
        listView.adapter = adapter

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedName = adapter.getItem(position)
            binding.tvMeetingType.text = selectedName

            val selectedObject = meetingList.firstOrNull { it.meetingName == selectedName }

           meetingTypeID=selectedObject!!.meetingTypeID

            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    private fun showSearchableVisitorDialog(meetingList: List<VisitorType>) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_searchable_list, null)

        val searchView = dialogView.findViewById<SearchView>(R.id.search_view)
        val listView = dialogView.findViewById<ListView>(R.id.list_view)

        val meetingNames = meetingList.map { it.visitorTypeDetail }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, meetingNames)
        listView.adapter = adapter

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedName = adapter.getItem(position)
            binding.tvVisitorType.text = selectedName

            val selectedObject = meetingList.firstOrNull { it.visitorTypeDetail == selectedName }
           visitorTypeID=selectedObject!!.visitorTypeID
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun compressImage(context: Context, imageUri: Uri, quality: Int = 60): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            var finalBitmap = originalBitmap

            if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
                val exif = context.contentResolver.openInputStream(imageUri)?.use {
                    ExifInterface(it)
                }

                val orientation = exif?.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                ) ?: ExifInterface.ORIENTATION_NORMAL

                finalBitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(originalBitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(originalBitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(originalBitmap, 270f)
                    else -> originalBitmap
                }
            }

            val compressedFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(compressedFile)

            finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(angle) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getDeviceCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC") // force UTC
        return sdf.format(Date())
    }


    fun EditText.debounceTextChanged(
        lifecycleScope: CoroutineScope,
        delayMillis: Long = 300L,
        onTextChanged: (String) -> Unit
    ) {
        var job: Job? = null
        this.addTextChangedListener {
            job?.cancel()
            job = lifecycleScope.launch {
                delay(delayMillis)
                onTextChanged(it.toString())
            }
        }
    }

}