package com.examples.a401store

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.examples.a401store.databinding.ActivityChooseLocationBinding

class ChooseLocationActivity : AppCompatActivity() {
    private val binding: ActivityChooseLocationBinding by lazy {
        ActivityChooseLocationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val locationList:Array<String> = arrayOf("TP Hồ Chí Minh", "Hà Nội", "Hải Phòng", "Đà Nẵng", "Long An", "Cần Thơ", "Cà Mau")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,locationList)
        val autoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}