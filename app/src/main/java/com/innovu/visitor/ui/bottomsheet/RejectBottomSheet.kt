package com.innovu.visitor.ui.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.innovu.visitor.R
import com.innovu.visitor.ui.dashboard.VisitorViewModel

class RejectBottomSheet(private val visitorId: Int,private val status: Int) : BottomSheetDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.bottom_sheet_reject, container, false)
        val rejectButton = view.findViewById<AppCompatButton>(R.id.btnReject)
        val btn_arrived = view.findViewById<AppCompatButton>(R.id.btn_arrived)
        val btn_cancel = view.findViewById<AppCompatButton>(R.id.btn_cancel)
        val btn_checkin = view.findViewById<AppCompatButton>(R.id.btn_checkin)
        val btn_checkout = view.findViewById<AppCompatButton>(R.id.btn_checkout)
        val btn_approve = view.findViewById<AppCompatButton>(R.id.btn_approve)
        val ed_reason = view.findViewById<AppCompatEditText>(R.id.ed_reason)

        if(status==1){
            btn_cancel.visibility = View.GONE
            btn_arrived.visibility = View.GONE
            btn_checkin.visibility = View.GONE
            btn_checkout.visibility = View.GONE
            btn_approve.visibility= View.GONE
            ed_reason.visibility=View.VISIBLE

        }
        else if(status==2){//btn_arrived
            rejectButton.visibility = View.GONE
            btn_cancel.visibility = View.GONE
            btn_checkin.visibility = View.GONE
            btn_checkout.visibility = View.GONE
            btn_approve.visibility= View.GONE
        }else if(status==3){//btn_approve
            rejectButton.visibility = View.GONE
            btn_arrived.visibility = View.GONE
            btn_checkin.visibility = View.GONE
            btn_checkout.visibility = View.GONE
            btn_approve.visibility= View.VISIBLE
        }
        else if(status==4){//btn_checkin
            rejectButton.visibility = View.GONE
            btn_arrived.visibility = View.GONE
            btn_checkout.visibility = View.GONE
            btn_cancel.visibility = View.GONE
            btn_approve.visibility= View.GONE
        }
        else if(status==5){//btn_checkout
            rejectButton.visibility = View.GONE
            btn_arrived.visibility = View.GONE
            btn_checkin.visibility = View.GONE
            btn_cancel.visibility = View.GONE
            btn_approve.visibility= View.GONE
        }
        else if(status==6){//btn_cancel
            rejectButton.visibility = View.GONE
            btn_arrived.visibility = View.GONE
            btn_checkout.visibility = View.GONE
            btn_checkin.visibility = View.GONE
            btn_approve.visibility= View.GONE
        }
        rejectButton.setOnClickListener {
            sendRejectRequest(visitorId)
            dismiss()
        }

        btn_arrived.setOnClickListener {
            sendArrivedRequest(visitorId)
            dismiss()
        }


        btn_cancel.setOnClickListener {
            CancelMetting(visitorId)
            dismiss()
        }
        btn_checkin.setOnClickListener {
            Checkin(visitorId)
            dismiss()
        }

        btn_checkout.setOnClickListener {
            Checkout(visitorId)
            dismiss()
        }
        btn_approve.setOnClickListener {
            Approve(visitorId)
            dismiss()
        }

        return view
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

    private fun Approve(visitorId: Int) {
        val viewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]
        viewModel.ApproveVisitor(visitorId)
    }
}

