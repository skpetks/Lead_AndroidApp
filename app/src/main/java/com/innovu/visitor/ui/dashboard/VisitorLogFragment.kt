package com.innovu.visitor.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.innovu.visitor.MainActivity
import com.innovu.visitor.R
import com.innovu.visitor.databinding.FragmentDashboardBinding
import com.innovu.visitor.model.Lead
import com.innovu.visitor.utlis.StorePrefData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class VisitorLogFragment : Fragment(), OnLeadActionListener {

    private var _binding: FragmentDashboardBinding? = null
    private val scope = MainScope()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var visitorViewModel: VisitorViewModel
    private lateinit var visitorAdapter: LeadAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }



    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup RecyclerView
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_grid)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        (requireActivity() as AppCompatActivity).let {
            if (it is MainActivity) {
                it.updateTitleWithColor("Lead Log", Color.BLACK)
            }
        }


        binding.progress.progressBar.isVisible=true
        visitorViewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]

        visitorAdapter = LeadAdapter(emptyList(),this,requireActivity())
        binding.recyclerVisitors.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = visitorAdapter
        }

        // Observe LiveData
        visitorViewModel.leadResponse.observe(viewLifecycleOwner) { visitors ->

            Log.d("visitors", "visitors() called with ${visitors.size} visitors")
            visitorAdapter.updateData(visitors.reversed())
            binding.progress.progressBar.isVisible=false
        }

        visitorViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            binding.progress.progressBar.isVisible=false
//            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()

        }
        visitorViewModel.result.observe(viewLifecycleOwner) { success ->

        }
        visitorViewModel.refreshTrigger.observe(viewLifecycleOwner) {
            visitorViewModel.fetchVisitors(StorePrefData.UserIId, StorePrefData.OrgId, StorePrefData.BranchId)

            // Refresh your data list
        }
        // Search implementation



        visitorViewModel.MeetingListcount.observe(viewLifecycleOwner) { dataList ->
            // TODO: Update your UI (RecyclerView, etc.) with visitor list
            binding.progress.progressBar.isVisible=false
            binding.rvFilter.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvFilter.adapter = HorizontalAdapter(dataList, requireActivity()) { clickedItem ->
                fetchMeetingsByStatus(clickedItem.meetingStatusID)
            }
        }
        // Call the function here
        if (visitorViewModel.visitorResponse.value == null) {
            GetVisitorDetail()
        }

// Call API
        GetVisitorDetail()



        // Search implementation
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                visitorAdapter.filter(newText ?: "")
                return true
            }
        })

        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

//// Set hint text color
        searchEditText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.grey))

// Optional: Set text color too
        searchEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

//// Optional: Force the hint again
        searchEditText.hint = "Search leads..."

        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_left)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_grid)

                // Handle back arrow click
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchMeetingsByStatus(statusId: Int) {
        // Call your API using Retrofit here
        binding.progress.progressBar.isVisible=true
        scope.launch {
            visitorViewModel!!.GetVisitorFilter(statusId)
        }
    }
    private fun GetVisitorDetail(){
        scope.launch {
            visitorViewModel!!.getLeadsByUser(StorePrefData.UserIId)
            visitorViewModel!!.GetmeetingStatusCount()
        }

    }
    override fun onCallClicked(lead: Lead) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${lead.phone}")
        }
        startActivity(intent)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openPopup() {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.dialog_success_created, null)

        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        val focusable = true // Allows taps outside to dismiss the popup
        val popupWindow = PopupWindow(popupView, width, height, focusable)
//        val btnNow = popupView.findViewById<ButtonBold>(R.id.btn_ok)
//        btnNow.setOnClickListener {
//            popupWindow.dismiss()
//        }
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

}