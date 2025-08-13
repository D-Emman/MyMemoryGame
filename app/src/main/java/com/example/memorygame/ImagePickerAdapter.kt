package com.example.memorygame

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.memorygame.models.BoardSize
import kotlin.math.min

class ImagePickerAdapter(
    private val context: Context,
    private val imageSources: List<Any>, // Can be Uri (local) or String (remote URL)
    private val boardSize: BoardSize,
    private val imageClickListener: ImageClickListener
) : RecyclerView.Adapter<ImagePickerAdapter.ViewHolder>() {

    interface ImageClickListener {
        fun onPlaceholderClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_image, parent, false)
        val cardWidth = parent.width / boardSize.getWidth()
        val cardHeight = parent.height / boardSize.getHeight()
        val cardSideLength = min(cardWidth, cardHeight)
        val layoutParams = view.findViewById<ImageView>(R.id.ivCustomView).layoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        return ViewHolder(view)
    }

    override fun getItemCount() = boardSize.getNumPairs()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < imageSources.size) {
            holder.bind(imageSources[position])
        } else {
            holder.bindPlaceholder()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCustomImage = itemView.findViewById<ImageView>(R.id.ivCustomView)

        fun bind(source: Any) {
            when (source) {
                is Uri -> {
                    // Local image
                    ivCustomImage.setImageURI(source)
                }
                is String -> {
                    // Remote image
                    Glide.with(context)
                        .load(source)
                        .centerCrop()
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_broken_image)
                        .into(ivCustomImage)
                }
            }
            ivCustomImage.setOnClickListener(null) // Prevent placeholder click
        }

        fun bindPlaceholder() {
            ivCustomImage.setImageResource(R.drawable.ic_add_photo)
            ivCustomImage.setOnClickListener {
                imageClickListener.onPlaceholderClicked()
            }
        }
    }
}
