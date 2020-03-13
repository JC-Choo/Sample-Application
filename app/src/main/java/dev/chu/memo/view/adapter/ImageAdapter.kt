package dev.chu.memo.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import dev.chu.memo.R
import dev.chu.memo.data.local.ImageData
import dev.chu.memo.databinding.ItemImageBinding

class ImageAdapter(private val items: MutableList<ImageData>): RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    fun addItem(item: ImageData) {
        items.add(item)
        notifyDataSetChanged()
    }

    fun setItems(item: List<ImageData>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.item = items[position]
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<ItemImageBinding>(view)!!
    }
}