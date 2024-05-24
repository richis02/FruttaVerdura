package com.example.prova_progetto

import android.content.Intent
import android.os.Bundle
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.ImageView
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private val REQUEST_CAMERA_PERMISSION: Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openCamera(view: View){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null){
            startActivityForResult(intent, REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CAMERA_PERMISSION && resultCode == RESULT_OK){
            val imageView: ImageView = findViewById(R.id.image)
            val bitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(bitmap)

            val result: TextView = findViewById(R.id.resul)
            result.text = ClassifyImage().findImage(bitmap, applicationContext)
        }
    }
}