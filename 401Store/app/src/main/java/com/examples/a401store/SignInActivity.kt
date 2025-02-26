@file:Suppress("DEPRECATION")

package com.examples.a401store

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.examples.a401store.Model.userModel
import com.examples.a401store.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class SignInActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivitySignInBinding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val googleSignInOptions =   GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        //intialize Firebase auth
        auth = Firebase.auth
        //intialize Firebase database
        database = Firebase.database.reference
        //intialize Google sign in
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.btnCreate.setOnClickListener {
            userName = binding.edtName.text.toString().trim()
            email = binding.edtEmail.text.toString().trim()
            password = binding.edtPassword.text.toString().trim()

            if(email.isBlank() || userName.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please fill all detail", Toast.LENGTH_SHORT).show()
            }else{
                createAccount(email, password)
            }
        }

        binding.btnGoogle.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

            binding.txtHave.setOnClickListener {
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
            }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                Toast.makeText(this,"Account create successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, "Account created fail", Toast.LENGTH_SHORT).show()
                Log.d("Account", "createAccount: Failure", task.exception)
            }
        }
    }

    private fun saveUserData() {
        //retrieve data from input file
        userName = binding.edtName.text.toString()
        password = binding.edtPassword.text.toString()
        email = binding.edtEmail.text.toString()

        val url: String = "https://strore-8515a-default-rtdb.asia-southeast1.firebasedatabase.app/"

        val user = userModel(userName, email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        //save user data Firebase database
        var ref = FirebaseDatabase.getInstance(url)
        var rtref = ref.getReference()
        rtref.child("user").child(userId).setValue(user)
    }

    //launcher for google
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){     result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account: GoogleSignInAccount? = task.result
                val credential: AuthCredential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this,"Sign in failed 😢", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Sign in failed 😢", Toast.LENGTH_SHORT).show()
            }
        }
    }
}