package com.example.prova_progetto

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class FruitListActivity : ComponentActivity(){

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fruit_list)

        val listId: Long? = intent.getLongExtra("list_key", -1L).takeIf { it != -1L }

        val tv: TextView = findViewById(R.id.tv)
        if (listId != null) {
            tv.text = "Lista id: $listId"
        } else {
            tv.text = "Lista id non disponibile"
        }
        //TODO: COSI è IL MODO CORRETTO DI SCRIVERE STRINGHE STATICHE
//        if (listId != null) {
//            tv.text = getString(R.string.list_id_text, listId)
//        } else {
//            tv.text = getString(R.string.list_id_unavailable)
//        }
    }
}