package ro.pub.cs.systems.eim.practical.practicaltest02v10

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream

class PracticalTest02MainActivityv10 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v10_main)

        val pokemonNameInput = findViewById<EditText>(R.id.pokemonNameInput)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val typesTextView = findViewById<TextView>(R.id.typesTextView)
        val abilitiesTextView = findViewById<TextView>(R.id.abilitiesTextView)
        val pokemonImageView = findViewById<ImageView>(R.id.pokemonImageView)

        searchButton.setOnClickListener {
            val pokemonName = pokemonNameInput.text.toString()

            if (pokemonName.isNotEmpty()) {
                fetchPokemonData(pokemonName, typesTextView, abilitiesTextView, pokemonImageView)
            } else {
                Toast.makeText(this, "Please enter a Pokémon name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchPokemonData(
        pokemonName: String,
        typesTextView: TextView,
        abilitiesTextView: TextView,
        pokemonImageView: ImageView
    ) {
        thread {
            val url = "https://pokeapi.co/api/v2/pokemon/$pokemonName"
            val response = getHttpResponse(url)

            if (response != null) {
                val pokemon = parsePokemonJson(response)
                runOnUiThread {
                    // Actualizăm UI-ul cu datele obținute
                    typesTextView.text = "Types: ${pokemon.types.joinToString { it.type.name }}"
                    abilitiesTextView.text = "Abilities: ${pokemon.abilities.joinToString { it.ability.name }}"

                    val imageUrl = pokemon.sprites.front_default
                    // Descarcă imaginea și setează-o în ImageView
                    downloadImage(imageUrl, pokemonImageView)
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Failed to fetch Pokémon data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getHttpResponse(urlString: String): String? {
        var result: String? = null
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val inputStream = connection.inputStream.bufferedReader().use { it.readText() }
            result = inputStream
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun parsePokemonJson(json: String): PokemonResponse {
        val jsonObject = JSONObject(json)

        val name = jsonObject.getString("name")

        val types = mutableListOf<Type>()
        val typesArray = jsonObject.getJSONArray("types")
        for (i in 0 until typesArray.length()) {
            val typeObject = typesArray.getJSONObject(i)
            val typeName = typeObject.getJSONObject("type").getString("name")
            types.add(Type(TypeDetails(typeName)))
        }

        val abilities = mutableListOf<Ability>()
        val abilitiesArray = jsonObject.getJSONArray("abilities")
        for (i in 0 until abilitiesArray.length()) {
            val abilityObject = abilitiesArray.getJSONObject(i)
            val abilityName = abilityObject.getJSONObject("ability").getString("name")
            abilities.add(Ability(AbilityDetails(abilityName)))
        }

        val spritesObject = jsonObject.getJSONObject("sprites")
        val frontDefault = spritesObject.getString("front_default")

        return PokemonResponse(name, types, abilities, Sprites(frontDefault))
    }


    private fun downloadImage(imageUrl: String, imageView: ImageView) {
        thread {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val inputStream: InputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                if (bitmap != null) {
                    runOnUiThread {
                        imageView.setImageBitmap(bitmap)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}