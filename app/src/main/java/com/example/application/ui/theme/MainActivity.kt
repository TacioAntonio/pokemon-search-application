package com.example.application.ui.theme

import android.content.Intent
import android.database.Cursor
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableInferredTarget
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.application.ui.theme.ApplicationTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.Objects

data class PokemonResponse(
    val id: Int,
    val name: String,
    val sprites: Any
    // Outros campos que você deseja mapear
)

interface PokeApiService {
    @GET("pokemon/{name}")
    fun getPokemon(@Path("name") name: String): Call<PokemonResponse>
}

fun takePokemon() {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val pokeApiService = retrofit.create(PokeApiService::class.java)
    val call = pokeApiService.getPokemon("ditto")

    call.enqueue(object : Callback<PokemonResponse> {
        override fun onResponse(call: Call<PokemonResponse>, response: Response<PokemonResponse>) {
            if (response.isSuccessful) {
                val pokemon = response.body()
                 println("Pokémon: "..pokemon.toString())
                // Faça algo com os dados do Pokémon aqui
            } else {
                // Lidar com erros aqui
            }
        }

        override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
            // Lidar com falhas na comunicação aqui
        }
    })
}

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Handler(Looper.getMainLooper()).postDelayed({
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }, 3000)

        setContent {
            ApplicationTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0xFFD0BCFF))
                    //                    color = Color(0xFFD0BCFF)
                ) {
                    var currentName by remember { mutableStateOf("") };
                    var nameInput by remember { mutableStateOf("") }

                    Text(
                        currentName.uppercase(),
                        fontWeight = FontWeight.Bold
                    );

                    Row {
                        TextField(
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                            },
                            label = { Text("Label") }
                        )

                        Button(
                            onClick = { currentName = nameInput },
                            modifier = Modifier
                                .padding(4.dp)
                                .wrapContentSize(Alignment.Center)
                                .height(IntrinsicSize.Min),
                            shape = CutCornerShape(10)
                        ) {
                            Text(
                                "Search".uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            );
                        }
                    }

                    //                    takePokemon();
                    Column(
                        modifier   = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = "https://cdn-icons-png.flaticon.com/512/4128/4128240.png",
                            contentDescription = null,
                            modifier = Modifier.size(width = 64.dp, height = 64.dp)
                        );

                        Spacer(modifier = Modifier.height(16.dp));

                        Text(
                            "PokemonName".uppercase(),
                            fontWeight = FontWeight.Bold
                        );

                        Spacer(modifier = Modifier.height(8.dp));

                        Text("Type 1");
                        Text("Type N");
                    }
                }
            }
        }
    }
}

//val peoples: List<People> = listOf(People(
//    "Max",
//    "https://cdn-icons-png.flaticon.com/512/4128/4128240.png"
//), People(
//    "Octavio",
//    "https://cdn-icons-png.flaticon.com/512/9308/9308310.png"
//));

//peoples.forEach { eachPeople ->
//    Row {
//        AsyncImage(
//            model = eachPeople.image,
//            contentDescription = null,
//            modifier = Modifier.size(width = 64.dp, height = 64.dp)
//        )
//
//        Spacer(modifier = Modifier.width(16.dp))
//
//        Column {
//            Text(
//                eachPeople.name,
//                fontWeight = FontWeight.Bold
//            );
//            Text("Description");
//        }
//    }
//}
class People(name: String = "Sem nome", image: String = "") {
   val name: String = name;
    val image: String = image;
}

@Composable
fun Greeting(people: People, modifier: Modifier = Modifier) {
    Text(
        text = "Hi ${people.name.uppercase()}! Welcome!",
        modifier = modifier,
        style = TextStyle(
//            color = Color(255, 255, 255),
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
    )

}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    ApplicationTheme {
//        Greeting(People("Tácio"))
//    }
//}

//implementation("io.coil-kt:coil:2.4.0")
//implementation("io.coil-kt:coil-compose:2.4.0")
//<uses-permission android:name="android.permission.INTERNET" />