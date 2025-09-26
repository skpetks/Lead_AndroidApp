package com.innovu.visitor.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innovu.visitor.BuildConfig
import com.innovu.visitor.R
import com.innovu.visitor.model.ChatUserModel
import com.innovu.visitor.model.UserModel
class UserAdapter(
    private var users: List<ChatUserModel>,
    private val onItemClick: (ChatUserModel) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var filteredUsers: List<ChatUserModel> = users.toList()

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.userName)
        val email = view.findViewById<TextView>(R.id.userEmail)
        val image = view.findViewById<ImageView>(R.id.userImage)

        fun bind(user: ChatUserModel) {
            name.text = user.userName.capitalizeFirst()
            email.text = user.email
            if (!user.photo.isNullOrEmpty()) {
                val newUrl = BuildConfig.SERVICE_END_POINT.replace("/api/", "/")
                val fullUrl = newUrl + (user.photo ?: "")
                Glide.with(itemView).load(fullUrl).circleCrop().into(image)
            } else {
                image.setImageResource(R.drawable.profile)
            }
            itemView.setOnClickListener { onItemClick(user) }
        }
    }

    fun String.capitalizeFirst(): String =
        this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(filteredUsers[position])
    }

    override fun getItemCount(): Int = filteredUsers.size

    fun updateData(newList: List<ChatUserModel>) {
        users = newList
        filteredUsers = newList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredUsers = if (query.isEmpty()) {
            users
        } else {
            users.filter {
                it.userName.contains(query, ignoreCase = true) ||
                        it.email?.contains(query, ignoreCase = true) == true
            }
        }
        notifyDataSetChanged()
    }
}
