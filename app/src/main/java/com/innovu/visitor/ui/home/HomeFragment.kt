package com.innovu.visitor.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.innovu.visitor.R
import com.innovu.visitor.databinding.FragmentHomeBinding
import com.innovu.visitor.ui.dashboard.VisitorAdapter
import com.innovu.visitor.ui.dashboard.VisitorViewModel
import com.innovu.visitor.utlis.StorePrefData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Calendar
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.innovu.visitor.MainActivity
import com.innovu.visitor.model.MeetingStatusData
import com.innovu.visitor.model.VisitorMeetingData
import com.innovu.visitor.ui.dashboard.HorizontalAdapter
import java.text.SimpleDateFormat
import java.util.Date

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val scope = MainScope()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var visitorViewModel: VisitorViewModel
    private lateinit var visitorAdapter: VisitorAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup RecyclerView
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_grid)

        getTitle()

        visitorAdapter =  VisitorAdapter(emptyList(),requireActivity())
        visitorViewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]
        binding.recyclerVisitors.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = visitorAdapter
        }
        val dividerItemDecoration = DividerItemDecoration(binding.recyclerVisitors.context, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.recycler_divider)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        binding.recyclerVisitors.addItemDecoration(dividerItemDecoration)
        binding.swipeRefreshLayout.setOnRefreshListener {
            GetVisitorDetail()
        }


        // Observe LiveData
        visitorViewModel.visitorResponse.observe(viewLifecycleOwner) { visitors ->
            // TODO: Update your UI (RecyclerView, etc.) with visitor list
            binding.swipeRefreshLayout.isRefreshing = false
            if(visitors.size>0){
                visitorAdapter.updateData(visitors.reversed())
                binding.recyclerVisitors.visibility=View.VISIBLE
                binding.noDataTextView.visibility=View.GONE
            }else{
                binding.recyclerVisitors.visibility=View.GONE
                binding.noDataTextView.visibility=View.VISIBLE

            }
            binding.progress.progressBar.isVisible=false
        }

        visitorViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            // TODO: Show error message (toast, textView, etc.)
            binding.progress.progressBar.isVisible=false
        }


        // Search implementation
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                visitorAdapter.filter(newText ?: "")
                return true
            }
        })
        binding.searchView.setIconifiedByDefault(false)
        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

//// Set hint text color
        searchEditText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.grey))

// Optional: Set text color too
        searchEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

//// Optional: Force the hint again
        searchEditText.hint = "Search visitors..."
        visitorViewModel.MeetingListcount.observe(viewLifecycleOwner) { dataList ->
            generatePieChart(dataList)
        }

        if (visitorViewModel.visitorResponse.value == null) {
            GetVisitorDetail()
        }
        visitorViewModel!!.GetmeetingStatusCount()
        visitorViewModel.refreshTrigger.observe(viewLifecycleOwner) {
            visitorViewModel.fetchVisitors(StorePrefData.UserIId, StorePrefData.OrgId, StorePrefData.BranchId)

            // Refresh your data list
        }

        visitorViewModel.MeetingListcount.observe(viewLifecycleOwner) { dataList ->
            // TODO: Update your UI (RecyclerView, etc.) with visitor list
            binding.progress.progressBar.isVisible=false
            binding.rvFilter.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvFilter.adapter = HorizontalAdapter(dataList, requireActivity()) { clickedItem ->
                fetchMeetingsByStatus(clickedItem.meetingStatusID)
            }
        }
        // Call the function here
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            @SuppressLint("ResourceType")
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_bell, menu)
                val menuItem = menu.findItem(R.id.action_info)
                val actionView = menuItem.actionView

                val redDot = actionView?.findViewById<View>(R.id.red_dot)
                val bellIcon = actionView?.findViewById<ImageView>(R.id.bell_icon)




                // Show or hide red dot
                redDot?.visibility = View.VISIBLE // or View.GONE

                // Optional: click listener on bell
                actionView?.setOnClickListener {
                    onMenuItemSelected(menuItem) // simulate click
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_info -> {
                        findNavController().navigate(
                            R.id.navigation_notifications,

                            )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    private fun GetVisitorDetail(){
        scope.launch {
            binding.progress.progressBar.isVisible=true
            visitorViewModel!!.fetchVisitors(StorePrefData.UserIId, StorePrefData.OrgId, StorePrefData.BranchId)
        }
    }
    private fun fetchMeetingsByStatus(statusId: Int) {
        // Call your API using Retrofit here
        binding.progress.progressBar.isVisible=true
        scope.launch {
            visitorViewModel!!.GetVisitorFilter(statusId)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun generatePieChart(dataList: List<MeetingStatusData>) {
        // Filter out entries with meetingCount <= 0
        val entries = dataList
            .filter { it.meetingCount > 0 }
            .map { data ->
                PieEntry(data.meetingCount.toFloat(), data.meetingStatus)
            }

        // Exit early if no valid entries
        if (entries.isEmpty()) {
            _binding?.pieChart?.clear()
            _binding?.pieChart?.invalidate()
            return
        }

        val dataSet = PieDataSet(entries, "Meeting Status")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        val data = PieData(dataSet)

        _binding?.pieChart?.apply {
            this.data = data
            description.isEnabled = false
            isRotationEnabled = true
            setEntryLabelColor(Color.BLACK)
            setUsePercentValues(true)
            setDrawHoleEnabled(true)
            setHoleColor(Color.TRANSPARENT)
            animateY(1000, Easing.EaseInOutQuad)
            invalidate()
        }
    }
    private fun getTitle() {
        val sdf: SimpleDateFormat = SimpleDateFormat("HH")
        val hr = sdf.format(Date()).toInt()
        var Message = "";
        if (hr < 12) {
            Message = "Good morning, "
        } else if (hr >= 12 && hr <= 16) {
            Message = "Good afternoon, "
        } else if (hr > 16 && hr <= 18) {
            Message = "Good evening, "
        } else if (hr > 18 && hr < 24) {
            Message = "Good Night, "
        }


        (requireActivity() as AppCompatActivity).let {
            if (it is MainActivity) {
                it.supportActionBar?.apply {
                    setDisplayHomeAsUpEnabled(true)   // Needed to show the icon
                    setDisplayShowHomeEnabled(true)   // Sometimes also needed
                }
                val icon = context?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.ic_grid) }
                val background =
                    context?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.bg_circle) } // custom background shape

                val layers = arrayOf(background, icon)
                val layerDrawable = LayerDrawable(layers)
// Optionally adjust insets if needed
                layerDrawable.setLayerInset(1, 10, 10, 10, 10) // Padding for the icon layer
                it.supportActionBar?.setHomeAsUpIndicator(layerDrawable)
                 it.UpdateHomePage(Message, StorePrefData.UserName.toString())


                it.getTitleName()
            }
        }
    }

}



class DatePickerBottomSheet(
    private val onDateSelected: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var datePicker: DatePicker
    private lateinit var btnConfirm: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_datepicker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        datePicker = view.findViewById(R.id.datePicker)
        btnConfirm = view.findViewById(R.id.btnConfirm)

        btnConfirm.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month + 1 // months are 0-based
            val year = datePicker.year

            val formattedDate = String.format("%02d/%02d/%04d", day, month, year)
            onDateSelected(formattedDate)
            dismiss()
        }
    }
}