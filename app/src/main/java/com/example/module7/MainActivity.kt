package com.example.module7

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.module7.Model.Calculations
import com.example.module7.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        try {
            val calculation = Calculations()
            val infixStr = "14 * 3 - 7 * ( 3 - ( 9 % 4 ) )"
            val result = calculation.calculation(infixStr).toString()
            Toast.makeText(
                this@MainActivity,
                result,
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                this@MainActivity,
                "Error",
                Toast.LENGTH_SHORT
            ).show()

        }


    }
}