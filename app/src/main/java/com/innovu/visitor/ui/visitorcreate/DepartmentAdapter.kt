package com.innovu.visitor.ui.visitorcreate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.innovu.visitor.model.Department


class DepartmentAdapter(
    private val originalList: MutableList<Department>,
    private val onItemClick: (Department) -> Unit
) : RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder>() {

    private var filteredList = originalList.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return DepartmentViewHolder(view)
    }

    override fun getItemCount() = filteredList.size

    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
        val department = filteredList[position]
        holder.textView.text = department.departmentName
        holder.itemView.setOnClickListener {
            onItemClick(department)
        }
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.departmentName.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    class DepartmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)
    }
}
