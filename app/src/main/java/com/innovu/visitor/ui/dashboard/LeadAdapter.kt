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
import com.innovu.visitor.model.Lead
import com.innovu.visitor.model.Visitor
import com.innovu.visitor.ui.bottomsheet.RejectBottomSheet
import com.innovu.visitor.ui.bottomsheet.ScheduleBottomSheet
import com.innovu.visitor.utlis.StorePrefData
import com.innovu.visitor.utlis.UserRole
import com.innovu.visitor.utlis.Utils
import com.innovu.visitor.utlis.Utils.setStatusAppearance

class LeadAdapter(private var fullList: List<Lead>,    private val listener: OnLeadActionListener, private val activity: FragmentActivity,) :
    RecyclerView.Adapter<LeadAdapter.VisitorViewHolder>() {

    private var displayedList: MutableList<Lead> = fullList.toMutableList()

    inner class VisitorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvVisitorName: TextView = itemView.findViewById(R.id.tvVisitorName)
        val tvMeetingType: TextView = itemView.findViewById(R.id.tv_meeting_type)

        val tv_statusdetail: AppCompatTextView = itemView.findViewById(R.id.tv_statusdetail)
        val tv_meetingdate: AppCompatTextView = itemView.findViewById(R.id.tv_meeting_date)

        val btn_CallApproval: AppCompatButton = itemView.findViewById(R.id.btn_CallApproval)

        val tvStartTime: AppCompatTextView = itemView.findViewById(R.id.tvStartTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lead, parent, false)
        return VisitorViewHolder(view)
    }

    override fun onBindViewHolder(holder: VisitorViewHolder, position: Int) {
        val visitor = displayedList[position]
        holder.tvVisitorName.text = "${visitor.name} ${visitor.company}"
        holder.tvMeetingType.text = visitor!!.requirement.toString()

        holder.tv_meetingdate.text="${ visitor.requirement}"
        holder.tvStartTime.text = "${Utils.getDateOnly(visitor.leadDate)}"
        holder.tv_statusdetail.text = "${visitor.leadsrc}"
        holder.tvMeetingType.text ="${visitor.stage}"


        holder.btn_CallApproval.setOnClickListener {

//            val intent = Intent(Intent.ACTION_DIAL).apply {
//                data = Uri.parse("tel:$00000000")
//            }
//            activity.startActivity(intent)

            listener.onCallClicked(visitor)
        }
    }

    override fun getItemCount(): Int = displayedList.size





    fun updateData(newList: List<Lead>) {
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
                it.name.lowercase().contains(lowerCaseQuery) ||
                        it.company.lowercase().contains(lowerCaseQuery)||
                        it.customerFeed.lowercase().contains(lowerCaseQuery)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }



}
