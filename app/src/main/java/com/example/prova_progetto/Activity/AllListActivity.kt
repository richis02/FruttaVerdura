package com.example.prova_progetto.Activity

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
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
import com.example.prova_progetto.db.ListFruitsCrossRef

class AllListActivity: ComponentActivity(), OnItemsListClickListener {

    private var isShowCustomDialog: Boolean = false
    private lateinit var dialog: Dialog
    private var listNewNameId: Long = -1
    private var listNewName: String = ""
    private var etNewName: EditText? = null

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
        back.setOnClickListener {v ->
            val intent = Intent(v.context, MainActivity::class.java)
            v.context.startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@AllListActivity, MainActivity::class.java)
                startActivity(intent)
            }
        })

        listTitleTv = findViewById(R.id.fruit_list_name)
        listTitleTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    //il nome delle liste può contenere solo spazi, lettere e numeri
                    //non consentiamo caratteri speciali e a capo
                    var filtered = it.toString().filter { char -> char.isLetterOrDigit() || char == ' ' }

                    //non permettiamo di avere 2 spazi consecutivi
                    while (filtered.contains("  ")) {
                        filtered = filtered.replace("  ", " ")
                    }

                    //la lunghezza del nome è max 20
                    if (filtered.length > 20) {
                        filtered = filtered.substring(0, 20)
                    }

                    if (filtered != it.toString()) {
                        listTitleTv.setText(filtered)
                        listTitleTv.setSelection(filtered.length)
                    }
                }
            }
        })
        listTitleTv.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onAddList()
                true
            } else {
                false
            }
        }


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

        addList.setOnClickListener { onAddList() }

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

            if(it.getBoolean(CUSTOM_DIALOG)){
                listNewName = it.getString(UPDATE_LIST_NAME) ?: ""
                listNewNameId = it.getLong(LIST_KEY)
                showCustomDialog()
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

    override fun onUpdateTitleClick(id: Long) {
        listNewNameId = id
        showCustomDialog()
    }


    private fun onAddList(){
        if(listTitleTv.text.toString() != "") {
            val newList = ItemsList(listTitle = listTitleTv.text.toString())
            fruitVegViewModel.insertList(newList)
            listTitleTv.text = null //tolgo il testo
        }
    }

    private fun showCustomDialog() {
        isShowCustomDialog = true
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_custom)

        etNewName = dialog.findViewById(R.id.et_new_name_list)
        etNewName!!.visibility = View.VISIBLE
        etNewName!!.setText(listNewName)

        etNewName!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    //il nome delle liste può contenere solo spazi, lettere e numeri
                    //non consentiamo caratteri speciali e a capo
                    var filtered = it.toString().filter { char -> char.isLetterOrDigit() || char == ' ' }

                    //non permettiamo di avere 2 spazi consecutivi
                    while (filtered.contains("  ")) {
                        filtered = filtered.replace("  ", " ")
                    }

                    //la lunghezza del nome è max 20
                    if (filtered.length > 20) {
                        filtered = filtered.substring(0, 20)
                    }

                    if (filtered != it.toString()) {
                        etNewName!!.setText(filtered)
                        etNewName!!.setSelection(filtered.length)
                    }
                }
            }
        })

        val linearLayout: LinearLayout = dialog.findViewById(R.id.modify_quantity)
        linearLayout.visibility = View.GONE
        val tvMessage: TextView = dialog.findViewById(R.id.message)
        tvMessage.visibility = View.GONE
        val tvMessageTitle: TextView = dialog.findViewById(R.id.title_dialog)
        tvMessageTitle.visibility = View.GONE

        val annullaButton: Button = dialog.findViewById(R.id.btn_annulla)
        annullaButton.setOnClickListener {
            isShowCustomDialog = false
            listNewNameId = -1
            listNewName = ""
            dialog.dismiss()
        }

        val confermaButton: Button = dialog.findViewById(R.id.btn_conferma)
        confermaButton.setOnClickListener {
            listNewName = etNewName!!.text.toString()
            if(listNewName != "") {
                fruitVegViewModel.updateListTitle(listNewNameId, listNewName)
                isShowCustomDialog = false
                listNewNameId = -1
                listNewName = ""
                dialog.dismiss()
            }
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        //distruggo il dialog quando l'attività viene distrutta in modo da non aver problemi
        if(isShowCustomDialog)
            dialog.dismiss()
    }

    companion object{
        const val LIST_KEY = "list_key"
        const val LIST_NAME = "list_name"
        const val IS_DELETING = "is_deleting"
        const val INDEXES_TO_DELETE = "indexes_to_delete"
        const val NEW_LIST_NAME = "new_list_name"
        const val CUSTOM_DIALOG = "custom_dialog"
        const val UPDATE_LIST_NAME = "update_list_name"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_DELETING, isDeleting)
        outState.putLongArray(INDEXES_TO_DELETE, indexesToDelete.toLongArray())
        outState.putString(NEW_LIST_NAME, listTitleTv.text.toString())
        outState.putBoolean(CUSTOM_DIALOG, isShowCustomDialog)
        outState.putLong(LIST_KEY, listNewNameId)
        outState.putString(UPDATE_LIST_NAME, etNewName?.text?.toString() ?: ""  )
        super.onSaveInstanceState(outState)
    }
}

