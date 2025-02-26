package com.examples.a401store.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.examples.a401store.DetailActivity
import com.examples.a401store.databinding.PopularItemBinding

class PopularAdapter(private val items: List<String>, private val price: List<String>, private val image: List<Int>, private val requireContext: Context): RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item = items[position]
        val images = image[position]
        val price = price[position]
        holder.bind(item,price,images)
        holder.itemView.setOnClickListener{
            val i = Intent(requireContext, DetailActivity::class.java)
            i.putExtra("MenuItemName", item)
            i.putExtra("MenuItemImage", images)
            requireContext.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PopularViewHolder (private val binding: PopularItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val imagesView = binding.imgFood
        fun bind(item: String, price: String, images: Int){
            binding.txtFoodName.text = item
            binding.txtPrice.text = price
            imagesView.setImageResource(images)
        }
    }
}