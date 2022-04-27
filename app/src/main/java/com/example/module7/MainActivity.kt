package com.example.module7

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.module7.Model.AlmostVirtualMachine
import com.example.module7.Model.Calculations
import com.example.module7.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val program = AlmostVirtualMachine("""
                |var a '2';
                |var c '4 * ( 5 + a )';
                |var b;
                |= b 'a + c';
                |var i;
                |while 'i < 7';
                |if 'i % 2 = 0';
                |out i;
                |end;
                |++ i;
                |end;
                |out a;
                |out b;
                |out c;""".trimMargin()
        )
        program.doLog = true
        program.execute()
        print(program.output)
        /*try {
            val calculation = Calculations()
            val infixStr = "2"
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
        }*/
    }
}