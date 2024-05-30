package com.example.prova_progetto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class FruitSearchAdapter(private var fullList: List<String>) : RecyclerView.Adapter<FruitSearchAdapter.FruitViewHolder>() {

    private var filteredList = ArrayList(fullList)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FruitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_layout, parent, false)
        return FruitViewHolder(view)
    }

    override fun onBindViewHolder(holder: FruitViewHolder, position: Int) {
        holder.textView.text = filteredList[position]
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun filter(text: String) {
        filteredList.clear()
        if (text.isEmpty()) {
            filteredList.addAll(fullList)
        } else {
            val searchText = text.toLowerCase(Locale.getDefault())
            for (item in fullList) {
                if (item.toLowerCase(Locale.getDefault()).contains(searchText)) {
                    filteredList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    class FruitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.search_result)
    }
}
