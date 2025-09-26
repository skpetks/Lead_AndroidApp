package com.innovu.visitor.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.innovu.visitor.R
import android.app.TimePickerDialog
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.innovu.visitor.model.RescheduleStatusUpdateRequest
import com.innovu.visitor.model.VisitorRequest
import com.innovu.visitor.ui.dashboard.VisitorViewModel
import com.innovu.visitor.utlis.StorePrefData
import com.innovu.visitor.utlis.Utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ScheduleBottomSheet(private val visitorId: Int) : BottomSheetDialogFragment() {

    private lateinit var etStartTime: AppCompatTextView
    private lateinit var etEndTime: AppCompatTextView
    private lateinit var etVenue: AppCompatEditText
    private lateinit var etOperationDate: AppCompatTextView
    private lateinit var btnSubmit: AppCompatButton
    private lateinit var btnCancel: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etStartTime = view.findViewById(R.id.tv_starttime)
        etEndTime = view.findViewById(R.id.tv_endtime)
        etVenue = view.findViewById(R.id.ed_note)
        etOperationDate = view.findViewById(R.id.tv_operationdate)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnCancel = view.findViewById(R.id.btnCancel)

        etStartTime.setOnClickListener {
            showTimePicker(true,etStartTime,etStartTime) // reopen
        }
        etEndTime.setOnClickListener {

            showTimePicker(false,etEndTime,etStartTime) // reopen

        }
        etOperationDate.setOnClickListener { showDatePicker(etOperationDate) }

        btnCancel.setOnClickListener { dismiss() }

        btnSubmit.setOnClickListener {
            val startTime = etStartTime.text.toString()
            val endTime = etEndTime.text.toString()
            val venue = etVenue.text.toString()
            val operationDate = etOperationDate.text.toString()

            // TODO: Handle your submission logic here
            val request =  RescheduleStatusUpdateRequest (
                visitorID=visitorId,
                startTime= Utils.convertToUtcDateTime(operationDate,startTime),         // use ISO format string e.g. "2025-06-27T12:30:00"
                endTime=Utils.convertToUtcDateTime(operationDate,endTime),
                venue=venue,
                operationDate=Utils.convertDateToIsoUtc(operationDate),
                UserID= StorePrefData.UserIId
            )


            val errors = request.validate()

            if (errors.isEmpty()) {
                Toast.makeText(context, "Please wait...", Toast.LENGTH_SHORT).show()
                sendRejectRequest(request)
                dismiss()

                // Send to API
            } else {
                // Optional: show all errors in Toast or Snackbar
                Snackbar.make(btnSubmit, errors.joinToString("\n"), Snackbar.LENGTH_LONG).show()
            }

        }
    }

    private fun sendRejectRequest(request: RescheduleStatusUpdateRequest) {
        val viewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]
        viewModel.reschedule(request)
    }
    fun RescheduleStatusUpdateRequest.validate(): List<String> {
        val errors = mutableListOf<String>()
        if (startTime.isNullOrBlank()) errors.add("Start Time is required.")
        if (endTime.isNullOrBlank()) errors.add("End Time is required.")
        if (operationDate.isNullOrBlank()) errors.add("Operation Date is required.")
        if (!startTime.isNullOrBlank() && !endTime.isNullOrBlank()) {
            try {
                val format = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val start = format.parse(startTime)
                val end = format.parse(endTime)
                if (start != null && end != null && end.before(start)) {
                    errors.add("End Time must be after Start Time.")
                }
            } catch (e: Exception) {
                errors.add("Date format invalid.")
            }
        }

        return errors
    }


    private fun showDateTimePicker(target: AppCompatTextView) {
        val now = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val datetime = "$year-${month + 1}-$day $hour:$minute"
                target.setText(datetime)
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }




    private fun showDatePicker(target: AppCompatTextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), // Use requireContext() inside Fragment
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "${selectedYear}-${selectedMonth + 1}-${selectedDay}"
                target.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }




    private fun showTimePicker(status: Boolean,target: AppCompatTextView,tvStarttime :AppCompatTextView) {
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
                    target.text = formattedTime
                } else {
                    // End time selection — validate against selected start time
                    val startTimeText = tvStarttime.text.toString()

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
                                showTimePicker(status,target,tvStarttime) // reopen
                                return@TimePickerDialog
                            }
                        }
                    }

                    val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    target.text = formattedTime
                }
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }
}
