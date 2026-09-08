package com.example.evaluacionp1salvadorb

import kotlinx.coroutines.delay

class SistemaLavanderia(val nombre: String = "LavExpress", val capacidad: Int = 10) {

    val slots: List<Slot> = List(capacidad) { i -> Slot(i + 1) }
    val historialTickets: MutableList<Ticket> = mutableListOf()
    val historialMaquinasAtendidas: MutableList<Pair<Maquina, Int>> = mutableListOf()

    private var contadorTickets = 1

    suspend fun registrarEntrada(maquina: Maquina, onLog: (String) -> Unit) {
        val slotDisponible = slots.find { it.estado == EstadoSlot.LIBRE }
            ?: throw SinCapacidadException("Error: No hay slots libres para la máquina ${maquina.codigo}.")

        onLog("\nENTRADA Asignando ${maquina.codigo} al Slot ${slotDisponible.numero}...")

        slotDisponible.cambiarACicloFinal("Registrando entrada en sensor...")
        onLog("Simulando sensor de entrada (espera 3s)...")
        delay(3000)

        slotDisponible.asignarMaquina(maquina)
        onLog("EXITO ENTRADA Máquina ${maquina.codigo} asignada al Slot ${slotDisponible.numero}.")
    }

    suspend fun registrarSalida(codigoMaquina: String, tiempoUsoMinutos: Int, onLog: (String) -> Unit) {
        val slot = slots.find { it.maquinaAsignada?.codigo == codigoMaquina && it.estado == EstadoSlot.EN_USO }
            ?: throw MaquinaNoEncontradaException("Error: La máquina '$codigoMaquina' no está registrada en ningún slot en uso.")

        val maquina = slot.maquinaAsignada!!
        onLog("\nSALIDA Procesando salida para $codigoMaquina en Slot ${slot.numero}...")

        slot.cambiarACicloFinal("Calculando tarifa...")
        onLog("Simulando procesamiento de salida y cobro (espera 6.5s)...")
        delay(6500)

        val montoPagar = maquina.calcularMontoFinal(tiempoUsoMinutos)

        val nuevoTicket = Ticket(
            numeroTicket = contadorTickets++,
            codigoMaquina = maquina.codigo,
            tipoMaquina = maquina.obtenerDetalle(),
            tiempoUsoMinutos = tiempoUsoMinutos,
            montoPagado = montoPagar
        )

        historialTickets.add(nuevoTicket)
        historialMaquinasAtendidas.add(Pair(maquina, tiempoUsoMinutos))

        slot.liberar()

        onLog("EXITO SALIDA Ticket N°${nuevoTicket.numeroTicket} | Detalle: ${nuevoTicket.tipoMaquina} | Monto: $${String.format("%.2f", nuevoTicket.montoPagado)}")
    }

    fun obtenerCantidadSlotsDisponibles(): Int = slots.count { it.estado == EstadoSlot.LIBRE }

    fun obtenerMaquinasClientesSuscriptores(): List<Maquina> {
        return historialMaquinasAtendidas
            .map { it.first }
            .filter { it.tipoUsuario == TipoUsuario.SUSCRIPTOR }
            .distinctBy { it.codigo }
    }

    fun obtenerIngresoPromedioPorMaquina(): Double {
        if (historialTickets.isEmpty()) return 0.0
        return historialTickets.sumOf { it.montoPagado } / historialTickets.size
    }

    fun obtenerCodigosMaquinasFinalizadas(): List<String> {
        return historialTickets.map { it.codigoMaquina }
    }

    fun obtenerMaquinaConMasTiempoUso(): String {
        val maquinaMax = historialMaquinasAtendidas.maxByOrNull { it.second }
        return maquinaMax?.let { "${it.first.codigo} (${it.second} min)" } ?: "Sin datos"
    }

    fun generarReporteCierre(): String {
        val sb = StringBuilder()

        sb.append("      REPORTE CIERRE DE TURNO - $nombre  \n")


        if (historialTickets.isEmpty()) {
            sb.append("No se registraron atenciones.\n")
        } else {
            sb.append("RESUMEN DE TICKETS EMITIDOS:\n")
            historialTickets.forEach { t ->
                sb.append(" • Ticket #${t.numeroTicket} | ${t.codigoMaquina} | $${String.format("%.2f", t.montoPagado)}\n")
            }

            val recaudacionTotal = historialTickets.sumOf { it.montoPagado }
            val totalMaquinas = historialTickets.size
            val promedio = obtenerIngresoPromedioPorMaquina()

            val totalLavadoras = historialTickets.filter { it.tipoMaquina.contains("Lavadora") }.sumOf { it.montoPagado }
            val totalSecadoras = historialTickets.filter { it.tipoMaquina.contains("Secadora") }.sumOf { it.montoPagado }
            val totalLavasecas = historialTickets.filter { it.tipoMaquina.contains("Lavaseca") }.sumOf { it.montoPagado }

            val tipoMasIngresos = mapOf(
                "Lavadora" to totalLavadoras,
                "Secadora" to totalSecadoras,
                "Lavaseca Industrial" to totalLavasecas
            ).maxByOrNull { it.value }?.key ?: "N/A"

            sb.append("\nESTADÍSTICAS DEL TURNO:\n")
            sb.append(" • Total Recaudado: $${String.format("%.2f", recaudacionTotal)}\n")
            sb.append(" • Cantidad Atenciones: $totalMaquinas\n")
            sb.append(" • Ingreso Promedio: $${String.format("%.2f", promedio)}\n")
            sb.append(" • Más Ingresos por: $tipoMasIngresos\n")
            sb.append(" • Slots Libres al Cierre: ${obtenerCantidadSlotsDisponibles()}\n")
        }

        return sb.toString()
    }
}