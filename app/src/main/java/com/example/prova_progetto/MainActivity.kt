package com.example.prova_progetto

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.prova_progetto.ui.theme.Prova_progettoTheme

//IMPORT AGGIUNTI
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val fruitAndVegetableArray = arrayOf(
        "apple", "banana", "beetroot", "bell pepper", "cabbage", "capsicum", "carrot", "cauliflower",
        "chilli pepper", "corn", "cucumber", "eggplant", "garlic", "ginger", "grapes", "jalepeno",
        "kiwi", "lemon", "lettuce", "mango", "onion", "orange", "paprika", "pear", "peas",
        "pineapple", "pomegranate", "potato", "raddish", "soy beans", "spinach", "sweetcorn",
        "sweetpotato", "tomato", "turnip", "watermelon"
    )
    val context = LocalContext.current
    val bitmap = remember { loadBitmapFromAssets(context, "pomodoro.jpg") }
    val result = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        bitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.size(200.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val interpreter = loadModelFile(context, "my_model.tflite")?.let { Interpreter(it) }
            if (interpreter != null && bitmap != null) {
                val output = Array(1) { FloatArray(36) } // Correggi la forma dell'array di output
                val input = preprocessBitmap(bitmap)
                interpreter.run(input, output)
                val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
                result.value = "Prediction: ${fruitAndVegetableArray[maxIndex]}, Confidence: ${output[0][maxIndex]}"
            }
        }) {
            Text("Run Model")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(result.value)
    }
}

fun loadBitmapFromAssets(context: Context, fileName: String): Bitmap? {
    return try {
        val inputStream = context.assets.open(fileName)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
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