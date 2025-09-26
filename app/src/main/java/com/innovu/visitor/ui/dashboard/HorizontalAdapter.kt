package com.innovu.visitor.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.innovu.visitor.R
import com.innovu.visitor.model.MeetingStatusData
import com.innovu.visitor.utlis.Utils.setStatusAppearance

class HorizontalAdapter(private val list: List<MeetingStatusData>, private val activity: FragmentActivity, private val onStatusClick: (MeetingStatusData) -> Unit) : RecyclerView.Adapter<HorizontalAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btn_filter: AppCompatTextView = view.findViewById(R.id.btn_filter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
          holder.btn_filter.text=list[position].meetingStatus

        if(item.meetingCount>0){
            holder.btn_filter.text = "${item.meetingStatus} (${item.meetingCount})"
        }
        holder.btn_filter.setOnClickListener {
            onStatusClick(item)
        }
        setStatusAppearance( holder.btn_filter,item.meetingStatusID,activity)
    }
}
