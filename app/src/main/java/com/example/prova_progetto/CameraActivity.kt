package com.example.prova_progetto

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity

class CameraActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val imageV: ImageView = findViewById(R.id.image)
        val result: TextView = findViewById(R.id.resul)
        val bitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")

        imageV.setImageBitmap(bitmap)
        if (bitmap != null) {
            result.text = ClassifyImage().findImage(bitmap, this.applicationContext)
        }
    }
}