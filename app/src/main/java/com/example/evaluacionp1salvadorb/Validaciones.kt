package com.example.evaluacionp1salvadorb

object Validaciones {
    private val REGEX_CODIGO = Regex("^[A-Za-z]{2}\\d{2}[A-Za-z]{2}$")

    fun esCodigoValido(codigo: String): Boolean {
        return REGEX_CODIGO.matches(codigo)
    }
}