package com.example.module7

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.module7.Model.AlmostVirtualMachine
import com.example.module7.databinding.ActivityMainBinding
import android.view.View.OnClickListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val outCloseBtn = findViewById<ImageButton>(R.id.imageButton)
        outCloseBtn.setOnClickListener {
            findViewById<ScrollView>(R.id.scrollView).visibility = View.GONE
            outCloseBtn.visibility = View.GONE
        }
        binding.buttonRun.setOnClickListener {
            val program = AlmostVirtualMachine(
                """
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
                |out 'Array A = *{A}, n = *{n}';
                |out 'A[7] = *{A[6 + 1]}';""".trimMargin()
            )
            program.doLog = true
            program.execute()
            if (program.output != "") {
                findViewById<ScrollView>(R.id.scrollView).visibility = View.VISIBLE
                outCloseBtn.visibility = View.VISIBLE
                findViewById<TextView>(R.id.output).text = program.output
            }
        }
    }

}