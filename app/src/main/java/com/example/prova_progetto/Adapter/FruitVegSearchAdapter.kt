package com.example.prova_progetto.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.OnFruitVegClickListener
import com.example.prova_progetto.R
import com.example.prova_progetto.db.FruitVegetable


class FruitVegSearchAdapter (private val listener: OnFruitVegClickListener) : ListAdapter<FruitVegetable, FruitVegSearchAdapter.ItemListViewHolder>(
    ITEMSLISTS_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        return ItemListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener { listener.onItemClick(current.fruitVegName, null, current.img) }
        holder.bind(current)
    }

    class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItemView: TextView = itemView.findViewById(R.id.list_text_view)
        private val imgView: ImageView = itemView.findViewById(R.id.item_image)

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

        fun bind(fruitVeg: FruitVegetable?) {
            // Con let si gestisce il caso di fruitVeg = null
            fruitVeg?.let {
                val img = it.img
                listItemView.text = fruitVeg.fruitVegName
                val imgResId = imageMap[img] ?: R.drawable.icon

                imgView.setImageResource(imgResId)
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
        private val ITEMSLISTS_COMPARATOR = object : DiffUtil.ItemCallback<FruitVegetable>() {
            override fun areItemsTheSame(oldItem: FruitVegetable, newItem: FruitVegetable): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: FruitVegetable, newItem: FruitVegetable): Boolean {
                return oldItem.fruitVegName == newItem.fruitVegName
            }
        }
    }
}
