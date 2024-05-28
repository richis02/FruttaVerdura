package com.example.prova_progetto

import android.content.Context
import android.graphics.Bitmap
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ClassifyImage constructor(){
    fun findImage(image: Bitmap, context: Context): String{
        val fruitAndVegetableArray = arrayOf(
            "mela", "banana", "barbabietola", "peperone", "cavolo", "capsico", "carota", "cavolfiore",
            "peperoncino", "mais", "cetriolo", "melanzana", "aglio", "zenzero", "uva", "jalepeno",
            "kiwi", "limone", "lattuga", "mango", "cipolla", "arancia", "paprika", "pera", "piselli",
            "ananas", "melograno", "patate", "raddish", "fagioli di soia", "spinaci", "mais dolce",
            "patata dolce", "pomodoro", "rapa", "anguria"
        )

        val interpreter = loadModelFile(context, "model.tflite")?.let { Interpreter(it) }

        if (interpreter != null && image != null) {
            val output = Array(1) { FloatArray(fruitAndVegetableArray.size) } // Correggi la forma dell'array di output
            val input = preprocessBitmap(image)
            interpreter.run(input, output)
            val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
            return "Prediction: ${fruitAndVegetableArray[maxIndex]}, Confidence: ${output[0][maxIndex]}"
        }
        return "Error"
    }

    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer? {
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

    private fun preprocessBitmap(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
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