package com.example.prova_progetto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory



class AllListActivity: ComponentActivity() {

    private val newListActivityRequestCode = 1

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_list)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val tempList = resources.getStringArray(R.array.flower_array) //TODO: RIMUOVERE
        val adapter = ItemsListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        fruitVegViewModel.allListTitles.observe(this, Observer { lists ->
            // Aggiornamento copia cached
            lists?.let { adapter.submitList(it) }
        })

    }

    //TODO: IMPLEMENTARE AGGIUNTA LISTA VEDI CODELAB
}