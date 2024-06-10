package com.example.prova_progetto.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.OnItemsListClickListener
import com.example.prova_progetto.R
import com.example.prova_progetto.db.ItemsList

class ItemsListAdapter(private val listener: OnItemsListClickListener)
    : ListAdapter<ItemsList, ItemsListAdapter.ItemListViewHolder>(
    ITEMSLISTS_COMPARATOR
) {

    private val selectedItems: MutableSet<Long> = mutableSetOf()
    private lateinit var editName: ImageView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        return ItemListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            listener.onItemClick(current.itemsListId, current.listTitle)
        }

        editName = holder.itemView.findViewById(R.id.modify_list_name)
        editName.setOnClickListener {
            listener.onUpdateTitleClick(current.itemsListId)
        }

        holder.bind(current, selectedItems.contains(current.itemsListId))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSelectedItems(selectedIds: List<Long>) {
        selectedItems.clear()
        selectedItems.addAll(selectedIds)
        notifyDataSetChanged()
    }

    class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItemView: TextView = itemView.findViewById(R.id.list_text_view)
        private val layout: LinearLayout = itemView.findViewById(R.id.list_item)

        fun bind(itemList: ItemsList?, isSelected: Boolean) {
            itemList?.let{
                listItemView.text = itemList.listTitle
            }

            if (isSelected) {
                layout.setBackgroundResource(R.drawable.red_border) // Rimuove il background
            } else {
                layout.setBackgroundResource(R.drawable.main_green_border)
            }

        }

        companion object {
            fun create(parent: ViewGroup): ItemListViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.all_list_item, parent, false)
                return ItemListViewHolder(view)
            }
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