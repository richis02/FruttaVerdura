package com.example.prova_progetto

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import com.example.prova_progetto.db.ItemsList


class AllListActivity: ComponentActivity() {


    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_list)

        val listTitleTv: EditText = findViewById(R.id.fruit_list_name)

        val addList: ImageView = findViewById(R.id.add_list)
        addList.setOnClickListener {
            if(listTitleTv.text.toString() != "") {
                val newList = ItemsList(listTitle = listTitleTv.text.toString())
                fruitVegViewModel.insertList(newList)
            }
        }


        val recyclerView: RecyclerView = findViewById(R.id.recycler_list)
        val adapter = ItemsListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //it è una convenzione di Kotlin per il nome di variabile implicito quando
        // si usa una lambda con un singolo parametro. È usato per brevità e leggibilità.

        fruitVegViewModel.allList.observe(this, Observer { lists ->
            // Aggiornamento copia cached
            lists?.let { adapter.submitList(it) }
        })

    }

    //TODO: IMPLEMENTARE AGGIUNTA LISTA VEDI CODELAB
}


//TODO    private val applicationScope = CoroutineScope(Dispatchers.Default)

//TODO        val database = FruitListRoomDatabase.getDatabase(this, applicationScope)

//TODO        applicationScope.launch(Dispatchers.IO) {
//           FruitListRoomDatabase.populateDatabaseFromCSV(this@AllListActivity, database.fruitVegDao())
//        }
