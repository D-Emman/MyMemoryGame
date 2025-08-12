package com.example.memorygame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MemoryGameAdapter(
    private val images: List<String>,
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<MemoryGameAdapter.CardViewHolder>() {

    private val revealed = mutableSetOf<Int>()

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.cardImage)
        val cardBack: View = itemView.findViewById(R.id.cardBack)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_custom_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val imageUrl = images[position]

        if (revealed.contains(position)) {
            holder.cardBack.visibility = View.GONE
            Glide.with(holder.itemView.context).load(imageUrl).into(holder.imageView)
        } else {
            holder.cardBack.visibility = View.VISIBLE
            holder.imageView.setImageDrawable(null)
        }

        holder.itemView.setOnClickListener { onCardClick(position) }
    }

    override fun getItemCount(): Int = images.size

    fun revealCard(position: Int) {
        revealed.add(position)
        notifyItemChanged(position)
    }

    fun hideCard(position: Int) {
        revealed.remove(position)
        notifyItemChanged(position)
    }
}
