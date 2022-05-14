package com.example.module7

import android.os.Bundle
import android.view.View
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
        val buttonRun = binding.buttonRun
        val consoleOpen = binding.consoleOpen
        val console = binding.console
        val output = binding.Output
        val input = binding.Input
        val buttonResume = binding.buttonResume
        val buttonRestart = binding.buttonRestart
        val consoleClose = binding.consoleClose
        setContentView(view)

        init()
        itemTouchHelper.attachToRecyclerView(binding.rcView)
        recyclerviewAdapter = RecyclerviewAdapter()
        binding.rcView.adapter = recyclerviewAdapter

        val code = """
                |int n;
                |int m;
                |in n;
                |in m;
                |arr A 'n * m';
                |int i;
                |int j;
                |while 'i < n';
                    |while 'j < m';
                        |in A[i * m + j];
                        |++ j;
                    |end;
                    |++ i;
                    |= j '0';
                |end;
                |arr B 'm';
                |= i '0';
                |while 'i < n';
                    |while 'j < m';
                        |= B[j] 'A[i * m + j]';
                        |++ j;
                    |end;
                    |out '*{B}';
                    |++ i;
                    |= j '0';
                |end;""".trimMargin()

        var expectingInput: Boolean? = null

        fun updateConsole(program: AlmostVirtualMachine?) {
            console.visibility = View.VISIBLE
            consoleClose.visibility = View.VISIBLE
            when (expectingInput) {
                null -> {
                    buttonRestart.visibility = View.GONE
                    buttonResume.visibility = View.GONE
                    input.visibility = View.GONE
                }
                true -> {
                    buttonRestart.visibility = View.GONE
                    buttonResume.visibility = View.VISIBLE
                    input.visibility = View.VISIBLE
                    input.requestFocus()
                }
                else -> {
                    buttonRestart.visibility = View.VISIBLE
                    buttonResume.visibility = View.GONE
                    input.visibility = View.GONE
                    output.text = program?.output ?: output.text
                }
            }
        }

        fun run() {
            val program = AlmostVirtualMachine(code)
            program.doLog = true
            output.text = null

            buttonResume.setOnClickListener {
                program.output = input.text.toString() + "\n"
                program.input = input.text.toString()
                try {
                    program.execute()
                } catch (e: Error) {
                    program.output += e.message
                }
                expectingInput = false
                updateConsole(program)
            }

            if (program.findInstruction("in") == null) {
                try {
                    program.execute()
                } catch (e: Error) {
                    program.output += e.message
                }
                expectingInput = false
            } else {
                expectingInput = true
            }
            updateConsole(program)
        }
        buttonRun.setOnClickListener {
            input.text = null
            run()
        }
        buttonRestart.setOnClickListener{
            run()
        }
        consoleClose.setOnClickListener {
            console.visibility = View.GONE
            consoleClose.visibility = View.GONE
            buttonResume.visibility = View.GONE
            buttonRestart.visibility = View.GONE
        }
        consoleOpen.setOnClickListener {
            updateConsole(null)
        }
    }

    private fun init() {
        binding.buttonAdd.setOnClickListener {
            val rnds = (0..10).random()
            recyclerviewAdapter.addBlocks(rnds)
        }
    }
}