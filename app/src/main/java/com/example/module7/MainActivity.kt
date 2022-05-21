package com.example.module7

import android.annotation.SuppressLint
import android.app.AlertDialog
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
        blockList = ArrayList()
        recyclerviewAdapter = RecyclerviewAdapter()
        binding.rcView.adapter = recyclerviewAdapter

        var expectingInput: Boolean? = null

        fun updateConsole(program: AlmostVirtualMachine?) {
            console.visibility = View.VISIBLE
            consoleClose.visibility = View.VISIBLE
            binding.buttonAdd.visibility = View.GONE
            buttonRun.visibility = View.GONE
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
                    "var" -> {
                        var varName = item.getNameEditText()
                        if (varName == null) {
                        } // Не введено имя переменной
                        var varValue = item.getValueEditText()
                        // тут имя переменной и значение при присваивании
                        program.pushInstr("int", varName?: "")
                        if (varValue != "") {
                            program.pushInstr("=", varName?: "", varValue?: "")
                        }
                    }
                    "assignment" -> {
                        val varName = item.getNameEditText()
                        if (varName == null) {
                        } // Не введено имя переменной
                        var expression = item.getValueEditText()
                        // имя объекта, которому что-то присваивается и присваиваемое значение
                        program.pushInstr("=", varName?: "", expression?: "")
                    }
                    "in" -> {
                        val varName = item.getNameEditText()
                        if (varName == null) {
                        } // Не введено имя переменной
                        //имя объекта которого вводит пользователь
                        program.pushInstr("in", varName?: "")
                    }
                    "out" -> {
                        val varName = item.getNameEditText()
                        if (varName == null) {
                        } // Не введено имя переменной
                        //имя объекта которого выводит пользователь
                        program.pushInstr("out", "",varName?: "")
                    }
                    "if" -> {
                        val firstExpression = item.getNameEditText()
                        if (firstExpression == null) {
                        } // Не введена левая часть сравнения
                        val secondExpression = item.getValueEditText()
                        if (secondExpression == null) {
                        } // Не введена правая часть сравнения
                        val comparison = item.comparison
                        //Тут обе части сравнения и оператор сравнения
                        program.pushInstr("if", "", "$firstExpression $comparison $secondExpression")
                    }
                    "else" -> {
                        //просто блок else
                        program.pushInstr("else")
                    }
                    "end" -> {
                        //блок end
                        program.pushInstr("end")
                    }
                    "while" -> {
                        val expression = item.getNameEditText()
                        if (expression == null) {
                        } // логическое выражение не введено
                        //логическое выражение
                        program.pushInstr("while", "", expression?: "")
                    }
                    "array" -> {
                        val arrayName = item.getNameEditText()
                        if (arrayName == null) {
                        } // не введено имя массива
                        val arraySize = item.getValueEditText()
                        if (arraySize == null) {
                        } // не указан размер массива
                        //имя массива и размер
                        program.pushInstr("arr", arrayName?: "", arraySize?: "")
                    }
                    "increment" -> {
                        val varName = item.getNameEditText()
                        if (varName == null) {
                        } // не введено имя увеличиваемого объекта
                        // блок для ++
                        program.pushInstr("=", varName?: "", "$varName + 1")
                    }
                    "decrement" -> {
                        val varName = item.getNameEditText()
                        if (varName == null) {
                        } // не введено имя уменьшаемого объекта
                        // блок для --
                        program.pushInstr("=", varName?: "", "$varName - 1")
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

            if (program.findInstruction("in") == null) {
                tryExecute()
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

        buttonRestart.setOnClickListener {
            run()
        }

        consoleClose.setOnClickListener {
            console.visibility = View.GONE
            consoleClose.visibility = View.GONE
            buttonResume.visibility = View.GONE
            buttonRestart.visibility = View.GONE
            binding.buttonAdd.visibility = View.VISIBLE
            buttonRun.visibility = View.VISIBLE
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
                .setLabel("Создание переменной")
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
                .setLabel("Присваивание")
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
                .setLabel("Ввод")
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
                .setLabel("Вывод")
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
                .setLabel("Условие")
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
                .setLabel("Цикл while")
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
                .setLabel("Массив")
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
                .setLabel("Инкремент")
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
                .setLabel("Декремент")
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
                .setLabel("Конец блока")
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
                    recyclerviewAdapter.addBlock("var", "")
                }
                R.id.assignment -> {
                    recyclerviewAdapter.addBlock("assignment", "")
                }
                R.id.creating_in -> {
                    recyclerviewAdapter.addBlock("in", "")
                }
                R.id.creating_out -> {
                    recyclerviewAdapter.addBlock("out", "")
                }
                R.id.creating_if -> {
                    val myDialog = LayoutInflater.from(this).inflate(R.layout.if_dialog, null)
                    val myBuilder = AlertDialog.Builder(this)
                        .setView(myDialog)
                        .setTitle("Укажите детали")
                    val myAlertDialog = myBuilder.show()
                    myDialog.findViewById<Button>(R.id.buttonCreate).setOnClickListener {
                        val comparison =
                            myDialog.findViewById<Spinner>(R.id.comparisons).selectedItem.toString()
                        val checkElse = myDialog.findViewById<CheckBox>(R.id.checkElse)
                        if (checkElse.isChecked) {
                            recyclerviewAdapter.addBlock("if", comparison)
                            recyclerviewAdapter.addBlock("end", "")
                            recyclerviewAdapter.addBlock("else", "")
                            recyclerviewAdapter.addBlock("end", "")
                        } else {
                            recyclerviewAdapter.addBlock("if", comparison)
                            recyclerviewAdapter.addBlock("end", "")
                        }
                        myAlertDialog.dismiss()

                    }
                    myDialog.findViewById<Button>(R.id.buttonClose).setOnClickListener {
                        myAlertDialog.dismiss()
                    }

                }
                R.id.creating_while -> {
                    recyclerviewAdapter.addBlock("while", "")
                    recyclerviewAdapter.addBlock("end", "")
                }
                R.id.creating_array -> {
                    recyclerviewAdapter.addBlock("array", "")
                }
                R.id.creating_increment -> {
                    recyclerviewAdapter.addBlock("increment", "")
                }
                R.id.creating_decrement -> {
                    recyclerviewAdapter.addBlock("decrement", "")
                }
            }
            false
        }

    }
}