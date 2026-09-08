package com.example.evaluacionp1salvadorb

import java.time.LocalDateTime

abstract class Maquina(
    val codigo: String,
    val marcaModelo: String,
    val tipoUsuario: TipoUsuario,
    val fechaIngreso: LocalDateTime = LocalDateTime.now()
) {
    init {
        if (!Validaciones.esCodigoValido(codigo)) {
            throw CodigoInvalidoException("El código '$codigo' no cumple el formato requerido (2 letras, 2 dígitos, 2 letras).")
        }
    }

    abstract fun calcularCostoBase(tiempoMinutos: Int): Double

    fun calcularMontoFinal(tiempoMinutos: Int): Double {
        val costoBase = calcularCostoBase(tiempoMinutos)

        if (costoBase == 0.0) {
            return 0.0
        }

        val montoConIva = costoBase * 1.19

        val montoFinal = if (tipoUsuario == TipoUsuario.EMPRESA) {
            montoConIva * 0.50
        } else {
            montoConIva
        }

        if (montoFinal < 0) {
            throw TarifaInvalidaException("Error de datos: El cálculo resultó en un monto negativo.")
        }

        return montoFinal
    }

    abstract fun obtenerDetalle(): String
}

class Lavadora(
    codigo: String,
    marcaModelo: String,
    tipoUsuario: TipoUsuario
) : Maquina(codigo, marcaModelo, tipoUsuario) {

    override fun calcularCostoBase(tiempoMinutos: Int): Double {
        val tarifaPorHora = 1200.0
        var horasEfectivas = tiempoMinutos / 60.0

        if (tipoUsuario == TipoUsuario.SUSCRIPTOR) {
            horasEfectivas *= 0.80
        }

        return horasEfectivas * tarifaPorHora
    }

    override fun obtenerDetalle(): String = "Lavadora ($marcaModelo)"
}

class Secadora(
    codigo: String,
    marcaModelo: String,
    tipoUsuario: TipoUsuario
) : Maquina(codigo, marcaModelo, tipoUsuario) {

    override fun calcularCostoBase(tiempoMinutos: Int): Double {
        if (tiempoMinutos < 30) {
            return 0.0
        }
        val tarifaPorHora = 1000.0
        val horas = tiempoMinutos / 60.0
        return horas * tarifaPorHora
    }

    override fun obtenerDetalle(): String = "Secadora ($marcaModelo)"
}

class LavasecaIndustrial(
    codigo: String,
    marcaModelo: String,
    tipoUsuario: TipoUsuario,
    val conVapor: Boolean
) : Maquina(codigo, marcaModelo, tipoUsuario) {

    override fun calcularCostoBase(tiempoMinutos: Int): Double {
        var tarifaPorHora = 2800.0
        if (conVapor) {
            tarifaPorHora *= 1.30
        }
        val horas = tiempoMinutos / 60.0
        return horas * tarifaPorHora
    }

    override fun obtenerDetalle(): String {
        val detalleVapor = if (conVapor) "Con Vapor" else "Sin Vapor"
        return "Lavaseca Industrial ($marcaModelo, $detalleVapor)"
    }
}