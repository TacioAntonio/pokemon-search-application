package com.example.application

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

data class Sprites(
    val front_default: String
)

data class Type(
    val type: TypeName
)

data class TypeName(
    val name: String
)

data class PokemonResponse(
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val types: Array<Type>
)

interface IPokemonService {
    @GET("pokemon/{name}")
    fun getPokemon(@Path("name") name: String): Call<PokemonResponse>
}

inline fun <reified T> serviceBuilder(baseUrl: String): T {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(T::class.java)
}

sealed class ResponseOrError {
    data class Success(val response: Response<PokemonResponse>) : ResponseOrError()
    data class Failure(val throwable: Throwable) : ResponseOrError()
}

class PokemonService {
     private val serviceCreate = serviceBuilder<IPokemonService>("https://pokeapi.co/api/v2/")

     fun getPokemon(pokemonName: String, onResponse: (ResponseOrError: ResponseOrError) -> Unit) {
        val call = serviceCreate.getPokemon(pokemonName.lowercase())

        call.enqueue(object : Callback<PokemonResponse> {
            override fun onResponse(call: Call<PokemonResponse>, response: Response<PokemonResponse>) {
                if (response.isSuccessful) {
                    onResponse(ResponseOrError.Success(response))
                } else {
                    onResponse(ResponseOrError.Failure(Throwable("Erro na resposta")))
                }
            }

            override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                onResponse(ResponseOrError.Failure(Throwable(t)))
            }
        })
    }
}