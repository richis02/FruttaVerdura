package com.example.prova_progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.db.FruitListRoomDatabase
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import com.example.prova_progetto.db.FruitVegetableDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AllListActivity: ComponentActivity() {

//TODO    private val applicationScope = CoroutineScope(Dispatchers.Default)

    private val newListActivityRequestCode = 1

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_list)

//TODO        val database = FruitListRoomDatabase.getDatabase(this, applicationScope)

//TODO        applicationScope.launch(Dispatchers.IO) {
//           FruitListRoomDatabase.populateDatabaseFromCSV(this@AllListActivity, database.fruitVegDao())
//        }


        val recyclerView: RecyclerView = findViewById(R.id.lists_recycler_view)
        val adapter = ItemsListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        fruitVegViewModel.allList.observe(this, Observer { lists ->
            // Aggiornamento copia cached
            lists?.let { adapter.submitList(it) }
        })

    }

    //TODO: IMPLEMENTARE AGGIUNTA LISTA VEDI CODELAB
}