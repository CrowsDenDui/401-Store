package com.examples.a401store.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.examples.a401store.Adapter.MenuAdapter
import com.examples.a401store.Adapter.PopularAdapter
import com.examples.a401store.Model.menuItem

import com.examples.a401store.R
import com.examples.a401store.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<menuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.txtView.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }

        //retrieve and display popular item
        retrieveAndDisplayPopularItem()

        return binding.root
    }

    private fun retrieveAndDisplayPopularItem() {

        //get reference to the database
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()

        //retrieve menu item from database
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshoot in snapshot.children){
                    val menuItem = foodSnapshoot.getValue(menuItem::class.java)
                    menuItem?.let { menuItems.add(it) }
                }

                //display a random popular items
                randomPopularItems()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun randomPopularItems() {
        //Create as shuffled list of menu items
        val index = menuItems.indices.toList().shuffled()
        val menuItemToShow = 6
        val subsetMenuItems = index.take(menuItemToShow).map{menuItems[it]}
        setPopularItemAdater(subsetMenuItems)
    }

    private fun setPopularItemAdater(subsetMenuItems: List<menuItem>) {
        val adapter = MenuAdapter(subsetMenuItems.toMutableList(), requireContext())
        binding.rvPopularItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPopularItems.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })

    }

}