package com.example.prova_progetto.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.Activity.AllListActivity
import com.example.prova_progetto.Activity.FruitDetailsActivity
import com.example.prova_progetto.OnItemClickListener
import com.example.prova_progetto.R
import com.example.prova_progetto.db.FruitVegetable

class FruitVegSearchAdapter (private val listener: OnItemClickListener) : ListAdapter<FruitVegetable, FruitVegSearchAdapter.ItemListViewHolder>(
    ITEMSLISTS_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        return ItemListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener { listener.onItemClick(current.fruitVegId) }
        holder.bind(current)
    }

    class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItemView: TextView = itemView.findViewById(R.id.list_text_view)
        //todo: da fare la grafica e recuperare ConstraintLayout

        fun bind(fruitVeg: FruitVegetable?) {
            // Con let si gestisce il caso di fruitVeg = null
            fruitVeg?.let {
                listItemView.text = fruitVeg.fruitVegId
            }
        }

        companion object {
            fun create(parent: ViewGroup): ItemListViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.search_layout, parent, false)
                return ItemListViewHolder(view)
            }

            const val FRUIT_KEY = "fruit_key"
        }
    }

    companion object {
        private val ITEMSLISTS_COMPARATOR = object : DiffUtil.ItemCallback<FruitVegetable>() {
            override fun areItemsTheSame(oldItem: FruitVegetable, newItem: FruitVegetable): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: FruitVegetable, newItem: FruitVegetable): Boolean {
                return oldItem.fruitVegId == newItem.fruitVegId
            }
        }
    }
}
