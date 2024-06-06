package com.example.prova_progetto.Activity

import android.app.Dialog
import com.example.prova_progetto.Adapter.FruitVegOfListAdapter
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
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

    private val REQUEST_CAMERA_PERMISSION: Int = 123

    private var isDeleting: Boolean = false
    private val indexesToDelete: MutableList<String> = mutableListOf()
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
        back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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

        cameraButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(LIST_KEY, listId)
            if(intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent, REQUEST_CAMERA_PERMISSION)
            }
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
    }

    companion object {
        const val LIST_KEY = "list_key"
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CAMERA_PERMISSION && resultCode == RESULT_OK){
            val bitmap = data?.extras?.get("data") as Bitmap

            val intent = Intent(this, CameraActivity::class.java).apply {
                putExtra("imageBitmap", bitmap)
            }
            startActivity(intent)
        }
    }

    override fun onItemClick(id: String, quantity: Int?) {
        if (!isDeleting) {
            showCustomDialog(id, quantity!!)
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

    private fun showCustomDialog(id: String, qnt: Int?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_custom)

        var quantity: Int = qnt!!

        val annullaButton: Button = dialog.findViewById(R.id.btn_annulla)
        annullaButton.setOnClickListener { dialog.dismiss() }

        val confermaButton: Button = dialog.findViewById(R.id.btn_conferma)
        confermaButton.setOnClickListener {
            // In caso di frutto già presente viene aggiornata la quantità
            fruitVegViewModel.updateQuantity(id, listId!!, quantity - qnt)
            dialog.dismiss()
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvQuantity: TextView = dialog.findViewById(R.id.tv_quantity)
        tvQuantity.text = quantity.toString()
        val btnIncrease: Button = dialog.findViewById(R.id.btn_increase)
        val btnDecrease: Button = dialog.findViewById(R.id.btn_decrease)

        val tvMessage: TextView = dialog.findViewById(R.id.message)
        tvMessage.text = getString(R.string.message_modify)

        btnIncrease.setOnClickListener {
            quantity++
            tvQuantity.text = quantity.toString()
        }

        btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQuantity.text = quantity.toString()
            }
        }
        dialog.show()
    }

}