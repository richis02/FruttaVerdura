package com.example.prova_progetto.Activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Context
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.prova_progetto.R
import com.example.prova_progetto.db.FruitListRoomDatabase
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val REQUEST_CAMERA_PERMISSION: Int = 123

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }

    private val applicationScope = CoroutineScope(Dispatchers.Default)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //devo controllare se l'app è la prima volta che viene lanciata o se è cambiata la versione del db.
        //in caso positivo del controllo vuol dire che devo fare l'update del db

        if (isFirstRun() || isDatabaseUpdated()) {
            // Cancella tutti i dati esistenti
            fruitVegViewModel.deleteAllFruitVeg()

            val database = FruitListRoomDatabase.getDatabase(this, applicationScope)

            // Popola il database da CSV
            applicationScope.launch(Dispatchers.IO) {
                FruitListRoomDatabase.populateDatabaseFromCSV(this@MainActivity, database.fruitVegDao())
                setFirstRunCompleted()
                setDatabaseUpdated()
            }
        }

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
        val camera: ConstraintLayout = findViewById(R.id.open_camera)
        camera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent, REQUEST_CAMERA_PERMISSION)
            }
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

    private fun isFirstRun(): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_first_run", true)
    }

    private fun setFirstRunCompleted() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("is_first_run", false)
            apply()
        }
    }

    private fun isDatabaseUpdated(): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val currentVersion = 18 // Cambia questo valore ogni volta che aggiorni il database
        val savedVersion = sharedPreferences.getInt("db_version", 0)
        return currentVersion > savedVersion
    }

    private fun setDatabaseUpdated() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val currentVersion = 18 // Deve corrispondere alla versione attuale del database
        with(sharedPreferences.edit()) {
            putInt("db_version", currentVersion)
            apply()
        }
    }
}