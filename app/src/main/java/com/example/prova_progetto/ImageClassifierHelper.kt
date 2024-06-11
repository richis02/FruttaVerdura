package com.example.prova_progetto

import android.content.Context
import android.graphics.Bitmap
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.tensorflow.lite.Interpreter


class ImageClassifierHelper(
    private var threshold: Float = 0.9f,
    val context: Context,
    val imageClassifierListener: ClassifierListener?,
    val fruitAndVegetableArray: List<String>
) {

    private var interpreter: Interpreter? = null
    init {
        interpreter = loadModelFile(context)?.let { Interpreter(it) }
    }
    fun classifyBitmap(bitmap: Bitmap) {
        if (interpreter != null) {
            val output = Array(1) { FloatArray(fruitAndVegetableArray.size) } // Correggi la forma dell'array di output
            val input = preprocessBitmap(bitmap)
            interpreter!!.run(input, output) //sopra c'è il controllo che non sia null
            val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
            val resultString = fruitAndVegetableArray[maxIndex]
            if (output[0][maxIndex] > threshold)
                imageClassifierListener?.onResults(resultString)
            else
                imageClassifierListener?.onResults("")
        }
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: String?
        )
    }

    companion object {
        const val modelName = "fruitVegClassifyModel.tflite"
    }

    private fun loadModelFile(context: Context): MappedByteBuffer? {
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
        val imageSize = 224
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
