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
                |int n;
                |= n '2 * 5';
                |arr A 'n - 1';
                |arr B '100 / n - 1';
                |int i;
                |while 'i < n - 1';
                |= B[i] '8 - i';
                |++ i;
                |end;
                |= i '0';
                |while 'i < n - 1';
                |= A[B[i / 2]] 'i';
                |++ i;
                |end;
                |out 'Array A = *{A}, n = *{n}, A[7] = *{A[6 + 1]}';""".trimMargin()
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