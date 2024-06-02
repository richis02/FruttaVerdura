package com.example.prova_progetto.Activity

import com.example.prova_progetto.Adapter.FruitVegOfListAdapter
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prova_progetto.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AllFruitVegOfListActivity : ComponentActivity(){

    private val REQUEST_CAMERA_PERMISSION: Int = 123

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }
    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fruit_list)

        val listId: Long? = intent.getLongExtra("list_key", -1L).takeIf { it != -1L }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_fruit_of_list)
        val adapter = FruitVegOfListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val cameraButton : FloatingActionButton = findViewById(R.id.fab_camera)
        val cercaButton : FloatingActionButton = findViewById(R.id.fab_cerca)
        val mostraButton: FloatingActionButton = findViewById(R.id.fab_mostra)
        val rimuoviButton: FloatingActionButton = findViewById(R.id.fab_rimuovi)

        mostraButton.setOnClickListener{
            if(cameraButton.isVisible){
                cercaButton.visibility = View.GONE
                cameraButton.visibility = View.GONE
                rimuoviButton.visibility = View.GONE
                mostraButton.setImageResource(R.drawable.baseline_more_horiz_24)
            }
            else{
                cercaButton.visibility = View.VISIBLE
                cameraButton.visibility = View.VISIBLE
                rimuoviButton.visibility = View.VISIBLE
                mostraButton.setImageResource(R.drawable.baseline_more_vert_24)
            }
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