package com.innovu.visitor

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(
    private val onDeleteClick: (Uri) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val items = mutableListOf<Uri>()

    fun submitList(data: List<Uri>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uri = items[position]
        Glide.with(holder.itemView.context).load(uri).into(holder.imageView)

        holder.deleteButton.setOnClickListener {
            onDeleteClick(uri)
        }
    }

    override fun getItemCount(): Int = items.size
}
