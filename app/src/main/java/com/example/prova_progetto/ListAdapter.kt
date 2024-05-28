package com.example.prova_progetto

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListAdapter (private val fruitList: Array<String>) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>(){
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val list: TextView = itemView.findViewById(R.id.list_item)

        fun bind(word: String) {
            list.text = word
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_layout, parent, false)

        val tv : TextView = view.findViewById(R.id.list_item)
        tv.setOnClickListener(onClickListener)

        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fruitList.size
    }

    private val onClickListener = View.OnClickListener { v->
        //val myIntent = Intent(v.context, DetailActivity::class.java)
        //myIntent.putExtra("flower", v.findViewById<TextView>(R.id.flower_text).text.toString())
        //v.context.startActivity(myIntent)
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(fruitList[position])
    }
}