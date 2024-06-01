package com.example.prova_progetto

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : ComponentActivity() {

    private val REQUEST_CAMERA_PERMISSION: Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED)
        {
            requestCameraPermission()
        }
        else{
            setContentView(R.layout.activity_main)
            setAllEvent()
        }
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            )
        ) { }
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    )
    {
        if (requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                //permessi negati
                //bisogna gestire in qualche modo
            }
            else
            {
                //permessi ok
                setContentView(R.layout.activity_main)
                setAllEvent()
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
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

    private fun setAllEvent(){
        val camera: CardView = findViewById(R.id.open_camera)
        camera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent, REQUEST_CAMERA_PERMISSION)
            }
        }

        val list: CardView = findViewById(R.id.all_list)
        list.setOnClickListener {v ->
            val intent = Intent(v.context, AllListActivity::class.java)
            v.context.startActivity(intent)
        }

        val search: CardView = findViewById(R.id.search)
        search.setOnClickListener {v ->
            val intent = Intent(v.context, FruitVegSearchActivity::class.java)
            v.context.startActivity(intent)
        }
    }
}