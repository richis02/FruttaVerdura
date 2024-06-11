package com.example.prova_progetto.Activity

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.Adapter.FruitVegSearchAdapter
import com.example.prova_progetto.OnFruitVegClickListener
import com.example.prova_progetto.R
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import com.example.prova_progetto.db.ListFruitsCrossRef

class FruitVegSearchActivity : ComponentActivity(), OnFruitVegClickListener {
    //variabili per la gestione del dialog che permette di aggiungere una quantità di un determinato frutto
    //le dichiaro globali per permettere il salvataggio dello stato
    private lateinit var dialog: Dialog
    private var isShowCustomDialog: Boolean = false
    private var idFruitCustomDialog: String = ""
    private var quantityCustomDialog: Int = 1
    private var imgCustomDialog : String = ""


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FruitVegSearchAdapter

    private var listId: Long? = null

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //gestione della freccia per tornare indietro
        val back: ImageView = findViewById(R.id.back_arrow)
        back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        /*prendo l'id della lista. Ci sono 2 casi:
        se id = null --> l'intent arriva dal main e quindi sul click di un frutto dovrò mostrare detailActivity
        se id > 0 --> l'intent arriva da allFruitVegOfListActivity e quindi dovrò permettere l'aggiunta di un frutto
        sulla lista id*/
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

        savedInstanceState?.let {
            if(it.getBoolean(CUSTOM_DIALOG)){
                listId = it.getLong(ID_LIST)
                quantityCustomDialog = it.getInt(QUANTITY_CUSTOM_DIALOG)
                idFruitCustomDialog = it.getString(ID_FRUIT_CUSTOM_DIALOG).toString()
                imgCustomDialog = it.getString(AllFruitVegOfListActivity.IMG_NAME) ?: ""
                showCustomDialog()
            }
        }
    }

    override fun onItemClick(id: String, quantity: Int?, icon : String) {
        //gestione dell'evento onClick
        //vedi commenti sopra per capire cosa fare se id = null o != da null
        if(listId != null) {
            idFruitCustomDialog = id
            imgCustomDialog = icon
            showCustomDialog()
        }
        else {
            val intent = Intent(this, FruitDetailsActivity::class.java)
            intent.putExtra(FruitVegSearchAdapter.ItemListViewHolder.FRUIT_KEY, id)
            this.startActivity(intent)
        }
    }

    private fun showCustomDialog() {
        isShowCustomDialog = true
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_custom)

        // Imposta icona immagine nel dialog
        val imgFruit : ImageView = dialog.findViewById(R.id.title_image)
        val imgRes = resources.getIdentifier(imgCustomDialog, "drawable", packageName)
        imgFruit.setImageResource(imgRes)
        
        val annullaButton: Button = dialog.findViewById(R.id.btn_annulla)
        annullaButton.setOnClickListener {
            isShowCustomDialog = false
            quantityCustomDialog = 1
            dialog.dismiss()
        }
        
        val confermaButton: Button = dialog.findViewById(R.id.btn_conferma)
        confermaButton.setOnClickListener {
            val listFruitCrossRef = ListFruitsCrossRef(listId = listId!!, fruitId = idFruitCustomDialog, quantity = quantityCustomDialog)
            // In caso di frutto già presente viene aggiornata la quantità
            fruitVegViewModel.insertFruitListCrossRef(listFruitCrossRef)
            Toast.makeText(this, R.string.toast_insert_fruit, Toast.LENGTH_SHORT).show()
            isShowCustomDialog = false
            quantityCustomDialog = 1
            dialog.dismiss()
        }
        
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        val tvQuantity: TextView = dialog.findViewById(R.id.tv_quantity)
        tvQuantity.text = quantityCustomDialog.toString()


        val tvMessage: TextView = dialog.findViewById(R.id.message)
        tvMessage.text = getString(R.string.message_add_selection)

        val tvMessageTitle: TextView = dialog.findViewById(R.id.title_dialog)
        tvMessageTitle.text = getString(R.string.message_add)

        val btnIncrease: Button = dialog.findViewById(R.id.btn_increase)
        btnIncrease.setOnClickListener {
            quantityCustomDialog++
            tvQuantity.text = quantityCustomDialog.toString()
        }

        val btnDecrease: Button = dialog.findViewById(R.id.btn_decrease)
        btnDecrease.setOnClickListener {
            if (quantityCustomDialog > 1) {
                quantityCustomDialog--
                tvQuantity.text = quantityCustomDialog.toString()
            }
        }

        dialog.show()
    }

    override fun onDestroy() {
        //gestione del custom dialog quando l'activity viene distrutta
        super.onDestroy()
        if(isShowCustomDialog)
            dialog.dismiss()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(CUSTOM_DIALOG, isShowCustomDialog)
        outState.putInt(QUANTITY_CUSTOM_DIALOG, quantityCustomDialog)
        outState.putString(ID_FRUIT_CUSTOM_DIALOG, idFruitCustomDialog)
        outState.putLong(ID_LIST, listId ?: -1)
        outState.putString(AllFruitVegOfListActivity.IMG_NAME, imgCustomDialog)
        super.onSaveInstanceState(outState)
    }

    companion object{
        const val CUSTOM_DIALOG = "custom_dialog"
        const val QUANTITY_CUSTOM_DIALOG = "quantity_custom_dialog"
        const val ID_FRUIT_CUSTOM_DIALOG = "id_fruit_custom_dialog"
        const val ID_LIST = "id_list"
        const val IMG_NAME = "img_name"
    }
}
