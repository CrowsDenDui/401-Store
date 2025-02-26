package com.examples.a401store.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.examples.a401store.Adapter.CartAdapter
import com.examples.a401store.Model.cartItem
import com.examples.a401store.Model.menuItem
import com.examples.a401store.PayOutActivity
import com.examples.a401store.R
import com.examples.a401store.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: FirebaseDatabase
    private lateinit var foodName: MutableList<String>
    private lateinit var foodPrice: MutableList<String>
    private lateinit var foodDescription: MutableList<String>

    //    private lateinit var foodImg: MutableList<String>
    private lateinit var foodIngredient: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    private val url: String =
        "https://strore-8515a-default-rtdb.asia-southeast1.firebasedatabase.app/"

    private lateinit var binding: FragmentCartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        retrieveCartItems()

        binding.btnProceed.setOnClickListener {
            //get order item details before proceeding to check out
            getOrderItemsDetail()
        }


        return binding.root
    }

    private fun getOrderItemsDetail() {
        ref = FirebaseDatabase.getInstance(url)
        val orderIdRef: DatabaseReference =
            ref.reference.child("user").child(userId).child("cartitem")

        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
//        val foodImg = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodIngredient = mutableListOf<String>()
        //get items Quantities
        val foodQuantities = cartAdapter.getUpdatedItemQuantities()

        orderIdRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    //get the cartItems to respective list
                    val orderItems = foodSnapshot.getValue(cartItem::class.java)
                    //add item detail into list
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodDescription?.let { foodDescription.add(it) }
                    orderItems?.foodIngredient?.let { foodIngredient.add(it) }
//                    orderItems?.foodImg?.let { foodName.add(it) }
                }
                orderNow(foodName, foodPrice, foodDescription, foodIngredient, foodQuantities)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Order making failed. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescription: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQuantities: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val i = Intent(requireContext(), PayOutActivity::class.java)
            i.putExtra("FoodItemName", foodName as ArrayList<String>)
            i.putExtra("FoodItemPrice", foodPrice as ArrayList<String>)
//            i.putExtra("FoodItemImg",foodImg as ArrayList<String>)
            i.putExtra("FoodItemDescription", foodDescription as ArrayList<String>)
            i.putExtra("FoodItemIngredient", foodIngredient as ArrayList<String>)
            i.putExtra("FoodItemQuantity", foodQuantities as ArrayList<Int>)
            startActivity(i)
        }
    }

    private fun retrieveCartItems() {


        //database reference to Firebase

        userId = auth.currentUser?.uid ?: ""

        ref = FirebaseDatabase.getInstance(url)

        val foodRef: DatabaseReference = ref.reference.child("user").child(userId).child("cartitem")

        foodName = mutableListOf()
        foodPrice = mutableListOf()
        foodDescription = mutableListOf()
//        foodImg = mutableListOf()
        foodIngredient = mutableListOf()
        quantity = mutableListOf()

        //fetch data from the database
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    //get the cartItem object from the child node
                    val cartItems = foodSnapshot.getValue(cartItem::class.java)

                    //add cart item details to the list
                    cartItems?.foodName?.let { foodName.add(it) }
                    cartItems?.foodPrice?.let { foodPrice.add(it) }
                    cartItems?.foodDescription?.let { foodDescription.add(it) }
//                    cartItems?.foodImg?.let { foodImg.add(it) }
                    cartItems?.foodQuantity?.let { quantity.add(it) }
                    cartItems?.foodIngredient?.let { foodIngredient.add(it) }
                }

                setAdapter()
            }

            private fun setAdapter() {
                 cartAdapter = CartAdapter(
                    requireContext(),
                    foodName,
                    foodPrice,
                    foodDescription,
                    quantity,
                    foodIngredient
                )
                binding.rvCart.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.rvCart.adapter = cartAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "data not fetch", Toast.LENGTH_SHORT).show()
            }
        })

    }

    companion object {

    }
}