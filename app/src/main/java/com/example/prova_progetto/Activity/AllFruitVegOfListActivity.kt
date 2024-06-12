package com.example.prova_progetto.Activity

import android.app.ActionBar.LayoutParams
import android.app.Dialog
import com.example.prova_progetto.Adapter.FruitVegOfListAdapter
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.OnFruitVegClickListener
import com.example.prova_progetto.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AllFruitVegOfListActivity : ComponentActivity(), OnFruitVegClickListener{
    private lateinit var dialog: Dialog
    private var qntCustomDialogForTV: Int? = null
    private var idCustomDialog: String = ""
    private var qntCustomDialogForQuery: Int? = null
    private var imgCustomDialog : String = ""

    private var isDeleting: Boolean = false
    private var isShowCustomDialog: Boolean = false
    private var indexesToDelete: MutableList<String> = mutableListOf()
    private var listId: Long? = null

    lateinit var recyclerView: RecyclerView
    lateinit var adapter : FruitVegOfListAdapter

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }
    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fruit_list)

        val back: ImageView = findViewById(R.id.back_arrow)
        back.setOnClickListener {v ->
            val intent = Intent(v.context, AllListActivity::class.java)
            v.context.startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@AllFruitVegOfListActivity, AllListActivity::class.java)
                startActivity(intent)
            }
        })

        listId = intent.getLongExtra("list_key", -1L).takeIf { it != -1L }

        val listTitle = intent.getStringExtra("list_name")
        val tvListTitle: TextView = findViewById(R.id.tv_nome_lista)
        tvListTitle.text = listTitle

        recyclerView = findViewById(R.id.recycler_fruit_of_list)
        adapter = FruitVegOfListAdapter(this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val cameraButton : FloatingActionButton = findViewById(R.id.fab_camera)
        val cercaButton : FloatingActionButton = findViewById(R.id.fab_cerca)
        val mostraButton: FloatingActionButton = findViewById(R.id.fab_mostra)
        val rimuoviButton: FloatingActionButton = findViewById(R.id.fab_rimuovi)

        val eliminaButton: Button = findViewById(R.id.btn_elimina)
        val annullaButton: Button = findViewById(R.id.btn_annulla)
        val constraintLayout: ConstraintLayout = findViewById(R.id.btn_elimina_linear)

        mostraButton.setOnClickListener{
            if(cameraButton.isVisible){
                cercaButton.visibility = View.GONE
                cameraButton.visibility = View.GONE
                rimuoviButton.visibility = View.GONE
            }
            else{
                cercaButton.visibility = View.VISIBLE
                cameraButton.visibility = View.VISIBLE
                rimuoviButton.visibility = View.VISIBLE
            }
        }

        rimuoviButton.setOnClickListener {
            isDeleting = true
            cercaButton.visibility = View.GONE
            cameraButton.visibility = View.GONE
            rimuoviButton.visibility = View.GONE
            mostraButton.visibility = View.GONE

            constraintLayout.visibility = View.VISIBLE
        }

        eliminaButton.setOnClickListener {
            for(index in indexesToDelete)
                fruitVegViewModel.deleteFruitListCrossRef(index, listId!!)
            //listId non può essere nullo
            constraintLayout.visibility = View.GONE
            mostraButton.visibility = View.VISIBLE
            isDeleting = false
        }
        annullaButton.setOnClickListener {
            indexesToDelete.clear()
            constraintLayout.visibility = View.GONE
            mostraButton.visibility = View.VISIBLE
            isDeleting = false
            val adapter = (recyclerView.adapter as FruitVegOfListAdapter)
            adapter.updateSelectedItems(indexesToDelete)
        }

        cameraButton.setOnClickListener { v ->
            val intent = Intent(v.context, RealTimeDetectionActivity::class.java)
            intent.putExtra(LIST_KEY, listId)
            intent.putExtra(LIST_NAME, listTitle)
            v.context.startActivity(intent)
        }

        cercaButton.setOnClickListener { v ->
            val intent = Intent(v.context, FruitVegSearchActivity::class.java)
            intent.putExtra(LIST_KEY, listId)
            v.context.startActivity(intent)
        }

        listId?.let {
            fruitVegViewModel.getAllFruitsVegOfList(listId!!).observe(this, Observer {fruits ->
                fruits?.let{
                    adapter.submitList(it)
                }
            })
        }


        savedInstanceState?.let {
            listId = it.getLong(LIST_KEY)

            if(it.getBoolean(IS_DELETING)) {
                isDeleting = true
                indexesToDelete = it.getStringArrayList(INDEXES_TO_DELETE)?.toMutableList() ?: mutableListOf()
                val adapter = (recyclerView.adapter as FruitVegOfListAdapter)
                adapter.updateSelectedItems(indexesToDelete)

                mostraButton.visibility = View.GONE
                cameraButton.visibility = View.GONE
                cercaButton.visibility = View.GONE
                rimuoviButton.visibility = View.GONE
                constraintLayout.visibility = View.VISIBLE
            }

            if(it.getBoolean(CUSTOM_DIALOG)) {
                isShowCustomDialog = true
                it.getString(ID_FRUIT)
                    ?.let { fruitName ->
                        idCustomDialog = fruitName
                    }
                qntCustomDialogForTV = it.getInt(QUANTITY_CUSTOM_DIALOG_FOR_TV)
                qntCustomDialogForQuery = it.getInt(QUANTITY_CUSTOM_DIALOG_FOR_QUERY)
                imgCustomDialog = it.getString(IMG_NAME) ?: ""
                showCustomDialog()
            }
        }
    }

    override fun onItemClick(id: String, quantity: Int?, icon : String) {
        if (!isDeleting) {
            idCustomDialog = id
            qntCustomDialogForQuery = quantity
            qntCustomDialogForTV = quantity
            imgCustomDialog = icon
            showCustomDialog()
        } else {
            val index = indexesToDelete.indexOf(id)
            if (index != -1) {
                indexesToDelete.removeAt(index)
            } else {
                indexesToDelete.add(id)
            }
            // Aggiorna il RecyclerView per riflettere i cambiamenti visivi
            val adapter = (recyclerView.adapter as FruitVegOfListAdapter)
            adapter.updateSelectedItems(indexesToDelete)
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

        //
        val annullaButton: Button = dialog.findViewById(R.id.btn_annulla)
        annullaButton.setOnClickListener {
            isShowCustomDialog = false
            dialog.dismiss()
        }

        val confermaButton: Button = dialog.findViewById(R.id.btn_conferma)
        confermaButton.setOnClickListener {
            // In caso di frutto già presente viene aggiornata la quantità
            fruitVegViewModel.updateQuantity(idCustomDialog, listId!!,
                qntCustomDialogForTV?.minus(qntCustomDialogForQuery!!) ?: 0
            )
            isShowCustomDialog = false
            dialog.dismiss()
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val tvQuantity: TextView = dialog.findViewById(R.id.tv_quantity)
        tvQuantity.text = qntCustomDialogForTV.toString()
        val btnIncrease: Button = dialog.findViewById(R.id.btn_increase)
        val btnDecrease: Button = dialog.findViewById(R.id.btn_decrease)

        val tvMessage: TextView = dialog.findViewById(R.id.message)
        tvMessage.text = getString(R.string.message_modify_selection)
        val tvMessageTitle: TextView = dialog.findViewById(R.id.title_dialog)
        tvMessageTitle.text = getString(R.string.message_modify)

        btnIncrease.setOnClickListener {
            qntCustomDialogForTV = qntCustomDialogForTV!! + 1
            tvQuantity.text = qntCustomDialogForTV.toString()
        }

        btnDecrease.setOnClickListener {
            if (qntCustomDialogForTV!! > 1) {
                qntCustomDialogForTV = qntCustomDialogForTV!! - 1
                tvQuantity.text = qntCustomDialogForTV.toString()
            }
        }
        dialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        //distruggo il dialog quando l'attività viene distrutta in modo da non aver problemi
        if(isShowCustomDialog)
            dialog.dismiss()
    }

    //Salvataggio dello stato
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_DELETING, isDeleting)
        outState.putStringArrayList(INDEXES_TO_DELETE, ArrayList(indexesToDelete))
        outState.putBoolean(CUSTOM_DIALOG, isShowCustomDialog)
        outState.putInt(QUANTITY_CUSTOM_DIALOG_FOR_QUERY, qntCustomDialogForQuery ?: -1)
        outState.putString(ID_FRUIT, idCustomDialog)
        outState.putInt(QUANTITY_CUSTOM_DIALOG_FOR_TV, qntCustomDialogForTV ?: -1)
        outState.putLong(LIST_KEY, listId!!)
        outState.putString(IMG_NAME, imgCustomDialog)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val LIST_KEY = "list_key"
        const val LIST_NAME = "list_name"
        const val IS_DELETING = "is_deleting"
        const val INDEXES_TO_DELETE = "indexes_to_delete"
        const val CUSTOM_DIALOG = "custom_dialog"
        const val QUANTITY_CUSTOM_DIALOG_FOR_QUERY = "quantity_custom_dialog_for_query"
        const val QUANTITY_CUSTOM_DIALOG_FOR_TV = "quantity_custom_dialog_for_tv"
        const val ID_FRUIT = "id_fruit"
        const val IMG_NAME = "img_name"
    }
}