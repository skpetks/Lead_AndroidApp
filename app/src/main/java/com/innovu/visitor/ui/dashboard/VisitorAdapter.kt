package com.innovu.visitor.ui.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innovu.visitor.BuildConfig
import com.innovu.visitor.R
import com.innovu.visitor.model.Visitor
import com.innovu.visitor.ui.bottomsheet.RejectBottomSheet
import com.innovu.visitor.ui.bottomsheet.ScheduleBottomSheet
import com.innovu.visitor.utlis.StorePrefData
import com.innovu.visitor.utlis.UserRole
import com.innovu.visitor.utlis.Utils
import com.innovu.visitor.utlis.Utils.setStatusAppearance

class VisitorAdapter(private var fullList: List<Visitor>,    private val activity: FragmentActivity,) :
    RecyclerView.Adapter<VisitorAdapter.VisitorViewHolder>() {

    private var displayedList: MutableList<Visitor> = fullList.toMutableList()

    inner class VisitorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvVisitorName: TextView = itemView.findViewById(R.id.tvVisitorName)
        val tvMeetingType: TextView = itemView.findViewById(R.id.tv_meeting_type)
        val tvStartTime: TextView = itemView.findViewById(R.id.tvStartTime)
        val rejectButton: AppCompatButton = itemView.findViewById(R.id.btn_reject)
        val tv_VisitorCode: AppCompatTextView = itemView.findViewById(R.id.tv_VisitorCode)
        val btn_cancel: AppCompatButton = itemView.findViewById(R.id.btn_cancel)
        val btn_arrived: AppCompatButton = itemView.findViewById(R.id.btn_arrived)
        val btn_reschedule: AppCompatButton = itemView.findViewById(R.id.btn_reschedule)
        val btn_checkin: AppCompatButton = itemView.findViewById(R.id.btn_checkin)
        val btn_checkout: AppCompatButton = itemView.findViewById(R.id.btn_checkout)
        val tv_statusdetail: AppCompatTextView = itemView.findViewById(R.id.tv_statusdetail)
        val btn_approve: AppCompatButton = itemView.findViewById(R.id.btn_approve)
        val tv_meetingdate: AppCompatTextView = itemView.findViewById(R.id.tv_meeting_date)

        val image_profile: ImageView = itemView.findViewById(R.id.image_profile)
        val btn_CallApproval: AppCompatButton = itemView.findViewById(R.id.btn_CallApproval)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visitor, parent, false)
        return VisitorViewHolder(view)
    }

    override fun onBindViewHolder(holder: VisitorViewHolder, position: Int) {
        val visitor = displayedList[position]
        holder.tvVisitorName.text = visitor.visitorName
        holder.tvMeetingType.text = visitor.meetingType

        holder.tv_meetingdate.text="${Utils.getDateOnly(visitor.operationDate)}"
    //    holder.tvStartTime.text = "${Utils.getTimeOnly(visitor.startTime)}-${Utils.getTimeOnly(visitor.endTime)}"

        holder.tvStartTime.text = "${Utils.getTimeOnly(visitor.startTime)}"
        holder.tv_VisitorCode.text = "Code:${visitor.visitorCode}"
        holder.tv_statusdetail.text = "${visitor.recordStatus}"
        holder.rejectButton.setOnClickListener {
            val bottomSheet = RejectBottomSheet(visitorId = visitor.visitorID,1)
            bottomSheet.show(activity.supportFragmentManager, bottomSheet.tag)
        }
        val role = UserRole.fromName("SECURITY")

        holder.btn_reschedule.setOnClickListener {

            if(visitor.meetingUserID== StorePrefData.UserIId){
                val bottomSheet = ScheduleBottomSheet(visitorId = visitor.visitorID)
                bottomSheet.show(activity.supportFragmentManager, "ScheduleBottomSheet")
            }else{
                Toast.makeText(activity,"unable to reschedule,", Toast.LENGTH_SHORT).show();
            }

        }

        setStatusAppearance( holder.tv_statusdetail,visitor.recordStatusID,activity)

        holder.btn_arrived.setOnClickListener {
            val bottomSheet = RejectBottomSheet(visitorId = visitor.visitorID,2)
            bottomSheet.show(activity.supportFragmentManager, "ScheduleBottomSheet")
        }

        holder.btn_approve.setOnClickListener {
            val bottomSheet = RejectBottomSheet(visitorId = visitor.visitorID,3)
            bottomSheet.show(activity.supportFragmentManager, "ScheduleBottomSheet")
        }
        holder.btn_checkin.setOnClickListener {
            val bottomSheet = RejectBottomSheet(visitorId = visitor.visitorID,4)
            bottomSheet.show(activity.supportFragmentManager, "ScheduleBottomSheet")
        }
        holder.btn_checkout.setOnClickListener {
            val bottomSheet = RejectBottomSheet(visitorId = visitor.visitorID,5)
            bottomSheet.show(activity.supportFragmentManager, "ScheduleBottomSheet")
        }
        holder.btn_cancel.setOnClickListener {
            val bottomSheet = RejectBottomSheet(visitorId = visitor.visitorID,6)
            bottomSheet.show(activity.supportFragmentManager, "ScheduleBottomSheet")
        }


        holder.btn_CallApproval.setOnClickListener {

            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$00000000")
            }
            activity.startActivity(intent)
        }



        if(visitor.recordStatusID==2 && StorePrefData.RoleId == role?.id)//if logged in User in security
        {
            holder.btn_arrived.visibility= View.VISIBLE

        }else{
            holder.btn_arrived.visibility= View.GONE
        }

        if(visitor.recordStatusID==2 && visitor.meetingUserID==  StorePrefData.UserIId)//if logged in User in security
        {
            holder.btn_cancel.visibility= View.VISIBLE
            holder.btn_CallApproval.visibility=View.GONE
        }else{
            holder.btn_cancel.visibility= View.GONE
        }


        if(visitor.recordStatusID==3 && visitor.meetingUserID==  StorePrefData.UserIId)//if logged in User in security
        {
            holder.btn_approve.visibility= View.VISIBLE
        }else{
            holder.btn_approve.visibility= View.GONE
        }







        if(visitor.recordStatusID==4 && StorePrefData.RoleId == role?.id)//if logged in User in security
        {        holder.btn_reschedule.visibility= View.GONE
            holder.btn_cancel.visibility= View.GONE
            holder.btn_checkin.visibility= View.VISIBLE
        }else{
            holder.btn_checkin.visibility= View.GONE
            holder.btn_CallApproval.visibility=View.GONE
        }
        if(visitor.recordStatusID==3 && StorePrefData.RoleId == role?.id)//if logged in User in security
        {
            holder.btn_checkin.visibility= View.VISIBLE
            holder.btn_CallApproval.visibility=View.VISIBLE
//            holder.btn_checkin.text= " Call Staff for Approval"
        }

        if(visitor.recordStatusID==5 && StorePrefData.RoleId == role?.id)//if logged in User in security
        {        holder.btn_reschedule.visibility= View.GONE
            holder.btn_cancel.visibility= View.GONE
            holder.btn_checkout.visibility= View.VISIBLE
        }else{
            holder.btn_checkout.visibility= View.GONE
        }

        if(visitor.meetingUserID==  StorePrefData.UserIId)//if logged in User in security
        {
            holder.btn_reschedule.visibility= View.GONE
            holder.rejectButton.visibility= View.VISIBLE
        }else{
            holder.btn_reschedule.visibility= View.GONE
            holder.rejectButton.visibility= View.GONE
        }

        if(visitor.recordStatusID==6)//if logged in User in security
        {
            holder.btn_arrived.visibility= View.GONE
            holder.btn_cancel.visibility= View.GONE
            holder.btn_approve.visibility= View.GONE
            holder.btn_checkin.visibility= View.GONE
            holder.btn_reschedule.visibility= View.GONE
            holder.rejectButton.visibility= View.GONE
            holder.btn_CallApproval.visibility=View.GONE

        }


        if(visitor.image!=null){
            if(visitor.image.size>1){
                val newUrl = BuildConfig.SERVICE_END_POINT.replace("/api/", "/")
                val fullUrl = newUrl + (visitor.image[1].imagePath ?: "")
                if (!visitor.image[1].imagePath.isNullOrEmpty()) {
                    Log.d("VisitorAdapter", "fullUrl : ${fullUrl} ")

                    Glide.with(activity)
                        .load(fullUrl)
                        .circleCrop()
                        .into(holder.image_profile)
                }
            }
            }




    }

    override fun getItemCount(): Int = displayedList.size





    fun updateData(newList: List<Visitor>) {
        Log.d("VisitorAdapter", "updateData() called with ${newList.size} visitors")
        fullList = ArrayList(newList)
        displayedList = ArrayList(newList)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val lowerCaseQuery = query.lowercase().trim()
        displayedList = if (lowerCaseQuery.isEmpty()) {
            fullList.toMutableList()
        } else {
            fullList.filter {
                it.visitorName.lowercase().contains(lowerCaseQuery) ||
                        it.visitorCode.lowercase().contains(lowerCaseQuery)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }



}
