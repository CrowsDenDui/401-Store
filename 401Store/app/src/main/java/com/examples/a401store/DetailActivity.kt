package com.examples.a401store

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.examples.a401store.Model.cartItem
import com.examples.a401store.databinding.ActivityDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding

    private var foodName: String? = null
    private var foodImg: String? = null
    private var foodDescription: String? = null
    private var foodIngredient: String? = null
    private var foodPrice: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize Firebase auth
        auth = FirebaseAuth.getInstance()

        foodName = intent.getStringExtra("MenuItemName")
        foodDescription = intent.getStringExtra("MenuItemDesciption")
        foodIngredient = intent.getStringExtra("MenuItemIngredient")
        foodPrice = intent.getStringExtra("MenuItemPrice")
//        foodImg = intent.getStringExtra("MenuItemImage")

        with(binding) {
            txtDetailTitle.text = foodName
            txtDescription.text = foodDescription
            txtIngredients.text = foodIngredient
//            Glide.with(this@DetailActivity).load(Uri.parse(foodImg)).into(imgFoodDetail)

        }


        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAddToCart.setOnClickListener {
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        val url: String = "https://strore-8515a-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val userId = auth.currentUser?.uid?:""

        //Create a cart item object
        val cartItem = cartItem(foodName.toString(), foodPrice.toString(), foodDescription.toString(),1)

        //save data to cart item to firebase
        var ref = FirebaseDatabase.getInstance(url)
        var rtref = ref.getReference()
        rtref.child("user").child(userId).child("cartitem").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this, "Items added into cart successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Items didn't add",Toast.LENGTH_SHORT).show()
        }
    }
}