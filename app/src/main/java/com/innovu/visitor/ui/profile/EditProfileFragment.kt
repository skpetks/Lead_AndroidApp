package com.innovu.visitor.ui.dashboard

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.innovu.visitor.BuildConfig
import com.innovu.visitor.MainActivity
import com.innovu.visitor.R
import com.innovu.visitor.databinding.FragmentEditprofileBinding
import com.innovu.visitor.databinding.FragmentProfileBinding
import com.innovu.visitor.model.EditUserProfileRequest
import com.innovu.visitor.ui.LoginActivity
import com.innovu.visitor.utlis.StorePrefData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditprofileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var visitorViewModel: VisitorViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditprofileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visitorViewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]

        // Observe profile data
        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.edFirstname.text = Editable.Factory.getInstance().newEditable(it.firstName)
                binding.edLastname.text = Editable.Factory.getInstance().newEditable(it.lastName)
                binding.edEmail.text = Editable.Factory.getInstance().newEditable(it.email ?: "")
                binding.edPhone.text = Editable.Factory.getInstance().newEditable(it.phoneNo ?: "")
                binding.edUsername.text = Editable.Factory.getInstance().newEditable(it.userName ?: "")



                val newUrl = BuildConfig.SERVICE_END_POINT.replace("/api/", "/")
                val fullUrl = newUrl + (it.photo ?: "")
                if (!it.photo.isNullOrEmpty()) {


                Glide.with(requireContext())
                    .load(fullUrl)
                    .circleCrop()
                    .into(binding.imageViewProfile)
                }
            }
        }

        // Observe error
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        // ✅ Only fetch if not already loaded
        if (viewModel.userProfile.value == null) {
            viewModel.fetchUserProfile(userId = StorePrefData.UserIId)
        }

        visitorViewModel.result.observe(viewLifecycleOwner) { success ->
            popupMessage()
        }

        binding.btnEdit.setOnClickListener{


            val request = EditUserProfileRequest(
                userID = StorePrefData.UserIId,
                firstName = binding.edFirstname.text.toString(),
                lastName = binding.edLastname.text.toString(),
                userName = "string",
                userCode = "string",
                password = "string",
                userType = "string",
                mobileNumber = "string",
                email = binding.edEmail.text.toString(),
                city = "string",
                pincode = "string",
                state = "string",
                address = "string",
                gender = "string",
                photo = "string",
                dateOfBirth = "2025-07-30T17:54:00.740Z",
                joiningDate = "2025-07-30T17:54:00.740Z",
                shiftStartTime = "string",
                shiftEndTime = "string",
                status = "string",
                employeeId = "string",
                deviceType = "string",
                deviceToken = "string",
                googleToken = "string",
                loginType = "string",
                branchID = 0,
                organizationID = 0,
                departmentID = 0,
                createdAt = "2025-07-30T17:54:00.740Z",
                updatedAt = "2025-07-30T17:54:00.740Z",
                recordStatusID = 0,
                phoneNo = binding.edPhone.text.toString()
            )
            lifecycleScope.launch(Dispatchers.IO) {
                visitorViewModel.EditUserProfile(request)
            }

        }

        binding.customButton.setOnClickListener {
            val navController = requireActivity()
                .findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_profile)
        }
        (requireActivity() as AppCompatActivity).let {
            if (it is MainActivity) {
                it.updateTitleWithColor("Edit Profile", Color.BLACK)
            }
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_left)



    }

    private fun popupMessage() {




        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.custom_popup, null)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val btnOk = view.findViewById<Button>(R.id.btnOk)

        tvTitle.text = ""
        tvMessage.text = "You Profile is updated Successfully"

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(false)
            .create()

        btnOk.setOnClickListener {
            val navController = requireActivity()
                .findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_profile)
            dialog.dismiss()
        }

        dialog.show()

    }







    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
