package com.example.prova_progetto.Activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.example.prova_progetto.R
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import com.example.prova_progetto.db.FruitVegetable
import androidx.lifecycle.Observer

class FruitDetailsActivity: ComponentActivity() {
    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val back: ImageView = findViewById(R.id.back_arrow)
        back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        //val image: ImageView = findViewById(R.id.image)
        //val tv_val_nutr: TextView = findViewById(R.id.valore_nutrizionale)

        val fruit_name: String? = intent.getStringExtra("fruit_key")

        //tv_nome.text = fruit_name

        fruit_name?.let {
            fruitVegViewModel.getFruitVeg(fruit_name).observe(this, Observer {fruit ->
                fruit?.let{
                    //tv_val_nutr.text = it.proteins.toString()
                }
            })
        }
    }
}