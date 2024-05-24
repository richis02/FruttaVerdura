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

        val fruitAndVegetableArray2 = arrayOf(
            "Apple Braeburn", "Apple Crimson Snow", "Apple Golden 1", "Apple Golden 2", "Apple Golden 3",
            "Apple Granny Smith", "Apple Pink Lady", "Apple Red 1", "Apple Red 2", "Apple Red 3", "Apple Red Delicious",
            "Apple Red Yellow 1", "Apple Red Yellow 2", "Apricot", "Avocado", "Avocado ripe", "Banana", "Banana Lady Finger",
            "Banana Red", "Beetroot", "Blueberry", "Cactus fruit", "Cantaloupe 1", "Cantaloupe 2", "Carambula", "Cauliflower",
            "Cherry 1", "Cherry 2", "Cherry Rainier", "Cherry Wax Black", "Cherry Wax Red", "Cherry Wax Yellow", "Chestnut",
            "Clementine", "Cocos", "Corn", "Corn Husk", "Cucumber Ripe", "Cucumber Ripe 2", "Dates", "Eggplant", "Fig",
            "Ginger Root", "Granadilla", "Grape Blue", "Grape Pink", "Grape White", "Grape White 2", "Grape White 3", "Grape White 4",
            "Grapefruit Pink", "Grapefruit White", "Guava", "Hazelnut", "Huckleberry", "Kaki", "Kiwi", "Kohlrabi", "Kumquats",
            "Lemon", "Lemon Meyer", "Limes", "Lychee", "Mandarine", "Mango", "Mango Red", "Mangostan", "Maracuja",
            "Melon Piel de Sapo", "Mulberry", "Nectarine", "Nectarine Flat", "Nut Forest", "Nut Pecan", "Onion Red", "Onion Red Peeled",
            "Onion White", "Orange", "Papaya", "Passion Fruit", "Peach", "Peach 2", "Peach Flat", "Pear", "Pear 2", "Pear Abate",
            "Pear Forelle", "Pear Kaiser", "Pear Monster", "Pear Red", "Pear Stone", "Pear Williams", "Pepino", "Pepper Green",
            "Pepper Orange", "Pepper Red", "Pepper Yellow", "Physalis", "Physalis with Husk", "Pineapple", "Pineapple mine",
            "Pitahaya Red", "Plum", "Plum 2", "Plum 3", "Pomegranate", "Pomelo Sweetie", "Potato Red", "Potato Red Washed", "Potato Sweet",
            "Potato White", "Quince", "Rambutan", "Raspberry", "Redcurrant", "Salak", "Strawberry", "Strawberry Wedge", "Tamarillo",
            "Tangelo", "Tomato 1", "Tomato 2", "Tomato 3", "Tomato 4", "Tomato Cherry Red", "Tomato Heart", "Tomato Marron",
            "Tomato Yellow", "Tomato not Ripened", "Walnut", "Watermelon"
        )

        val interpreter = loadModelFile(context, "my_model_2.0.tflite")?.let { Interpreter(it) }

        if (interpreter != null && image != null) {
            val output = Array(1) { FloatArray(131) } // Correggi la forma dell'array di output
            val input = preprocessBitmap(image)
            interpreter.run(input, output)
            val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
            return "Prediction: ${fruitAndVegetableArray2[maxIndex]}, Confidence: ${output[0][maxIndex]}"
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