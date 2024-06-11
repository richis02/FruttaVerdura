package com.example.prova_progetto.Activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.prova_progetto.R
import com.example.prova_progetto.db.FruitVegApplication
import com.example.prova_progetto.db.FruitVegViewModel
import com.example.prova_progetto.db.FruitVegViewModelFactory

class FruitDetailsActivity: ComponentActivity() {
    private val fruitVegViewModel: FruitVegViewModel by viewModels {
        FruitVegViewModelFactory((application as FruitVegApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val back: ImageView = findViewById(R.id.back_arrow)
        back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val fruitName: String? = intent.getStringExtra("fruit_key")

        val tvNome: TextView = findViewById(R.id.title_fruit)
        tvNome.text = fruitName

        fruitName?.let {
            fruitVegViewModel.getFruitVeg(fruitName).observe(this, Observer { fruit ->
                fruit?.let{
                    val imageResourceId = resources.getIdentifier(
                        it.photo, "drawable",
                        packageName
                    )
                    val image: ImageView = findViewById(R.id.fruit_image)
                    if (imageResourceId != 0) // Verifico che l'ID della risorsa sia valido
                        image.setImageResource(imageResourceId)

                    val tvEnergyJ: TextView = findViewById(R.id.number_of_energy_j)
                    tvEnergyJ.text = "${it.energyJoule}"
                    val pbEnergyJ: ProgressBar = findViewById(R.id.stats_progressbar_energy_j)
                    pbEnergyJ.progress = it.energyJoule.toInt()

                    val tvEnergyC: TextView = findViewById(R.id.number_of_energy_cal)
                    tvEnergyC.text = "${it.energyCal}"
                    val pbEnergyCal: ProgressBar = findViewById(R.id.stats_progressbar_energy_cal)
                    pbEnergyCal.progress = it.energyCal.toInt()

                    val tvProtein: TextView = findViewById(R.id.number_of_protein)
                    tvProtein.text = getString(R.string.nutrition_value, it.proteins.toString())
                    val pbProtein: ProgressBar = findViewById(R.id.stats_progressbar_protein)
                    pbProtein.progress = (it.proteins * 100).toInt()

                    val tvCarbohydrates: TextView = findViewById(R.id.number_of_carbohydrates)
                    tvCarbohydrates.text = getString(R.string.nutrition_value, it.carbohydrates.toString())
                    val pbCarbohydrates: ProgressBar = findViewById(R.id.stats_progressbar_carbohydrates)
                    pbCarbohydrates.progress = (it.carbohydrates * 100).toInt()

                    val tvLipids: TextView = findViewById(R.id.number_of_lipids)
                    tvLipids.text = getString(R.string.nutrition_value, it.lipids.toString())
                    val pbLipids: ProgressBar = findViewById(R.id.stats_progressbar_lipids)
                    pbLipids.progress = (it.lipids * 100).toInt()

                    val tvFibre: TextView = findViewById(R.id.number_of_fibers)
                    tvFibre.text = getString(R.string.nutrition_value, it.fibre.toString())
                    val pbFibers: ProgressBar = findViewById(R.id.stats_progressbar_fibers)
                    pbFibers.progress = (it.fibre * 100).toInt()
                }
            })
        }
    }
}