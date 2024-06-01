package com.example.prova_progetto

import FruitVegOfListAdapter
import FruitVegSearchAdapter
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FruitListActivity : ComponentActivity(){

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }
    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fruit_list)

        // TODO: non dovrebbe mai succedere che l'id non esiste quindi riscrivere riga sotto correttamente
        val listId: Long? = intent.getLongExtra("list_key", -1L).takeIf { it != -1L }

        val tv: TextView = findViewById(R.id.tv)

        if (listId != null) {
            tv.text = "Lista id: $listId"
        } else {
            tv.text = "Lista id non disponibile"
        }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_fruit_of_list)
        val adapter = FruitVegOfListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)



//TODO --> finire ...
        listId?.let {
            fruitVegViewModel.getAllFruitsVegOfList(listId).observe(this, Observer {fruits ->
                fruits?.let{
                    adapter.submitList(it)
                }
            })
        }

        //TODO: COSI è IL MODO CORRETTO DI SCRIVERE STRINGHE STATICHE

    }
}

//        if (listId != null) {
//            tv.text = getString(R.string.list_id_text, listId)
//        } else {
//            tv.text = getString(R.string.list_id_unavailable)
//        }