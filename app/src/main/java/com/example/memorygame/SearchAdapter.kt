package com.example.memorygame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SearchAdapter(
    private val imageUrls: List<String>,
    private val listener: OnImageCheckedListener
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    interface OnImageCheckedListener {
        fun onImageChecked(url: String, isChecked: Boolean)
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewSearchItem)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxSearchItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_image, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val imageUrl = imageUrls[position]

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .into(holder.imageView)

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = false // default unchecked

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            listener.onImageChecked(imageUrl, isChecked)
        }

        holder.itemView.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
        }
    }

    override fun getItemCount(): Int = imageUrls.size
}
