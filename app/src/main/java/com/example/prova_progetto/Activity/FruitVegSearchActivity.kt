package com.example.prova_progetto.Activity

import com.example.prova_progetto.Adapter.FruitVegSearchAdapter
import android.os.Bundle
import android.util.Log
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

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //TODO: aggiungere TV nome lista se serve
        val listId: Long? = intent.getLongExtra("list_key", -1L).takeIf { it != -1L }

        val searchView: SearchView = findViewById(R.id.search)
        recyclerView = findViewById(R.id.recycler_search)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val piuButton : FloatingActionButton = findViewById(R.id.fab_piu)
        val menoButton : FloatingActionButton = findViewById(R.id.fab_meno)
        val qttTv : TextView = findViewById(R.id.tv_quantita)



        piuButton.setOnClickListener {
            val qtt : Int = qttTv.text.toString().toInt()
            qttTv.text = (qtt + 1).toString()
        }

        menoButton.setOnClickListener {
            val qtt : Int = qttTv.text.toString().toInt()
            if(qtt != 0)
                qttTv.text = (qtt - 1).toString()
        }


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
        val listId: Long? = intent.getLongExtra("list_key", -1L).takeIf { it != -1L }
        listId?.let{
            val listFruitCrossRef = ListFruitsCrossRef(listId = it, fruitId = id)
            // In caso di frutto già presente viene aggiornata la quantità
            fruitVegViewModel.insertFruitListCrossRef(listFruitCrossRef)
            Toast.makeText(this, "Quantità aggiornata o elemento aggiunto", Toast.LENGTH_SHORT).show()
        }
    }
}
