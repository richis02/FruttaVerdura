package com.example.prova_progetto.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.camera.core.AspectRatio
import androidx.camera.core.ImageProxy
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.prova_progetto.Adapter.FruitVegSearchAdapter
import com.example.prova_progetto.ImageClassifierHelper
import com.example.prova_progetto.R
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory
import com.example.prova_progetto.db.ListFruitsCrossRef
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RealTimeDetectionActivity : ComponentActivity(), ImageClassifierHelper.ClassifierListener {

    companion object {
        private const val TAG = "Image Classifier"
        const val LIST_ID = "list_key"
        const val LIST_NAME = "list_name"
    }

    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }

    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var bitmapBuffer: Bitmap
    private lateinit var tvResult: TextView
    private lateinit var btnResult : Button
    private lateinit var cardView: CardView
    private lateinit var viewFinder: PreviewView

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_real_time_detection)

        tvResult = findViewById(R.id.tv_result)
        cardView  = findViewById(R.id.cw_result)
        btnResult = findViewById(R.id.btn_result)
        viewFinder = findViewById(R.id.view_camera)

        val back: ImageView = findViewById(R.id.back_arrow)
        back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Se la classificazione è richiesta da una items_list
        val listId = intent.getLongExtra(LIST_ID, -1L).takeIf { it != -1L }
        if(listId == null)
            btnResult.text = "Dettagli"//Le R.string non funzionavano
        else
            btnResult.text = "Aggiungi"

        // Quando l'utente conferma la classificazione del frutto/verdura
        btnResult.setOnClickListener { v ->
            if (listId == null){
                val fruit_name = tvResult.text
                val intent = Intent(v.context, FruitDetailsActivity::class.java)
                intent.putExtra(FruitVegSearchAdapter.ItemListViewHolder.FRUIT_KEY, fruit_name)
                v.context.startActivity(intent)
            } else {
                val listFruitCrossRef = ListFruitsCrossRef(
                        listId = listId,
                        fruitId = tvResult.text.toString(),
                        quantity = 1
                    )
                //so che id sarà id valido quindi posso usare !!
                fruitVegViewModel.insertFruitListCrossRef(listFruitCrossRef)

                val list_name = intent.getStringExtra(LIST_NAME)
                val intent = Intent(v.context, AllFruitVegOfListActivity::class.java)
                intent.putExtra(LIST_ID, listId)
                intent.putExtra(LIST_NAME, list_name)
                v.context.startActivity((intent))
            }
        }

        // Labels
        fruitVegViewModel.allFruitVegNames.observe(this, Observer { fruitVegNames ->
            fruitVegNames?.let {
                imageClassifierHelper =
                    ImageClassifierHelper(context = this, imageClassifierListener = this, fruitAndVegetableArray = it)
            }
        })

        viewFinder.post {
            setUpCamera()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onPause() {
        super.onPause()
        // Rilascia le risorse della camera e dell'analizzatore
        cardView.visibility = View.GONE
        cameraProvider?.unbindAll()
    }

    override fun onResume() {
        super.onResume()
        // Verifica se cameraProvider è già inizializzato
        if (cameraProvider == null) {
            setUpCamera()
        } else {
            bindCameraUseCases()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Shut down our background executor
        cameraExecutor.shutdown()
    }


    // Initialize CameraX, and prepare to bind the camera use cases
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            // Viene eseguito quando ListenableFuture è completato
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector - makes assumption that we're only using the back camera
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview =
            Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        if (!::bitmapBuffer.isInitialized) {
                            // The image rotation and RGB image buffer are initialized only once
                            // the analyzer has started running
                            bitmapBuffer = Bitmap.createBitmap(
                                image.width,
                                image.height,
                                Bitmap.Config.ARGB_8888
                            )
                        }

                        classifyImage(image)
                    }
                }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun classifyImage(image: ImageProxy) {
        // Copy out RGB bits to the shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        imageClassifierHelper.classifyBitmap(bitmapBuffer)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onError(error: String) {
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            tvResult.text = ""
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResults(results: String?) {
        runOnUiThread {
            if(results != "") {
                if (cardView.visibility == View.GONE) cardView.visibility = View.VISIBLE
                tvResult.text = results
            }
        }
    }
}
