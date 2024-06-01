package com.example.prova_progetto

import FruitVegOfListAdapter
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FruitListActivity : ComponentActivity(){

    private val REQUEST_CAMERA_PERMISSION: Int = 123

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

        val cameraButton : FloatingActionButton = findViewById(R.id.fab_camera)
        val cercaButton : FloatingActionButton = findViewById(R.id.fab_cerca)

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
            fruitVegViewModel.getAllFruitsVegOfList(listId).observe(this, Observer {fruits ->
                fruits?.let{
                    adapter.submitList(it)
                }
            })
        }

        //TODO: COSI è IL MODO CORRETTO DI SCRIVERE STRINGHE STATICHE

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

}

//        if (listId != null) {
//            tv.text = getString(R.string.list_id_text, listId)
//        } else {
//            tv.text = getString(R.string.list_id_unavailable)
//        }