package com.example.yourapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.R

class SearchAdapter(
    private val gameList: List<String>,  // Could be List<Game> if you have a model
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumbnail: ImageView = itemView.findViewById(R.id.ivGameThumbnail)
        val tvName: TextView = itemView.findViewById(R.id.tvGameName)

        fun bind(gameName: String) {
            tvName.text = gameName
            // If you have image loading logic, add it here (Glide/Picasso)
            itemView.setOnClickListener { onItemClick(gameName) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game_search, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(gameList[position])
    }

    override fun getItemCount(): Int = gameList.size
}
