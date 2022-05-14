package com.example.module7.Model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.module7.databinding.AdapterItemBinding
import java.util.*


class RecyclerviewAdapter : RecyclerView.Adapter<RecyclerviewAdapter.ItemViewHolder>() {
    var arrayItems: ArrayList<Block> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterItemBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val movieItem = arrayItems[position]
        holder.bindView(movieItem)
    }

    override fun getItemCount(): Int {
        return arrayItems.size
    }


    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(arrayItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

    }

    fun onItemDismiss(position: Int) {
        arrayItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, arrayItems.size)

    }

    fun addBlocks(value: Int) {
        arrayItems.add(Block(value))
        notifyItemChanged(itemCount - 1)
    }


    inner class ItemViewHolder(private val binding: AdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: Block) {
            binding.apply {
                blockNumber.text = item.number.toString()

            }
        }
    }
}
