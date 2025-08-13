//package com.example.memorygame
//
//import android.net.Uri
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageButton
//import android.widget.ImageView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//
//class ImagePickerAdapter(
//    private val items: MutableList<Uri>,
//    private val onRemoveAt: (Int) -> Unit
//) : RecyclerView.Adapter<ImagePickerAdapter.VH>() {
//
//    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val iv: ImageView = itemView.findViewById(R.id.ivPicked)
//        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
//        val v = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_selected_image, parent, false)
//        return VH(v)
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    override fun onBindViewHolder(holder: VH, position: Int) {
//        val uri = items[position]
//
//        // Use Glide so both local (content://) and remote (https://) show
//        Glide.with(holder.iv)
//            .load(uri)
//            .centerCrop()
//            .into(holder.iv)
//
//        holder.btnRemove.setOnClickListener {
//            // Delegate removal to activity lambda (keeps source of truth there)
//            onRemoveAt(holder.bindingAdapterPosition)
//        }
//    }
//}
