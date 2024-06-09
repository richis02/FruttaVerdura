package com.example.prova_progetto.Activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.prova_progetto.R

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
            setContentView(R.layout.test_main)
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
                //permessi negati --> l'app si chiude
                finishAffinity()
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

    private fun setAllEvent(){
        val camera: ConstraintLayout = findViewById(R.id.open_camera)
        camera.setOnClickListener {v ->
            val intent = Intent(v.context, RealTimeDetectionActivity::class.java)
            v.context.startActivity(intent)
        }

        val list: ConstraintLayout = findViewById(R.id.all_list)
        list.setOnClickListener {v ->
            val intent = Intent(v.context, AllListActivity::class.java)
            v.context.startActivity(intent)
        }

        val search: ConstraintLayout = findViewById(R.id.search)
        search.setOnClickListener {v ->
            val intent = Intent(v.context, FruitVegSearchActivity::class.java)
            v.context.startActivity(intent)
        }
    }

}