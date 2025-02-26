package com.examples.a401store.Adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.examples.a401store.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrices: MutableList<String>,
    private var cartDescription: MutableList<String>,
//    private val cartImages: MutableList<String>,
    private val cartQuantities: MutableList<Int>,
    private val cartIngredient: MutableList<String>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    //initialize Firebase auth
    private val auth = FirebaseAuth.getInstance()

    init {
        val url: String = "https://strore-8515a-default-rtdb.asia-southeast1.firebasedatabase.app/"

        val ref = FirebaseDatabase.getInstance(url)
        val rtref = ref.getReference()
        val userId = auth.currentUser?.uid?:""
        val cartItemNumber = cartItems.size

        itemQuantities = IntArray(cartItemNumber){1}
        cartItemReference = rtref.child("user").child(userId).child("cartitem")
    }

    companion object{
        private var itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemReference: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                txtCartFoodName.text = cartItems[position]
                txtCartFoodPrice.text = cartItemPrices[position]

//                load image using glide
//                val uriString = cartImages[position]
//                val uri = Uri.parse(uriString)
//                Glide.with(context).load(uri)
//                    .listener(object : RequestListener<Drawable>{
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable>,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        Log.d("Glide", "onLoadFailed: Image Loading Failed" )
//                        return false
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable,
//                        model: Any,
//                        target: Target<Drawable>?,
//                        dataSource: DataSource,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        Log.d("Glide", "onLoadFailed: Image Loading Success" )
//                        return false
//                    }
//                })
//                    .into(imgCart)


                txtQuantity.text = quantity.toString()

                binding.btnPlus.setOnClickListener {
                    increaseQuantity(position)
                }

                binding.btnMinus.setOnClickListener {
                    decreaseQuantity(position)
                }

                btnDelete.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteQuantity(itemPosition)
                    }
                }
            }
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                cartQuantities[position] = itemQuantities[position]
                binding.txtQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                cartQuantities[position] = itemQuantities[position]
                binding.txtQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun deleteQuantity(position: Int) {
            val positionRetrieve = position
            getUniqueKeyAtPosition(positionRetrieve){uniqueKey ->
                if (uniqueKey != null){
                    removeItem(position, uniqueKey)
                }
            }
        }
    }

    private fun removeItem(position: Int, uniqueKey: String) {
        if(uniqueKey != null){
            cartItemReference.child(uniqueKey).removeValue().addOnSuccessListener {
                cartItems.removeAt( position)
//                cartImages.removeAt(position)
                cartDescription.removeAt(position)
                cartQuantities.removeAt(position)
                cartItemPrices.removeAt(position)
                cartIngredient.removeAt(position)
                Toast.makeText(context, "Items deleted", Toast.LENGTH_SHORT).show()

                //update itemQuantities
                itemQuantities = itemQuantities.filterIndexed{index, i -> index != position }.toIntArray()
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, cartItems.size)
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
        cartItemReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniqueKey: String? = null

                //loop for snapshot children
                snapshot.children.forEachIndexed{index, dataSnapshot ->
                    if(index == positionRetrieve){
                        uniqueKey = dataSnapshot.key
                        return@forEachIndexed
                    }
                }
                onComplete(uniqueKey)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    //get update quantity
    fun getUpdatedItemQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantities)
        return itemQuantity
    }
}