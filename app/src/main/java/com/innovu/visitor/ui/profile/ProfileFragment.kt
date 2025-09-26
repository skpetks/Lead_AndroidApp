package com.innovu.visitor.ui.dashboard

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
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
import com.innovu.visitor.databinding.FragmentProfileBinding
import com.innovu.visitor.ui.LoginActivity
import com.innovu.visitor.utlis.StorePrefData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var visitorViewModel: VisitorViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visitorViewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]

        // Observe profile data
        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.textViewName.text = "${it.firstName} ${it.lastName}"
                binding.textViewEmail.text = it.email ?: ""
                binding.textViewGender.text = it.gender ?: ""
                binding.textViewPhone.text = it.phoneNo ?: ""
                binding.tvUsername.text = it.userName ?: ""



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

        binding.btnEdit.setOnClickListener{
            val navController = requireActivity()
                .findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_editprofile)
        }
        // Logout button

        binding.customButton.setOnClickListener {
            showPopup(it)
        }
        (requireActivity() as AppCompatActivity).let {
            if (it is MainActivity) {
                it.updateTitleWithColor("Profile", Color.BLACK)
            }
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_left)

        visitorViewModel.result.observe(viewLifecycleOwner) { success ->
            NavigateLogin()
        }

    }

    private fun performLogout() {


        lifecycleScope.launch(Dispatchers.IO) {
            visitorViewModel.logoutUser()
        }

        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
        // Navigate to login screen
//        val intent = Intent(requireContext(), LoginActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
//        requireActivity().finish()
    }



    private fun NavigateLogin(){
                val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
    private fun showPopup(anchor: View) {
        val inflater = LayoutInflater.from(anchor.context)
        val popupView = inflater.inflate(R.layout.custom_logout, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true // focusable
        )

        // Optional: make background transparent for rounded corners to show
    //    popupWindow.setBackgroundDrawable(null)
        popupWindow.elevation = 10f
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set click actions
        val btnCancel = popupView.findViewById<Button>(R.id.btnCancel)
        val btnLogout = popupView.findViewById<Button>(R.id.btnYes)

        btnCancel.setOnClickListener {
            popupWindow.dismiss()
        }

        btnLogout.setOnClickListener {
            popupWindow.dismiss()
            Toast.makeText(anchor.context, "Logged out", Toast.LENGTH_SHORT).show()
            // Handle your logout logic here
            performLogout()
        }

        // Show popup
        popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
