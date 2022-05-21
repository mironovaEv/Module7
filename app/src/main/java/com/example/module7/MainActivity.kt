package com.example.module7

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.example.module7.Model.AlmostVirtualMachine
import com.example.module7.Model.Block
import com.example.module7.Model.RecyclerviewAdapter
import com.example.module7.databinding.ActivityMainBinding
import com.leinardi.android.speeddial.SpeedDialActionItem


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var recyclerviewAdapter: RecyclerviewAdapter
    private lateinit var blockList: ArrayList<Block>
    private lateinit var prefs: SharedPreferences

    companion object {
        const val VAR = "var"
        const val ASSIGNMENT = "assignment"
        const val IN = "in"
        const val OUT = "out"
        const val IF = "if"
        const val ELSE = "else"
        const val WHILE = "while"
        const val BLOCK_END = "end"
        const val ARRAY = "array"
        const val INCREMENT = "increment"
        const val DECREMENT = "decrement"


    }

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
        prefs = getSharedPreferences(
            "com.example.app", Context.MODE_PRIVATE
        )
        init()
        itemTouchHelper.attachToRecyclerView(binding.rcView)
        blockList = ArrayList()
        recyclerviewAdapter = RecyclerviewAdapter()
        binding.rcView.adapter = recyclerviewAdapter

        var expectingInput: Boolean? = null

        fun updateConsole(program: AlmostVirtualMachine?) {
            console.visibility = View.VISIBLE
            consoleClose.visibility = View.VISIBLE
            binding.buttonAdd.visibility = View.GONE
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
            val program = AlmostVirtualMachine()
            for (i in 0 until recyclerviewAdapter.blockList.size) {
                val item = recyclerviewAdapter.blockList[i]
                when (item.type) {
                    VAR -> {
                        val varName = item.getNameEditText()
                        val varValue = item.getValueEditText()
                        program.pushInstr("int", varName ?: "")
                        if (varValue != "") {
                            program.pushInstr("=", varName ?: "", varValue ?: "")
                        }
                    }
                    ASSIGNMENT -> {
                        val varName = item.getNameEditText()
                        val expression = item.getValueEditText()
                        program.pushInstr("=", varName ?: "", expression ?: "")
                    }
                    IN -> {
                        val varName = item.getNameEditText()
                        program.pushInstr("in", varName ?: "")
                    }
                    OUT -> {
                        val varName = item.getNameEditText()
                        program.pushInstr("out", "", varName ?: "")
                    }
                    IF -> {
                        val firstExpression = item.getNameEditText()
                        val secondExpression = item.getValueEditText()
                        val comparison = item.comparison
                        program.pushInstr(
                            "if",
                            "",
                            "$firstExpression $comparison $secondExpression"
                        )
                    }
                    ELSE -> {
                        program.pushInstr("else")
                    }
                    BLOCK_END -> {
                        program.pushInstr("end")
                    }
                    WHILE -> {
                        val expression = item.getNameEditText()
                        program.pushInstr("while", "", expression ?: "")
                    }
                    ARRAY -> {
                        val arrayName = item.getNameEditText()
                        val arraySize = item.getValueEditText()
                        program.pushInstr("arr", arrayName ?: "", arraySize ?: "")
                    }
                    INCREMENT -> {
                        val varName = item.getNameEditText()
                        program.pushInstr("=", varName ?: "", "$varName + 1")
                    }
                    DECREMENT -> {
                        val varName = item.getNameEditText()
                        program.pushInstr("=", varName ?: "", "$varName - 1")
                    }
                }
            }

            program.doLog = true
            program.infiniteLoopStop = 1000000
            output.text = null

            fun tryExecute() {
                try {
                    program.execute()
                } catch (e: Throwable) {
                    program.output += e
                }
            }

            buttonResume.setOnClickListener {
                program.output = input.text.toString() + "\n"
                program.input = input.text.toString()
                tryExecute()
                expectingInput = false
                updateConsole(program)
            }


            expectingInput = if (program.findInstruction("in") == null) {
                tryExecute()
                false
            } else {
                true
            }
            updateConsole(program)
        }

        buttonRun.setOnClickListener {
            input.text = null
            run()
        }

        buttonRestart.setOnClickListener {
            run()
        }

        consoleClose.setOnClickListener {
            console.visibility = View.GONE
            consoleClose.visibility = View.GONE
            buttonResume.visibility = View.GONE
            buttonRestart.visibility = View.GONE
            binding.buttonAdd.visibility = View.VISIBLE
        }

        consoleOpen.setOnClickListener {
            updateConsole(null)
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun init() {
        binding.buttonAdd.addActionItem(
            SpeedDialActionItem.Builder(R.id.creating_variable, R.drawable.ic_add_var_24)
                .setFabBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.var_back,
                        theme
                    )
                )
                .setFabImageTintColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.white,
                        theme
                    )
                )
                .setLabel(R.string.creating_variable)
                .setLabelColor(R.color.label_color)
                .setLabelBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.label_back,
                        theme
                    )
                )
                .setLabelClickable(false)
                .create()
        )
        binding.buttonAdd.addActionItem(
            SpeedDialActionItem.Builder(R.id.assignment, R.drawable.ic_assignment_24dp)
                .setFabBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.assignment_back,
                        theme
                    )
                )
                .setFabImageTintColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.white,
                        theme
                    )
                )
                .setLabel(R.string.assignment_item)
                .setLabelColor(R.color.label_color)
                .setLabelBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.label_back,
                        theme
                    )
                )
                .setLabelClickable(false)
                .create()
        )
        binding.buttonAdd.addActionItem(
            SpeedDialActionItem.Builder(R.id.creating_in, R.drawable.ic_add_in)
                .setFabBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.in_back,
                        theme
                    )
                )
                .setFabImageTintColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.white,
                        theme
                    )
                )
                .setLabel(R.string.input)
                .setLabelColor(R.color.label_color)
                .setLabelBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.label_back,
                        theme
                    )
                )
                .setLabelClickable(false)
                .create()
        )
        binding.buttonAdd.addActionItem(
            SpeedDialActionItem.Builder(R.id.creating_out, R.drawable.ic_add_out)
                .setFabBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.out_back,
                        theme
                    )
                )
                .setFabImageTintColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.white,
                        theme
                    )
                )
                .setLabel(R.string.output)
                .setLabelColor(R.color.label_color)
                .setLabelBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.label_back,
                        theme
                    )
                )
                .setLabelClickable(false)
                .create()
        )
        binding.buttonAdd.addActionItem(
            SpeedDialActionItem.Builder(R.id.creating_if, R.drawable.ic_add_if)
                .setFabBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.if_else_back,
                        theme
                    )
                )
                .setFabImageTintColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.white,
                        theme
                    )
                )
                .setLabel(R.string.condition_item)
                .setLabelColor(R.color.label_color)
                .setLabelBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.label_back,
                        theme
                    )
                )
                .setLabelClickable(false)
                .create()
        )
        binding.buttonAdd.addActionItem(
            SpeedDialActionItem.Builder(R.id.creating_while, R.drawable.ic_add_while)
                .setFabBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.while_back,
                        theme
                    )
                )
                .setFabImageTintColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.white,
                        theme
                    )
                )
                .setLabel(R.string.cycle)
                .setLabelColor(R.color.label_color)
                .setLabelBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.label_back,
                        theme
                    )
                )
                .setLabelClickable(false)
                .create()
        )
        binding.buttonAdd.addActionItem(
            SpeedDialActionItem.Builder(R.id.creating_array, R.drawable.ic_add_array)
                .setFabBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.array_back,
                        theme
                    )
                )
                .setFabImageTintColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.white,
                        theme
                    )
                )
                .setLabel(R.string.array_item)
                .setLabelColor(R.color.label_color)
                .setLabelBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.label_back,
                        theme
                    )
                )
                .setLabelClickable(false)
                .create()
        )
        binding.buttonAdd.addActionItem(
            SpeedDialActionItem.Builder(R.id.creating_increment, R.drawable.ic_add_increment)
                .setFabBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.increment_back,
                        theme
                    )
                )
                .setFabImageTintColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.white,
                        theme
                    )
                )
                .setLabel(R.string.increment)
                .setLabelColor(R.color.label_color)
                .setLabelBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.label_back,
                        theme
                    )
                )
                .setLabelClickable(false)
                .create()
        )

        binding.buttonAdd.addActionItem(
            SpeedDialActionItem.Builder(R.id.creating_decrement, R.drawable.ic_add_decrement)
                .setFabBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.decrement_back,
                        theme
                    )
                )
                .setFabImageTintColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.white,
                        theme
                    )
                )
                .setLabel(R.string.decrement)
                .setLabelColor(R.color.label_color)
                .setLabelBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.label_back,
                        theme
                    )
                )
                .setLabelClickable(false)
                .create()
        )
        binding.buttonAdd.addActionItem(
            SpeedDialActionItem.Builder(R.id.creating_end, R.drawable.ic_add_end)
                .setFabBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.end_back,
                        theme
                    )
                )
                .setFabImageTintColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.white,
                        theme
                    )
                )
                .setLabel(R.string.end_block)
                .setLabelColor(R.color.label_color)
                .setLabelBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.label_back,
                        theme
                    )
                )
                .setLabelClickable(false)
                .create()
        )
        binding.buttonAdd.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.creating_variable -> {
                    recyclerviewAdapter.addBlock(VAR, "")
                }
                R.id.assignment -> {
                    recyclerviewAdapter.addBlock(ASSIGNMENT, "")
                }
                R.id.creating_in -> {
                    recyclerviewAdapter.addBlock(IN, "")
                }
                R.id.creating_out -> {
                    recyclerviewAdapter.addBlock(OUT, "")
                }
                R.id.creating_if -> {
                    val myDialog = LayoutInflater.from(this).inflate(R.layout.if_dialog, null)
                    val myBuilder = AlertDialog.Builder(this)
                        .setView(myDialog)
                        .setTitle(R.string.details)
                    val myAlertDialog = myBuilder.show()
                    myDialog.findViewById<Button>(R.id.buttonCreate).setOnClickListener {
                        val comparison =
                            myDialog.findViewById<Spinner>(R.id.comparisons).selectedItem.toString()
                        val checkElse = myDialog.findViewById<CheckBox>(R.id.checkElse)
                        if (checkElse.isChecked) {
                            recyclerviewAdapter.addBlock(IF, comparison)
                            recyclerviewAdapter.addBlock(ELSE, "")
                            recyclerviewAdapter.addBlock(BLOCK_END, "")
                        } else {
                            recyclerviewAdapter.addBlock(IF, comparison)
                            recyclerviewAdapter.addBlock(BLOCK_END, "")
                        }
                        myAlertDialog.dismiss()

                    }
                    myDialog.findViewById<Button>(R.id.buttonClose).setOnClickListener {
                        myAlertDialog.dismiss()
                    }

                }
                R.id.creating_while -> {
                    recyclerviewAdapter.addBlock(WHILE, "")
                    recyclerviewAdapter.addBlock(BLOCK_END, "")
                }
                R.id.creating_array -> {
                    recyclerviewAdapter.addBlock(ARRAY, "")
                }
                R.id.creating_increment -> {
                    recyclerviewAdapter.addBlock(INCREMENT, "")
                }
                R.id.creating_decrement -> {
                    recyclerviewAdapter.addBlock(DECREMENT, "")
                }
                R.id.creating_end -> {
                    recyclerviewAdapter.addBlock(BLOCK_END, "")
                }
            }
            false
        }

    }
}