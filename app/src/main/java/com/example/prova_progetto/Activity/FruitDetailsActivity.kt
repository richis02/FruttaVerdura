package com.example.prova_progetto.Activity

import android.os.Bundle
import android.widget.ImageView
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

        val fruit_name: String? = intent.getStringExtra("fruit_key")

        val tvNome: TextView = findViewById(R.id.title_fruit)
        tvNome.text = fruit_name

        fruit_name?.let {
            fruitVegViewModel.getFruitVeg(fruit_name).observe(this, Observer {fruit ->
                fruit?.let{
                    val imageResourceId = resources.getIdentifier(
                        it.img, "drawable",
                        packageName
                    )
                    val image: ImageView = findViewById(R.id.fruit_image)
                    if (imageResourceId != 0) // Verifico che l'ID della risorsa sia valido
                        image.setImageResource(imageResourceId);

                    val tvEnergyJ: TextView = findViewById(R.id.tv_energy_j)
                    tvEnergyJ.text = "Energia (Joule): ${it.energyJoule}"

                    val tvEnergyC: TextView = findViewById(R.id.tv_energy_c)
                    tvEnergyC.text = "Energia (KiloCalorie): ${it.energyCal}"

                    val tvProtein: TextView = findViewById(R.id.tv_protein)
                    tvProtein.text = "Proteine: ${it.proteins}"

                    val tvCarbohydrates: TextView = findViewById(R.id.tv_carbohydrates)
                    tvCarbohydrates.text = "Carboidrati: ${it.carbohydrates}"

                    val tvLipids: TextView = findViewById(R.id.tv_lipids)
                    tvLipids.text = "Lipidi: ${it.lipids}"

                    val tvFibre: TextView = findViewById(R.id.tv_fibre)
                    tvFibre.text = "Fibre: ${it.fibre}"
                }
            })
        }
    }
}