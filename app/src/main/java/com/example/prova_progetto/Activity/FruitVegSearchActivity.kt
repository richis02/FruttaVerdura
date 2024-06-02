package com.example.prova_progetto.Activity

import android.app.AlertDialog
import android.content.Intent
import com.example.prova_progetto.Adapter.FruitVegSearchAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.OnItemClickListener
import com.example.prova_progetto.R
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import com.example.prova_progetto.db.ListFruitsCrossRef
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FruitVegSearchActivity : ComponentActivity(), OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FruitVegSearchAdapter

    //TODO: CAPIRE SE VA MODIFICATO
    private var listId: Long? = null

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        listId = intent.getLongExtra("list_key", -1L).takeIf { it != -1L }

        val searchView: SearchView = findViewById(R.id.search)
        recyclerView = findViewById(R.id.recycler_search)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FruitVegSearchAdapter(this)
        recyclerView.adapter = adapter

        fruitVegViewModel.getFilteredFruitVeg("").observe(this, Observer { lists ->
            // Metodo da eseguire quando c'è un cambiamento
            lists?.let { adapter.submitList(it) }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                fruitVegViewModel.setFilterText(newText ?: "")
                return true
            }
        })
    }

    override fun onItemClick(id: String) {
        if(listId != null) {
            //siamo certi che listId sia diverso da null
            showCustomDialog(id)
        }
        else {
            val intent = Intent(this, FruitDetailsActivity::class.java)
            intent.putExtra(FruitVegSearchAdapter.ItemListViewHolder.FRUIT_KEY, id)
            this.startActivity(intent)
        }
    }

    private fun showCustomDialog(id: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)
        val tvQuantity: TextView = dialogView.findViewById(R.id.tv_quantity)
        val btnIncrease: Button = dialogView.findViewById(R.id.btn_increase)
        val btnDecrease: Button = dialogView.findViewById(R.id.btn_decrease)

        var quantity = 0

        btnIncrease.setOnClickListener {
            quantity++
            tvQuantity.text = quantity.toString()
        }

        btnDecrease.setOnClickListener {
            if (quantity > 0) {
                quantity--
                tvQuantity.text = quantity.toString()
            }
        }

        val builder = AlertDialog.Builder(this)
            .setTitle("Modifica Quantità")
            .setView(dialogView)
            .setPositiveButton("Conferma") { dialog, which ->
                val listFruitCrossRef = ListFruitsCrossRef(listId = listId!!, fruitId = id, quantity = quantity)
                // In caso di frutto già presente viene aggiornata la quantità
                fruitVegViewModel.insertFruitListCrossRef(listFruitCrossRef, quantity)
            }
            .setNegativeButton("Annulla") { dialog, which ->
                dialog.dismiss()
            }

        builder.create().show()
    }
}
