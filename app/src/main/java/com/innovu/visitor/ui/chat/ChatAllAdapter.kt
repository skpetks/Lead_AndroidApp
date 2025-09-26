package com.innovu.visitor.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.innovu.visitor.R
import com.innovu.visitor.model.ChatUser
import com.innovu.visitor.utlis.Utils.formatUtcToLocalTime

class ChatAllAdapter(
    private val onItemClick: (ChatUser) -> Unit
) : RecyclerView.Adapter<ChatAllAdapter.ChatViewHolder>() {

    private val fullList = mutableListOf<ChatUser>()
    private val filteredList = mutableListOf<ChatUser>()

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.userName)
        val message = view.findViewById<TextView>(R.id.lastMessage)
        val time = view.findViewById<TextView>(R.id.timestamp)
        val profile = view.findViewById<ImageView>(R.id.profileImage)
        val statusDot = view.findViewById<ImageView>(R.id.statusDot)

        fun bind(item: ChatUser) {
            name.text = item.userName.capitalizeFirst()
            message.text = item.message.capitalizeFirst()
            time.text = formatUtcToLocalTime(item.sentAt)

            itemView.setOnClickListener { onItemClick(item) }

            val statusDrawable = if (item.isOnline) {
                R.drawable.online_status_dot
            } else {
                R.drawable.offline_status_dot
            }

            statusDot.setBackgroundResource(statusDrawable)
        }
    }

    fun String.capitalizeFirst(): String =
        this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateData(newList: List<ChatUser>) {
        fullList.clear()
        fullList.addAll(newList)

        filteredList.clear()
        filteredList.addAll(newList)

        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val lowerQuery = query.lowercase().trim()
        filteredList.clear()

        if (lowerQuery.isEmpty()) {
            filteredList.addAll(fullList)
        } else {
            filteredList.addAll(fullList.filter {
                it.userName.lowercase().contains(lowerQuery)
            })
        }

        notifyDataSetChanged()
    }

    fun setUserStatus(userId: Int, isOnline: Boolean) {
        val index = filteredList.indexOfFirst { it.userID == userId }
        if (index != -1) {
            filteredList[index].isOnline = isOnline
            notifyItemChanged(index)
        }

        // Also update in fullList for consistent state
        fullList.find { it.userID == userId }?.isOnline = isOnline
    }
}

