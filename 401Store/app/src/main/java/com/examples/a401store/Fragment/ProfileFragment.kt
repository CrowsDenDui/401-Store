package com.examples.a401store.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.examples.a401store.Model.userModel
import com.examples.a401store.R
import com.examples.a401store.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val url: String = "https://strore-8515a-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val auth = FirebaseAuth.getInstance()
    private val ref = FirebaseDatabase.getInstance(url)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setUserData()

        binding.btnSaveInfo.setOnClickListener {
            val name = binding.edtProfileName.text.toString()
            val email = binding.edtProfileEmail.text.toString()
            val address = binding.edtProfileAddress.text.toString()
            val phone = binding.edtProfilePhone.text.toString()

            updateUserData(name, email, address, phone)
        }

        return binding.root
    }

    private fun updateUserData(name: String, email: String, address: String, phone: String) {
        val userId = auth.currentUser?.uid
        if (userId != null){
            val userRef = ref.getReference("user").child(userId)

            val userData = hashMapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phone
            )
            userRef.setValue(userData).addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Profile Updated fail 😢", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null){
            val userRef = ref.getReference("user").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val userProfile = snapshot.getValue(userModel::class.java)
                        if (userProfile != null){
                            binding.edtProfileName.setText(userProfile.name)
                            binding.edtProfileAddress.setText(userProfile.address)
                            binding.edtProfileEmail.setText(userProfile.email)
                            binding.edtProfilePhone.setText(userProfile.phone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        }
    }

}