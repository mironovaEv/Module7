package com.example.module7

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.example.module7.Model.AlmostVirtualMachine
import com.example.module7.Model.RecyclerviewAdapter
import com.example.module7.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var recyclerviewAdapter: RecyclerviewAdapter

    private val itemTouchHelper by lazy {
        val itemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(UP or DOWN or START or END, RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val recyclerviewAdapter = recyclerView.adapter as RecyclerviewAdapter
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition
                    recyclerviewAdapter.moveItem(fromPosition, toPosition)

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    recyclerviewAdapter.onItemDismiss(position)


                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.scaleY = 1.3f
                        viewHolder?.itemView?.alpha = 0.7f

                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.scaleY = 1.0f
                    viewHolder.itemView.alpha = 1.0f
                }

            }
        ItemTouchHelper(itemTouchCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        itemTouchHelper.attachToRecyclerView(binding.rcView)
        recyclerviewAdapter = RecyclerviewAdapter()
        binding.rcView.adapter = recyclerviewAdapter

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
                //findViewById<TextView>(R.id.output).text = program.output
                var str = ""
                for (i in 0 until recyclerviewAdapter.arrayItems.size) {
                    str += recyclerviewAdapter.arrayItems[i].number
                }

                binding.output.text = str
            }
        }
    }

    private fun init() {
        binding.buttonAdd.setOnClickListener {
            val rnds = (0..10).random()
            recyclerviewAdapter.addBlocks(rnds)
        }
    }
}