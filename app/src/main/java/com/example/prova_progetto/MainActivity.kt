package com.example.prova_progetto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.ImageView
import androidx.compose.runtime.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
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

            classifyImage(bitmap)
        }
    }

    private fun classifyImage(image: Bitmap){
        val fruitAndVegetableArray = arrayOf(
            "apple", "banana", "beetroot", "bell pepper", "cabbage", "capsicum", "carrot", "cauliflower",
            "chilli pepper", "corn", "cucumber", "eggplant", "garlic", "ginger", "grapes", "jalepeno",
            "kiwi", "lemon", "lettuce", "mango", "onion", "orange", "paprika", "pear", "peas",
            "pineapple", "pomegranate", "potato", "raddish", "soy beans", "spinach", "sweetcorn",
            "sweetpotato", "tomato", "turnip", "watermelon"
        )
        val context = applicationContext
        val result: TextView = findViewById(R.id.resul)
        val interpreter = loadModelFile(context, "my_model.tflite")?.let { Interpreter(it) }

        if (interpreter != null && image != null) {
            val output = Array(1) { FloatArray(36) } // Correggi la forma dell'array di output
            val input = preprocessBitmap(image)
            interpreter.run(input, output)
            val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
            result.text =
                "Prediction: ${fruitAndVegetableArray[maxIndex]}, Confidence: ${output[0][maxIndex]}"
        }
    }
    fun loadModelFile(context: Context, modelName: String): MappedByteBuffer? {
        return try {
            val fileDescriptor = context.assets.openFd(modelName)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun preprocessBitmap(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val imageSize = 224  // Modifica in base al tuo modello
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)
        val input = Array(1) { Array(imageSize) { Array(imageSize) { FloatArray(3) } } }
        for (y in 0 until imageSize) {
            for (x in 0 until imageSize) {
                val pixel = resizedBitmap.getPixel(x, y)
                input[0][y][x][0] = (pixel shr 16 and 0xFF) / 255.0f
                input[0][y][x][1] = (pixel shr 8 and 0xFF) / 255.0f
                input[0][y][x][2] = (pixel and 0xFF) / 255.0f
            }
        }
        return input
    }
}