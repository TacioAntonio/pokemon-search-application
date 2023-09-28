package com.example.application

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableInferredTarget
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.application.ui.theme.ApplicationTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.timerTask

class Pokemon(
    id: Int,
    name: String,
    frontSprite: String,
    types: Array<Type>
) {
    private val id: Int = id
    private val name: String = name
    private val frontSprite: String = frontSprite
    private val types:  Array<Type> = types


    val getName: String
         get() = name;

    val getFrontSprite: String
        get() = frontSprite;

    val getTypes: Array<Type>
        get() = types;
}

class Home : ComponentActivity() {

    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ApplicationTheme {
                var currentName by remember { mutableStateOf("") }
                var currentPokemon: Pokemon? by remember { mutableStateOf(null) }
                var hasLoading by remember { mutableStateOf(true) }

                Column {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color(0xFF2D2F31)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = R.drawable.ic_pokemon_foreground,
                            contentDescription = null,
                            modifier = Modifier.size(width = 96.dp, height = 96.dp)
                        );

                        Text(
                            "Who are you looking for? ",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFFFF)
                        );

                        SearchBox(
                            placeholder="Search pokemons...",
                            textButton="Search",
                            onSearchClick = { newName ->
                                currentName = newName
                                PokemonService().getPokemon(currentName) { responseOrError ->
                                    when (responseOrError) {
                                        is ResponseOrError.Success -> {
                                            val pokemonResponse = responseOrError.response.body()
                                            if (pokemonResponse != null) {
                                                println("##### $pokemonResponse")
                                                val (id, name, sprites, types) = pokemonResponse
                                                currentPokemon = Pokemon(
                                                    id,
                                                    name,
                                                    sprites.front_default,
                                                    types
                                                )

                                                hasLoading = false
                                            }
                                        }

                                        is ResponseOrError.Failure -> {
//                                            val throwable = responseOrError.throwable
                                            currentPokemon = null
                                            hasLoading = false
                                        }
                                    }
                                }
                            }
                        );
                    }

                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (currentPokemon != null) {
                            LoadingWrapper(
                                hasLoading,
                                { newHasLoading -> hasLoading = newHasLoading },
                                {
                                    Column (
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        AsyncImage(
                                            model = currentPokemon!!.getFrontSprite,
                                            contentDescription = currentPokemon!!.getName,
                                            modifier = Modifier
                                                .width(160.dp)
                                                .height(160.dp)
                                        );

                                        Text(
                                            currentPokemon!!.getName.capitalize(),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        );

                                        Row {
                                            currentPokemon!!.getTypes.map { type ->
                                                Text(
                                                    type.type.name,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                );

                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                        }
                                    }
                                }
                            )
                        } else {
                            LoadingWrapper(
                                hasLoading,
                                { newHasLoading -> hasLoading = newHasLoading },
                                {
                                    Column (
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        AsyncImage(
                                            model = R.drawable.ic_interrogation_foreground,
                                            contentDescription = "Who?",
                                            modifier = Modifier.size(width = 160.dp, height = 160.dp)
                                        );
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBox(
    placeholder: String = "Label",
    textButton: String = "Click",
    onSearchClick: (String) -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
    ) {
        var nameInput by remember { mutableStateOf(TextFieldValue(""))  }

        BasicTextField(
            value = nameInput,
            onValueChange = {
                nameInput = it
            },
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .background(Color.White, CircleShape)
                .padding(16.dp, 8.dp),
            decorationBox = { innerTextField ->
                if(nameInput.text.isEmpty())
                    Text(text = placeholder, color = Color.Gray)

                innerTextField.invoke()
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedButton(
            onClick={ onSearchClick(nameInput.text) },
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .height(IntrinsicSize.Min),
            shape = CircleShape
        ) {
            Text(
                textButton.uppercase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun LoadingWrapper(
    hasLoading: Boolean,
    callbackHasLoading: (Boolean) -> Unit,
    callbackUI: @Composable () -> Unit
) {
    var currentProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(hasLoading) {
        loadProgress { progress ->
            currentProgress = progress
        }

        callbackHasLoading(true)
    }

    if (hasLoading) {
        callbackUI()
    } else {
        CircularProgressIndicator(
            modifier = Modifier.size(width = 64.dp, height = 64.dp),
            progress = currentProgress,
            color = Color.DarkGray
        )
    }
}

suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..100) {
        updateProgress(i.toFloat() / 100)
        delay(50)
    }
}

