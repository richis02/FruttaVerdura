package com.example.prova_progetto.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.Activity.AllFruitVegActivity
import com.example.prova_progetto.R
import com.example.prova_progetto.db.ItemsList

class ItemsListAdapter : ListAdapter<ItemsList, ItemsListAdapter.ItemListViewHolder>(
    ITEMSLISTS_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {

        //val view = LayoutInflater.from(parent.context)
        //    .inflate(R.layout.list_layout, parent, false)

        //val tv : TextView = view.findViewById(R.id.list_item)
        //tv.setOnClickListener(onClickListener)

        return ItemListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }


    // Binda un testo con una textview
    class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItemView: TextView = itemView.findViewById(R.id.list_item)


        fun bind(itemList: ItemsList?) {
            // Con let si gestisce il caso itemList = null
            itemList?.let{
                listItemView.text = itemList.listTitle
                listItemView.setOnClickListener {v ->
                    val intent = Intent(v.context, AllFruitVegActivity::class.java)
                    intent.putExtra(LIST_KEY, itemList.itemsListId)

                    v.context.startActivity(intent)
                }
            }

        }

        companion object {
            fun create(parent: ViewGroup): ItemListViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_layout, parent, false)
                return ItemListViewHolder(view)
            }

            const val LIST_KEY = "list_key"
        }
    }

    companion object {
        // Come controllare se due titoli sono uguali o se due contents sono uguali
        private val ITEMSLISTS_COMPARATOR = object : DiffUtil.ItemCallback<ItemsList>() {
            override fun areItemsTheSame(oldItem: ItemsList, newItem: ItemsList): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ItemsList, newItem: ItemsList): Boolean {
                return oldItem.itemsListId == newItem.itemsListId //TODO: QUESTO SARA' DA MODIFICARE MI SA
            }
        }
    }

}