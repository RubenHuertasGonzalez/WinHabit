package com.institutvidreres.winhabit.model

data class InicioPerfil(
    val nivel: Int = 1,
    val monedas: Int = 0,
    val experiencia: Int = 0,
    val porcentajeNecesario: Int = 10,
    val nivelMaximoAlcanzado: Boolean = false,
    val vidasPerdidas: Int = 0,
    var tareasCompletadas: Int =0,
    val userId: String = ""
)
