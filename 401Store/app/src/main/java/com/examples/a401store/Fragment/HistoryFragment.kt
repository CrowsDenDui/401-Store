package com.examples.a401store.Fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.examples.a401store.Adapter.BuyAgainAdapter
import com.examples.a401store.Model.orderDetail
import com.examples.a401store.R
import com.examples.a401store.databinding.FragmentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var ref: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItem: MutableList<orderDetail> = mutableListOf()

    private val url: String =
        "https://strore-8515a-default-rtdb.asia-southeast1.firebasedatabase.app/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment

        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance(url)

        //retrieve and display the user order history
        retrieveBuyHistory()
        return binding.root
    }

    private fun retrieveBuyHistory() {
        binding.clRecentBuy.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""

        val buyItemRef = ref.reference.child("user").child(userId).child("buyhistory")
        val shortingQuerry = buyItemRef.orderByChild("currentTime")

        shortingQuerry.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapShot in snapshot.children) {
                    val buyHistoryItem = buySnapShot.getValue(orderDetail::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItem.add(it)
                    }
                }
                listOfOrderItem.reverse()
                if (listOfOrderItem.isNotEmpty()) {
                    setDataInRecentBuyItem()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setDataInRecentBuyItem() {
        binding.clRecentBuy.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItem.firstOrNull()
        recentOrderItem?.let {
            with(binding) {
                txtHistoryFoodName.text = it.foodName?.firstOrNull() ?: ""
                txtHistoryPrice.text = it.foodPrice?.firstOrNull() ?: ""

//                val image = it.foodImg?.firstOrNull()?:""
//                val uri = Uri.parse(image)
//                Glide.with(requireContext()).load(uri).into(imgHistoryFood)

                listOfOrderItem.reverse()
                if (listOfOrderItem.isNotEmpty()) {
//                    setDataInRecentBuyItem()
                    setPreviousBuyItemRecyclerView()
                }
            }
        }
    }

    private fun setPreviousBuyItemRecyclerView() {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImg = mutableListOf<String>()
        for (i in 1 until listOfOrderItem.size) {
            listOfOrderItem[i].foodName?.firstOrNull()?.let { buyAgainFoodName.add(it) }
            listOfOrderItem[i].foodPrice?.firstOrNull()?.let { buyAgainFoodPrice.add(it) }
//            listOfOrderItem[i].foodImg?.firstOrNull()?.let { buyAgainFoodImg.add(it) }
        }
        val rv = binding.rvBuyAgain
        rv.layoutManager = LinearLayoutManager(requireContext())
        buyAgainAdapter =
            BuyAgainAdapter(buyAgainFoodName, buyAgainFoodPrice, buyAgainFoodImg, requireContext())
        rv.adapter = buyAgainAdapter
    }

}