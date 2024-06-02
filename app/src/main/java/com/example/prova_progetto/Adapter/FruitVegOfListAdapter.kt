package com.example.prova_progetto.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.Activity.FruitDetailsActivity
import com.example.prova_progetto.R
import com.example.prova_progetto.db.FruitVegInfo

class FruitVegOfListAdapter : ListAdapter<FruitVegInfo, FruitVegOfListAdapter.ItemListViewHolder>(
    ITEMSLISTS_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        return ItemListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItemView: TextView = itemView.findViewById(R.id.list_text_view)
        private val fruitOfListItem: LinearLayout = itemView.findViewById(R.id.list_item)
        private val quantityView: TextView = itemView.findViewById(R.id.quantity)

        fun bind(fruitVeg: FruitVegInfo?) {
            // Con let si gestisce il caso di fruitVeg = null
            fruitVeg?.let {
                listItemView.text = fruitVeg.fruitVeg.fruitVegId
                quantityView.text = fruitVeg.quantity.toString()
                //TODO QUANTITA
                fruitOfListItem.setOnClickListener {   v ->
                    val intent = Intent(v.context, FruitDetailsActivity::class.java)
                    intent.putExtra(FRUIT_KEY, fruitVeg.fruitVeg.fruitVegId)
                    v.context.startActivity(intent)
                }

            }
        }

        companion object {
            fun create(parent: ViewGroup): ItemListViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fruit_veg_of_list_item, parent, false)
                return ItemListViewHolder(view)
            }

            const val FRUIT_KEY = "fruit_key"
        }
    }

    companion object {
        private val ITEMSLISTS_COMPARATOR = object : DiffUtil.ItemCallback<FruitVegInfo>() {
            override fun areItemsTheSame(oldItem: FruitVegInfo, newItem: FruitVegInfo): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: FruitVegInfo, newItem: FruitVegInfo): Boolean {
                //TODO: testare come fare ad aggiornare in caso di cambio della quantità
                return oldItem.fruitVeg.fruitVegId == newItem.fruitVeg.fruitVegId
            }
        }
    }
}
