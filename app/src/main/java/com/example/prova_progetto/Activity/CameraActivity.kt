package com.example.prova_progetto.Activity

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import androidx.lifecycle.Observer
import com.example.prova_progetto.ClassifyImage
import com.example.prova_progetto.R
import kotlin.math.log


class CameraActivity: ComponentActivity(){

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val imageV: ImageView = findViewById(R.id.image)
        val result: TextView = findViewById(R.id.resul)
        val bitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")

        imageV.setImageBitmap(bitmap)
        if (bitmap != null) {
            fruitVegViewModel.allFruitVeg.observe(this, Observer { fruitVegNames ->
                // Controlla se fruitVegNames non è nullo prima di chiamare findImage
                fruitVegNames?.let {
                    val fruitNames : MutableList<String> = mutableListOf()
                    for(x in it)
                        fruitNames.add(x.fruitVegName)
                    Log.d("aaaa",it.size.toString())
                    result.text = ClassifyImage().findImage(bitmap, this.applicationContext, fruitNames)
                }
            })
        }
    }
}