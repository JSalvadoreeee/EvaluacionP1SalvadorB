package com.example.evaluacionp1salvadorb

import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var textViewLog: TextView
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scrollView = ScrollView(this)
        textViewLog = TextView(this).apply {
            textSize = 13f
            setPadding(32, 32, 32, 32)
            text = "INICIANDO SISTEMA LAVEXPRESS\n"
        }
        scrollView.addView(textViewLog)
        setContentView(scrollView)

        lifecycleScope.launch {
            ejecutarSimulacion()
        }
    }

    private fun log(mensaje: String) {
        runOnUiThread {
            textViewLog.append("$mensaje\n")
            scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        }
    }

    private suspend fun ejecutarSimulacion() {
        val sistema = SistemaLavanderia("LavExpress", capacidad = 10)

        log("\n PRUEBA DE MANEJO DE ERRORES (R6)")
        try {
            log("Intentando código inválido '123ABC'...")
            val maquinaErronea = Lavadora("123ABC", "Generic", TipoUsuario.REGULAR)
            sistema.registrarEntrada(maquinaErronea) { log(it) }
        } catch (e: Exception) {
            log("Capturado (Controlado): ${e.message}")
        }

        try {
            log("Intentando salida de código inexistente 'XX99XX'...")
            sistema.registrarSalida("XX99XX", 60) { log(it) }
        } catch (e: Exception) {
            log("Capturado (Controlado): ${e.message}")
        }

        log("\nREGISTRO DE ENTRADAS (R5)")
        val m1 = Lavadora("LV12CD", "Samsung WW90", TipoUsuario.SUSCRIPTOR)
        val m2 = Lavadora("LV99ZA", "LG F4WV509", TipoUsuario.REGULAR)
        val m3 = Secadora("SC22TO", "Bosch WTH85200", TipoUsuario.REGULAR)
        val m4 = LavasecaIndustrial("LI44RG", "Miele PW6", TipoUsuario.EMPRESA, conVapor = true)
        val m5 = LavasecaIndustrial("LI77RG", "Speed Queen SF7", TipoUsuario.REGULAR, conVapor = false)

        sistema.registrarEntrada(m1) { log(it) }
        sistema.registrarEntrada(m2) { log(it) }
        sistema.registrarEntrada(m3) { log(it) }
        sistema.registrarEntrada(m4) { log(it) }
        sistema.registrarEntrada(m5) { log(it) }

        log("\nREGISTRO DE SALIDAS Y EMISIÓN DE TICKETS")
        sistema.registrarSalida("LV12CD", 75) { log(it) }
        sistema.registrarSalida("LV99ZA", 180) { log(it) }
        sistema.registrarSalida("SC22TO", 25) { log(it) }
        sistema.registrarSalida("LI44RG", 120) { log(it) }
        sistema.registrarSalida("LI77RG", 45) { log(it) }

        log("\nCONSULTAS DE NEGOCIO (R4)")
        log("1. Slots disponibles: ${sistema.obtenerCantidadSlotsDisponibles()}")
        log("2. Suscriptores: ${sistema.obtenerMaquinasClientesSuscriptores().map { it.codigo }}")
        log("3. Ingreso promedio: $${String.format("%.2f", sistema.obtenerIngresoPromedioPorMaquina())}")
        log("4. Finalizadas: ${sistema.obtenerCodigosMaquinasFinalizadas()}")
        log("5. Mayor tiempo de uso: ${sistema.obtenerMaquinaConMasTiempoUso()}")

        log(sistema.generarReporteCierre())
    }
}