package com.example.prova_progetto

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class FruitListActivity : ComponentActivity(){

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fruit_list)

        val string = intent.getStringExtra( "list_key")

        val tv: TextView = findViewById(R.id.tv)
        tv.text = "Lista id: $string"

    }
}