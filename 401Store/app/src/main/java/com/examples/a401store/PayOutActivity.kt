package com.examples.a401store

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.examples.a401store.Model.orderDetail
import com.examples.a401store.databinding.ActivityPayOutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList

class PayOutActivity : AppCompatActivity() {
    lateinit var binding: ActivityPayOutBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var foodItemName: ArrayList<String>
    private lateinit var foodItemPrice: ArrayList<String>

    //    private lateinit var foodItemImg: ArrayList<String>
    private lateinit var foodItemDescription: ArrayList<String>
    private lateinit var foodItemIngredient: ArrayList<String>
    private lateinit var foodItemQuantity: ArrayList<Int>
    private lateinit var ref: DatabaseReference
    private lateinit var userId: String

    private val url: String =
        "https://strore-8515a-default-rtdb.asia-southeast1.firebasedatabase.app/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize Firebase and User details
        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance(url).getReference()

        //Set user data
        setUserData()

        //get user details from Firebase
        val i = intent
        foodItemName = i.getStringArrayListExtra("FoodItemName") as ArrayList<String>
        foodItemPrice = i.getStringArrayListExtra("FoodItemPrice") as ArrayList<String>
//        foodItemImg = i.getStringArrayListExtra("FoodItemImg") as ArrayList<String>
        foodItemIngredient = i.getStringArrayListExtra("FoodItemIngredient") as ArrayList<String>
        foodItemDescription = i.getStringArrayListExtra("FoodItemDescription") as ArrayList<String>
        foodItemQuantity = i.getIntegerArrayListExtra("FoodItemQuantity") as ArrayList<Int>

        totalAmount = calculateTotalAmount().toString() + "D"
        binding.edtEditTotal.isEnabled = false
        binding.edtEditTotal.setText(totalAmount)

        binding.btnPlaceMyOrder.setOnClickListener {
            //get data from textView
            name = binding.edtEditName.text.toString().trim()
            address = binding.edtEditAddress.text.toString().trim()
            phone = binding.edtEditPhone.text.toString().trim()
            if (name.isBlank() && address.isBlank() && phone.isBlank()) {
                Toast.makeText(this, "Please Enter All The Details", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid ?: ""
        val time = System.currentTimeMillis()
        val itemPushKey = ref.child("orderdetails").push().key
        val orderDetails = orderDetail(
            userId,
            name,
            foodItemName,
            foodItemPrice,
            foodItemQuantity,
            totalAmount,
            address,
            phone,
            time,
            itemPushKey,
            false,
            false
        )
        val orderRef = ref.child("orderdetail").child(itemPushKey!!)
        orderRef.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "Test")
            removeItemFromCart()
            addOrderToHistory(orderDetails)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to order", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOrderToHistory(orderDetails: orderDetail) {
        ref.child("user").child(userId).child("buyhistory").child(orderDetails.itemPushKey!!)
            .setValue(orderDetails).addOnSuccessListener {

            }
    }

    private fun removeItemFromCart() {
        val cartItemRef = ref.child("user").child(userId).child("cartitem")
        cartItemRef.removeValue()
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (i in 0 until foodItemPrice.size) {
            var price = foodItemPrice[i]
            var lastChar = price.last()
            var priceIntValue = if (lastChar == 'D') {
                price.dropLast(1).toInt()
            } else {
                price.toInt()
            }
            var quantity = foodItemQuantity[i]
            totalAmount += priceIntValue * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val userRef = ref.child("user").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").getValue(String::class.java) ?: ""
                        val address = snapshot.child("address").getValue(String::class.java) ?: ""
                        val phone = snapshot.child("phone").getValue(String::class.java) ?: ""
                        binding.apply {
                            edtEditName.setText(name)
                            edtEditAddress.setText(address)
                            edtEditPhone.setText(phone)
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}