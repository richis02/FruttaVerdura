package com.example.prova_progetto.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.OnItemsListClickListener
import com.example.prova_progetto.R
import com.example.prova_progetto.db.ItemsList

class ItemsListAdapter(private val listener: OnItemsListClickListener) : ListAdapter<ItemsList, ItemsListAdapter.ItemListViewHolder>(
    ITEMSLISTS_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        return ItemListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            listener.onItemClick(current.itemsListId)
        }
        holder.bind(current)
    }


    // Binda un testo con una textview
    class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItemView: TextView = itemView.findViewById(R.id.list_text_view)

        fun bind(itemList: ItemsList?) {
            // Con let si gestisce il caso itemList = null
            itemList?.let{
                listItemView.text = itemList.listTitle
            }

        }

        companion object {
            fun create(parent: ViewGroup): ItemListViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.all_list_item, parent, false)
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