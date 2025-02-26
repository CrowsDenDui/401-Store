package com.examples.a401store.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.examples.a401store.DetailActivity
import com.examples.a401store.Model.menuItem
import com.examples.a401store.databinding.MenuItemBinding

class MenuAdapter(
    private val menuItem: MutableList<menuItem>,
    private val requireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItem.size
    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailActivity(position)
                }
            }
        }

        private fun openDetailActivity(position: Int) {
            val menuItem = menuItem[position]

            //a intent to open details activity and pass data
            val i = Intent(requireContext, DetailActivity::class.java).apply {
                putExtra("MenuItemName",menuItem.foodName)
//                putExtra("MenuItemImage",menuItem.foodImgUrl)
                putExtra("MenuItemDesciption",menuItem.foodDescription)
                putExtra("MenuItemPrice",menuItem.foodPrice)
                putExtra("MenuItemIngredient",menuItem.foodIngredient)
            }

            //star the details activity
            requireContext.startActivity(i)

        }

        //set data into recycleview item name, price, img
        fun bind(position: Int) {
            val menuItem = menuItem[position]
            binding.apply {
                txtMenuFoodName.text = menuItem.foodName
                txtMenuPrice.text = menuItem.foodPrice
//                val uri = Uri.parse(menuItem.foodImgUrl)
//                Glide.with(requireContext).load(uri).into(imgMenuFood)

            }
        }
    }

}


