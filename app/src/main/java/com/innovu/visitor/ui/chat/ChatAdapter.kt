package com.innovu.visitor.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.innovu.visitor.R
import com.innovu.visitor.model.ChatMessage
import com.innovu.visitor.model.ChatUserModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ChatAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }
    private val messages = mutableListOf<ChatMessage>()
    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSentByMe) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_SENT)
            R.layout.chat_item_sent else R.layout.chat_item_received
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val textView = holder.itemView.findViewById<TextView>(R.id.textMessage)
        textView.text = messages[position].message
        val textViewTime = holder.itemView.findViewById<TextView>(R.id.textTime)
        val time = formatUtcToLocalTime(messages[position].sentAt)
        textViewTime.text = time
    }

    override fun getItemCount() = messages.size


    fun updateData(newList: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newList)
        notifyDataSetChanged()

    }

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }



    fun formatUtcToLocalTime(dateTime: String): String {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a") // 12-hour format with AM/PM
            val date = LocalDateTime.parse(dateTime, inputFormatter)
            outputFormatter.format(date)
        } catch (e: Exception) {
            ""
        }
    }
}
