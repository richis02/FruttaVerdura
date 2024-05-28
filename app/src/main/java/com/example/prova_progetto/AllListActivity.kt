package com.example.prova_progetto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView

class AllListActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_list)

        val tempList = resources.getStringArray(R.array.flower_array)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = ListAdapter(tempList)
    }
}