package com.examples.a401store.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.examples.a401store.Adapter.MenuAdapter
import com.examples.a401store.R
import android.widget.SearchView
import com.examples.a401store.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private val originalMenuFoodName = listOf("Bột Chiên", "Mì Xào", "Nui Xào", "Miến Xào")
    private val originalMenuItemPrice = listOf("30.000VND", "35.000VND", "35.000VND", "35.000VND")
    private val originalMenuImage = listOf(
        R.drawable.menu1,
        R.drawable.menu2,
        R.drawable.menu3,
        R.drawable.menu4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private val fillterMenuFoodName = mutableListOf<String>()
    private val fillterMenuItemPrice = mutableListOf<String>()
    private val fillterMenuImage = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater,container, false)

//        adapter = MenuAdapter(fillterMenuFoodName,fillterMenuItemPrice,fillterMenuImage,requireContext())
        binding.rvMenu.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMenu.adapter = adapter

        //setup for search view
        setupSearchView()

        //showw all menu item
        showAllMenu()

        return binding.root
    }

    private fun showAllMenu() {
        fillterMenuFoodName.clear()
        fillterMenuItemPrice.clear()
        fillterMenuImage.clear()

        fillterMenuFoodName.addAll(originalMenuFoodName)
        fillterMenuItemPrice.addAll(originalMenuItemPrice)
        fillterMenuImage.addAll(originalMenuImage)

        adapter.notifyDataSetChanged()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                fillerMenuItem(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                fillerMenuItem(newText)
                return true
            }
        })
    }

    private fun fillerMenuItem(query: String?) {
        fillterMenuFoodName.clear()
        fillterMenuItemPrice.clear()
        fillterMenuImage.clear()

        originalMenuFoodName.forEachIndexed{index, foodName ->
            if(foodName.contains(query.toString(), ignoreCase = true)){
                fillterMenuFoodName.add(foodName)
                fillterMenuItemPrice.add(originalMenuItemPrice[index])
                fillterMenuImage.add(originalMenuImage[index])
            }
        }
        adapter.notifyDataSetChanged()
    }

    companion object {

    }
}