package com.example.prova_progetto.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.Adapter.ItemsListAdapter
import com.example.prova_progetto.OnItemsListClickListener
import com.example.prova_progetto.R
import com.example.prova_progetto.db.FruitListRoomDatabase
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import com.example.prova_progetto.db.FruitVegetable
import com.example.prova_progetto.db.ItemsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AllListActivity: ComponentActivity(), OnItemsListClickListener {

    private var isDeleting: Boolean = false
    private val indexesToDelete: MutableList<Long> = mutableListOf()

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_list)

        val back: ImageView = findViewById(R.id.back_arrow)
        back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val listTitleTv: EditText = findViewById(R.id.fruit_list_name)
        val deleteBtn: Button = findViewById(R.id.delete_btn)
        val annullaBtn: Button = findViewById(R.id.annulla_btn)
        deleteBtn.setOnClickListener{
            if(!isDeleting){
                isDeleting = true
                deleteBtn.text = "Conferma"
                annullaBtn.visibility = View.VISIBLE

            } else {
                isDeleting = false
                deleteBtn.text = "Elimina"
                for (index in indexesToDelete) {
                    fruitVegViewModel.deleteItemList(index)
                }
                annullaBtn.visibility = View.GONE
            }
        }

        annullaBtn.setOnClickListener {
            annullaBtn.visibility = View.GONE
            deleteBtn.text = "Elimina"
            indexesToDelete.clear()
            isDeleting = false
        }

        val addList: ImageView = findViewById(R.id.add_list)
        addList.setOnClickListener {
            if(listTitleTv.text.toString() != "") {
                val newList = ItemsList(listTitle = listTitleTv.text.toString())
                fruitVegViewModel.insertList(newList)
                listTitleTv.text = null //tolgo il testo
            }
        }


        val recyclerView: RecyclerView = findViewById(R.id.recycler_list)
        val adapter = ItemsListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //it è una convenzione di Kotlin per il nome di variabile implicito quando
        // si usa una lambda con un singolo parametro. È usato per brevità e leggibilità.

        fruitVegViewModel.allList.observe(this, Observer { lists ->
            // Aggiornamento copia cached
            lists?.let {
                adapter.submitList(it)
            }
        })

    }

    override fun onItemClick(id: Long, name: String) {
        if(!isDeleting) {
            val intent = Intent(this, AllFruitVegOfListActivity::class.java)
            intent.putExtra(LIST_KEY, id)
            intent.putExtra(LIST_NAME, name)
            startActivity(intent)
        } else
        {
            val index = indexesToDelete.indexOf(id)
            if(index != -1) {
                indexesToDelete.removeAt(index)
            }
            else {
                indexesToDelete.add(id)
            }
        }
    }

    companion object{
        const val LIST_KEY = "list_key"
        const val LIST_NAME = "list_name"
    }
    //TODO: IMPLEMENTARE AGGIUNTA LISTA VEDI CODELAB
}


//TODO    private val applicationScope = CoroutineScope(Dispatchers.Default)

//TODO        val database = FruitListRoomDatabase.getDatabase(this, applicationScope)

//TODO        applicationScope.launch(Dispatchers.IO) {
//           FruitListRoomDatabase.populateDatabaseFromCSV(this@AllListActivity, database.fruitVegDao())
//
//        }
