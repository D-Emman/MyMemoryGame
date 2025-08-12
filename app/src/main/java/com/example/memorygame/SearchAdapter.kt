package com.example.memorygame

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter(
    private val gameList: List<String>,  // Could be List<Game> if you have a model
    private val onSelectionChanged: (List<String>) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {


    private val selectedItems = mutableSetOf<String>()
    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//      val ivThumbnail: ImageView = itemView.findViewById(R.id.ivGameThumbnail)
        val tvName: TextView = itemView.findViewById(R.id.tvGameName)

//        fun bind(gameName: String) {
//            tvName.text = gameName
//            // If you have image loading logic, add it here (Glide/Picasso)
//            itemView.setOnClickListener { onSelectionChanged(gameName) }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game_search, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
//        holder.bind(gameList[position])
        val item = gameList[position]
        holder.tvName.text = item

        // Highlight selection
        holder.itemView.setBackgroundColor(
            if (selectedItems.contains(item)) Color.LTGRAY else Color.TRANSPARENT
        )

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
            } else {
                selectedItems.add(item)
            }
            notifyItemChanged(position)
            onSelectionChanged(selectedItems.toList())
        }
    }


    override fun getItemCount(): Int = gameList.size
}
