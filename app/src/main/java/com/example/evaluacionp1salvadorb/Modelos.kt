package com.example.evaluacionp1salvadorb

import java.time.LocalDateTime

enum class TipoUsuario {
    REGULAR,
    SUSCRIPTOR,
    EMPRESA
}

enum class EstadoSlot {
    LIBRE,
    EN_USO,
    EN_CICLO_FINAL,
    FUERA_DE_SERVICIO
}

data class Ticket(
    val numeroTicket: Int,
    val codigoMaquina: String,
    val tipoMaquina: String,
    val tiempoUsoMinutos: Int,
    val montoPagado: Double,
    val fechaEmision: LocalDateTime = LocalDateTime.now()
)