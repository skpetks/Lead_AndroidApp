package com.innovu.visitor.ui.dashboard

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.innovu.visitor.ImageAdapter
import com.innovu.visitor.MainActivity
import com.innovu.visitor.R
import com.innovu.visitor.databinding.FragmentCreatevisitorBinding
import com.innovu.visitor.databinding.FragmentDetailvisitorBinding
import com.innovu.visitor.model.Visitor
import com.innovu.visitor.services.ImageUploadService
import com.innovu.visitor.utlis.StorePrefData
import com.innovu.visitor.utlis.UserRole
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailVisitorFragment : Fragment() {

    private var _binding: FragmentDetailvisitorBinding? = null
    private val imageUris = mutableListOf<Uri>()
    private lateinit var photoUri: Uri
    // This property is only valid between onCreateView and
    private lateinit var imageAdapter: ImageAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val cameraLauncherols = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageUris.add(photoUri)
            imageAdapter.submitList(imageUris.toList())
        }
    }


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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailvisitorBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val visitor = Gson().fromJson(StorePrefData.visitorJson, Visitor::class.java)
            binding.tvMeetingtype.text=visitor.meetingType
            binding.tvStatus.text=visitor.recordStatus
        binding.tvFatherName.text=visitor.fatherName
        binding.tvFatherNamePhone.text=visitor.contactNumber
        binding.tvStaffName.text=visitor.userName
        binding.tvVenu.text=visitor.venue
        binding.tvMeetingtime.text=visitor.startTime
        binding.tvStudent.text=visitor.studentName
        binding.tvStudentClass.text=visitor.classname
        binding.tvGrNo.text=visitor.grno
        binding.tvDepart.text=visitor.department
        binding.tvVisitor.text=visitor.visitorName

        val role = UserRole.fromName("SECURITY")
        if(visitor.recordStatusID==2)
        {
            binding.btnNext.text="Arrived"
        }
       else if(visitor.recordStatusID==3 && (visitor.meetingUserID== StorePrefData.UserIId  || StorePrefData.DepartmentID== visitor.departmentID))
        {
            binding.btnNext.text="Confirm Approval"
        }
        else if(visitor.recordStatusID==3 && StorePrefData.RoleId == role?.id)
        {
            binding.btnNext.text="Notifiy Staff"
        }
        else if(visitor.recordStatusID==4)
        {
            binding.btnCaptureImage.visibility= View.VISIBLE
            binding.recyclerView.visibility= View.VISIBLE
            binding.btnNext.text="Check In"
        }
        else if(visitor.recordStatusID==5)
        {
            binding.btnCaptureImage.visibility= View.VISIBLE
            binding.recyclerView.visibility= View.VISIBLE
            binding.btnNext.text="Check Out"
        }
        else if(visitor.recordStatusID==6)
        {
            binding.btnNext.text="Meeting Complete"
        }
        else if(visitor.recordStatusID==7)
        {
            binding.btnNext.text="Rescheduled Meeting"
        }
        else if(visitor.recordStatusID==8)
        {
            binding.btnNext.text="Meeting Cancelled"
        }
        else if(visitor.recordStatusID==9)
        {
            binding.btnNext.text="Not Attended"
        }

        binding.btnNext.setOnClickListener {


             var navigationStatus=false;
            if(visitor.recordStatusID==2)
            {
                navigationStatus=true;
                sendArrivedRequest(visitor.visitorID)
            }
            else if(visitor.recordStatusID==3)
            {
                if(visitor.meetingUserID== StorePrefData.UserIId  || StorePrefData.DepartmentID== visitor.departmentID){
                    ApproveVisitor(visitor.visitorID)
                    navigationStatus=true;
                }else{
                    navigationStatus=false;
                    GetVisitorNotification(visitor.visitorID)
                    Snackbar.make( binding.btnNext, "Sorry User and Department ID miss match!. unable to update the status!.", Snackbar.LENGTH_LONG).show()

                }

            }
            else if(visitor.recordStatusID==4)
            {
                val role = UserRole.fromName("SECURITY")
                if (StorePrefData.RoleId == role?.id)
                {navigationStatus=true;
                    Checkin(visitor.visitorID)
                }else{
                    navigationStatus=false;
                    Snackbar.make( binding.btnNext, "Sorry User and Role . unable to Checkin!.", Snackbar.LENGTH_LONG).show()
                }
            }
            else if(visitor.recordStatusID==5)
            {
                val role = UserRole.fromName("SECURITY")
                if (StorePrefData.RoleId == role?.id)
                {
                    navigationStatus=true;
                    Checkout(visitor.visitorID)
                }else{
                    navigationStatus=false;
                    Snackbar.make( binding.btnNext, "Sorry User and Role . unable to Checkin!.", Snackbar.LENGTH_LONG).show()
                }
            }
            else if(visitor.recordStatusID==6)
            {
                navigationStatus=true;
                binding.btnNext.text="Meeting Complete"
            }

            StorePrefData.visitorID= visitor.visitorID;
            if(imageUris.size>0) {
                val imagePaths: ArrayList<String> =
                    convertUrisToFilePaths(requireContext(), imageUris)
                val intent = Intent(context, ImageUploadService::class.java).apply {
                    putStringArrayListExtra("imagePaths", imagePaths)
                }
                requireContext().startService(intent)
            }

            if( navigationStatus) {
                val navController = requireActivity()
                    .findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigate(R.id.navigation_dashboard)
            }
        }

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

        (requireActivity() as AppCompatActivity).let {
            if (it is MainActivity) {
                it.updateTitleWithColor("Visitor Detail", Color.BLACK)

            }
        }

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
    private fun sendArrivedRequest(visitorId: Int) {
        val viewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]
        viewModel.Arrived(visitorId)
    }
    private fun sendRejectRequest(visitorId: Int) {
        val viewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]
        viewModel.RejectVisitor(visitorId)
    }
    private fun CancelMetting(visitorId: Int) {
        val viewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]
        viewModel.CancelledMeeting(visitorId)
    }
    private fun Checkin(visitorId: Int) {
        val viewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]
        viewModel.CheckInRequestUpdate(visitorId)
    }
    private fun Checkout(visitorId: Int) {
        val viewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]
        viewModel.CheckOutRequestUpdate(visitorId)
    }
    private fun ApproveVisitor(visitorId: Int) {
        val viewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]
        viewModel.ApproveVisitor(visitorId)
    }
    private fun GetVisitorNotification(visitorId: Int) {
        val viewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]
        viewModel.GetVisitorNotification(visitorId)
    }



    fun compressImage(context: Context, imageUri: Uri, quality: Int = 60): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val compressedFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(compressedFile)

            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}