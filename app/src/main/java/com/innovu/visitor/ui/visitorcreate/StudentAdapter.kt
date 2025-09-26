package com.innovu.visitor.ui.visitorcreate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(
    private var students: List<Student>,
    private val onItemClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    fun filterList(filtered: List<Student>) {
        students = filtered
        notifyDataSetChanged()
    }

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(android.R.id.text1)
        init {
            itemView.setOnClickListener {
                onItemClick(students[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.tvName.text = "${students[position].firstName} (${students[position].contactNumber})"
    }

    override fun getItemCount(): Int = students.size
}
