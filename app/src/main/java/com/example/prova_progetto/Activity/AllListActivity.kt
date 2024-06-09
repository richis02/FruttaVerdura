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
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import com.example.prova_progetto.db.ItemsList

class AllListActivity: ComponentActivity(), OnItemsListClickListener {

    private lateinit var listTitleTv: EditText
    private var isDeleting: Boolean = false
    private var indexesToDelete: MutableList<Long> = mutableListOf()

    lateinit var recyclerView: RecyclerView
    lateinit var adapter : ItemsListAdapter

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

        listTitleTv = findViewById(R.id.fruit_list_name)
        val deleteBtn: Button = findViewById(R.id.delete_btn)
        val annullaBtn: Button = findViewById(R.id.annulla_btn)
        deleteBtn.setOnClickListener{
            if(!isDeleting){
                isDeleting = true
                deleteBtn.setText(R.string.conferma)
                annullaBtn.visibility = View.VISIBLE

            } else {
                isDeleting = false
                deleteBtn.setText(R.string.elimina)
                for (index in indexesToDelete) {
                    fruitVegViewModel.deleteItemList(index)
                }
                indexesToDelete.clear()
                annullaBtn.visibility = View.GONE
            }
        }

        annullaBtn.setOnClickListener {
            annullaBtn.visibility = View.GONE
            deleteBtn.setText(R.string.elimina)
            indexesToDelete.clear()
            isDeleting = false

            val adapter = (recyclerView.adapter as ItemsListAdapter)
            adapter.updateSelectedItems(indexesToDelete)
        }

        val addList: ImageView = findViewById(R.id.add_list)
        addList.setOnClickListener {
            if(listTitleTv.text.toString() != "") {
                val newList = ItemsList(listTitle = listTitleTv.text.toString())
                fruitVegViewModel.insertList(newList)
                listTitleTv.text = null //tolgo il testo
            }
        }


        recyclerView = findViewById(R.id.recycler_list)
        adapter = ItemsListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fruitVegViewModel.allList.observe(this, Observer { lists ->
            // Aggiornamento copia cached
            lists?.let {
                adapter.submitList(it)
            }
        })

        savedInstanceState?.let {
            val newListName = it.getString(NEW_LIST_NAME) ?: ""
            listTitleTv.setText(newListName)

            if(it.getBoolean(IS_DELETING)) {
                isDeleting = true
                val longArray = it.getLongArray(INDEXES_TO_DELETE)
                if (longArray != null) {
                    indexesToDelete = longArray.toMutableList()
                }
                val adapter = (recyclerView.adapter as ItemsListAdapter)
                adapter.updateSelectedItems(indexesToDelete)

                deleteBtn.setText(R.string.conferma)
                annullaBtn.visibility = View.VISIBLE
            }
        }
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

        val adapter = (recyclerView.adapter as ItemsListAdapter)
        adapter.updateSelectedItems(indexesToDelete)
    }

    companion object{
        const val LIST_KEY = "list_key"
        const val LIST_NAME = "list_name"
        const val IS_DELETING = "is_deleting"
        const val INDEXES_TO_DELETE = "indexes_to_delete"
        const val NEW_LIST_NAME = "new_list_name"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_DELETING, isDeleting)
        outState.putLongArray(INDEXES_TO_DELETE, indexesToDelete.toLongArray())
        outState.putString(NEW_LIST_NAME, listTitleTv.text.toString())
        super.onSaveInstanceState(outState)
    }
}

