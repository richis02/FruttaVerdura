package com.example.prova_progetto

import FruitSearchAdapter
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory

class SearchActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FruitSearchAdapter

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchView: SearchView = findViewById(R.id.search)
        recyclerView = findViewById(R.id.recycler_search)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FruitSearchAdapter()
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
                fruitVegViewModel.getFilteredFruitVeg(newText ?: "")
                return true
            }
        })
    }
}
