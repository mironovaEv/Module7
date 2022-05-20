package com.example.module7.Model

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.module7.R
import java.util.*
import kotlin.collections.ArrayList


class RecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_VAR = 0
        private const val TYPE_ASSIGNMENT = 1
        private const val TYPE_IN = 2
        private const val TYPE_OUT = 3
        private const val TYPE_IF = 4
        private const val TYPE_END = 5
        private const val TYPE_ELSE = 6
        private const val TYPE_WHILE = 7
        private const val TYPE_ARRAY = 8
        private const val TYPE_INCREMENT = 9
        private const val TYPE_DECREMENT = 10


    }

    var blockList = ArrayList<Block>()
    class ViewHolderNull(itemView: View, blockList: ArrayList<Block>, viewType: Int) :
        RecyclerView.ViewHolder(itemView) {
            init {

            }
        }

    class ViewHolderOne(itemView: View, blockList: ArrayList<Block>, viewType: Int) :
        RecyclerView.ViewHolder(itemView) {
        var varName: EditText

        init {
            varName = when (viewType) {
                TYPE_IN -> {
                    itemView.findViewById(R.id.varNameIn) as EditText
                }
                TYPE_OUT -> {
                    itemView.findViewById(R.id.varNameOut) as EditText
                }
                TYPE_WHILE -> {
                    itemView.findViewById(R.id.whileExpression) as EditText
                }
                TYPE_INCREMENT -> {
                    itemView.findViewById(R.id.varNameIncrement) as EditText
                }
                TYPE_DECREMENT -> {
                    itemView.findViewById(R.id.varNameDecrement) as EditText
                }
                else -> throw IllegalArgumentException()

            }

            if (blockList.size != 0) {
                varName.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                    ) {
                    }

                    override fun onTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                    ) {
                        blockList[adapterPosition].setNameEditText(varName.text.toString())
                    }

                    override fun afterTextChanged(editable: Editable) {}
                })

            }
        }

    }


    class ViewHolderTwo(itemView: View, blockList: ArrayList<Block>, viewType: Int) :
        RecyclerView.ViewHolder(itemView) {
        var varName: EditText
        var varValue: EditText


        init {
            when (viewType) {
                TYPE_VAR -> {
                    varName = itemView.findViewById(R.id.varName) as EditText
                    varValue = itemView.findViewById(R.id.varValue) as EditText
                }
                TYPE_ASSIGNMENT -> {
                    varName = itemView.findViewById(R.id.varNameAss) as EditText
                    varValue = itemView.findViewById(R.id.varValueAss) as EditText
                }
                TYPE_IF -> {
                    varName = itemView.findViewById(R.id.firstExpression) as EditText
                    varValue = itemView.findViewById(R.id.secondExpression) as EditText

                }
                TYPE_ARRAY -> {
                    varName = itemView.findViewById(R.id.arrayName) as EditText
                    varValue = itemView.findViewById(R.id.arraySize) as EditText

                }

                else -> throw IllegalArgumentException()

            }

            if (blockList.size != 0) {
                varName.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                    ) {
                    }

                    override fun onTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                    ) {
                        blockList[adapterPosition].setNameEditText(varName.text.toString())
                    }

                    override fun afterTextChanged(editable: Editable) {}
                })
                varValue.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                    ) {
                    }

                    override fun onTextChanged(
                        charSequence: CharSequence, i: Int, i1: Int, i2: Int
                    ) {
                        blockList[adapterPosition].setValueEditText(varValue.text.toString())
                    }

                    override fun afterTextChanged(editable: Editable) {}
                })
            }
        }

    }



    override fun getItemCount(): Int {
        return blockList.size
    }

    override fun getItemViewType(position: Int): Int =
        when (blockList[position].type) {
            "var" -> TYPE_VAR
            "assignment" -> TYPE_ASSIGNMENT
            "in" -> TYPE_IN
            "out" -> TYPE_OUT
            "if" -> TYPE_IF
            "end" -> TYPE_END
            "else" -> TYPE_ELSE
            "while" -> TYPE_WHILE
            "array" -> TYPE_ARRAY
            "increment" -> TYPE_INCREMENT
            "decrement" -> TYPE_DECREMENT
            else -> throw IllegalArgumentException()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_VAR -> ViewHolderTwo(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.var_item, parent, false), blockList, viewType
            )
            TYPE_ASSIGNMENT -> ViewHolderTwo(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.assignment_item, parent, false), blockList, viewType
            )
            TYPE_IN -> ViewHolderOne(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.in_item, parent, false), blockList, viewType
            )
            TYPE_OUT -> ViewHolderOne(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.out_item, parent, false), blockList, viewType
            )
            TYPE_IF -> ViewHolderTwo(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.if_item, parent, false), blockList, viewType
            )
            TYPE_END ->ViewHolderNull(LayoutInflater.from(parent.context)
                .inflate(R.layout.end_item, parent, false), blockList, viewType
            )
            TYPE_ELSE ->ViewHolderNull(LayoutInflater.from(parent.context)
                .inflate(R.layout.else_item, parent, false), blockList, viewType
            )
            TYPE_WHILE ->ViewHolderOne(LayoutInflater.from(parent.context)
                .inflate(R.layout.while_item, parent, false), blockList, viewType
            )
            TYPE_ARRAY ->ViewHolderTwo(LayoutInflater.from(parent.context)
                .inflate(R.layout.array_item, parent, false), blockList, viewType
            )
            TYPE_INCREMENT ->ViewHolderOne(LayoutInflater.from(parent.context)
                .inflate(R.layout.increment_item, parent, false), blockList, viewType
            )
            TYPE_DECREMENT ->ViewHolderOne(LayoutInflater.from(parent.context)
                .inflate(R.layout.decrement_item, parent, false), blockList, viewType
            )
            else -> throw  IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (blockList[position].type){
            "var", "assignment", "array" -> {
                val myHolder = holder as ViewHolderTwo
                myHolder.varName.setText(blockList[position].getNameEditText())
                myHolder.varValue.setText(blockList[position].getValueEditText())
            }
            "in", "out", "while", "increment", "decrement" -> {
                val myHolder = holder as ViewHolderOne
                myHolder.varName.setText(blockList[position].getNameEditText())
            }
            "if" -> {
                val myHolder = holder as ViewHolderTwo
                myHolder.varName.setText(blockList[position].getNameEditText())
                myHolder.varValue.setText(blockList[position].getValueEditText())
                val item = holder.itemView.findViewById<TextView>(R.id.ifOperand)
                item.text = blockList[position].comparison

            }
            "end", "else" ->{

            }
            else -> throw  IllegalStateException()
        }


    }


    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(blockList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

    }

    fun onItemDismiss(position: Int) {
        blockList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, blockList.size)

    }

    fun addBlock(type: String, comparison: String) {
        blockList.add(Block(type, comparison))
        notifyItemChanged(itemCount - 1)
    }


}
