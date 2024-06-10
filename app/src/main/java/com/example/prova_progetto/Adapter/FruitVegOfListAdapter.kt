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
import com.example.prova_progetto.OnFruitVegClickListener
import com.example.prova_progetto.R
import com.example.prova_progetto.db.FruitVegInfo

class FruitVegOfListAdapter(private val listener: OnFruitVegClickListener) : ListAdapter<FruitVegInfo, FruitVegOfListAdapter.ItemListViewHolder>(
    ITEMSLISTS_COMPARATOR
) {

    private val selectedItems: MutableSet<String> = mutableSetOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        return ItemListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            listener.onItemClick(current.fruitVeg.fruitVegName, current.quantity, current.fruitVeg.img)
        }
        holder.bind(current, selectedItems.contains(current.fruitVeg.fruitVegName))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSelectedItems(selectedIds: List<String>) {
        selectedItems.clear()
        selectedItems.addAll(selectedIds)
        notifyDataSetChanged()
    }


    class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItemView: TextView = itemView.findViewById(R.id.list_text_view)
        private val quantityView: TextView = itemView.findViewById(R.id.quantity)
        private val imgView: ImageView = itemView.findViewById(R.id.item_image)
        private val layout: LinearLayout = itemView.findViewById(R.id.list_item)

        // Per una questione di efficenza è stata definita una mappa, questo è possibile perchè
        // la dimensione del dataset è ridotta
        private val imageMap = mapOf(
            "garlic" to R.drawable.garlic,
            "pineapple" to R.drawable.pineapple,
            "watermelon" to R.drawable.watermelon,
            "orange" to R.drawable.orange,
            "banana" to R.drawable.banana,
            "beetroot" to R.drawable.beetroot,
            "capsicum" to R.drawable.capsicum,
            "carrots" to R.drawable.carrots,
            "cauliflower" to R.drawable.cauliflower,
            "cabbage" to R.drawable.cabbage,
            "cucumber" to R.drawable.cucumber,
            "onion" to R.drawable.onion,
            "beans" to R.drawable.beans,
            "jalapeno" to R.drawable.jalapeno,
            "kiwi" to R.drawable.kiwi,
            "lettuce" to R.drawable.lettuce,
            "lemon" to R.drawable.lemon,
            "corn" to R.drawable.corn,
            "mango" to R.drawable.mango,
            "apple" to R.drawable.apple,
            "eggplant" to R.drawable.eggplant,
            "pomegranate" to R.drawable.pomegranate,
            "paprika" to R.drawable.paprika,
            "sweet_potato" to R.drawable.sweet_potato,
            "potato" to R.drawable.potato,
            "chili" to R.drawable.chili,
            "bell_pepper" to R.drawable.bell_pepper,
            "pear" to R.drawable.pear,
            "peas" to R.drawable.peas,
            "tomato" to R.drawable.tomato,
            "raddish" to R.drawable.radish,
            "turnip" to R.drawable.turnip,
            "spinach" to R.drawable.spinach,
            "grapes" to R.drawable.grapes,
            "ginger" to R.drawable.ginger,
            "sausage" to R.drawable.sausage
        )

        fun bind(fruitVeg: FruitVegInfo?, isSelected: Boolean) {
            // Con let si gestisce il caso di fruitVeg = null
            fruitVeg?.let {
                val img: String = it.fruitVeg.img
                listItemView.text = fruitVeg.fruitVeg.fruitVegName
                quantityView.text = fruitVeg.quantity.toString()
                val imgResId = imageMap[img] ?: R.drawable.icon

                imgView.setImageResource(imgResId)
                if (isSelected) {
                    layout.setBackgroundResource(R.drawable.red_border) // Rimuove il background
                } else {
                    layout.setBackgroundResource(R.drawable.main_green_border)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): ItemListViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fruit_veg_of_list_item, parent, false)
                return ItemListViewHolder(view)
            }
        }
    }

    companion object {
        private val ITEMSLISTS_COMPARATOR = object : DiffUtil.ItemCallback<FruitVegInfo>() {
            override fun areItemsTheSame(oldItem: FruitVegInfo, newItem: FruitVegInfo): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: FruitVegInfo, newItem: FruitVegInfo): Boolean {
                //TODO: testare come fare ad aggiornare in caso di cambio della quantità
                return oldItem.fruitVeg.fruitVegName == newItem.fruitVeg.fruitVegName
            }
        }
    }
}
