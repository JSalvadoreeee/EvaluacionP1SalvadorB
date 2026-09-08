package com.example.evaluacionp1salvadorb

class Slot(val numero: Int) {
    var estado: EstadoSlot = EstadoSlot.LIBRE
        private set

    var maquinaAsignada: Maquina? = null
        private set

    var motivoEstado: String = ""
        private set

    fun asignarMaquina(maquina: Maquina) {
        this.maquinaAsignada = maquina
        this.estado = EstadoSlot.EN_USO
        this.motivoEstado = "Máquina en uso: ${maquina.codigo}"
    }

    fun cambiarACicloFinal(motivo: String) {
        this.estado = EstadoSlot.EN_CICLO_FINAL
        this.motivoEstado = motivo
    }

    fun liberar() {
        this.maquinaAsignada = null
        this.estado = EstadoSlot.LIBRE
        this.motivoEstado = "Disponible"
    }

    fun ponerFueraDeServicio(motivo: String) {
        this.maquinaAsignada = null
        this.estado = EstadoSlot.FUERA_DE_SERVICIO
        this.motivoEstado = motivo
    }
}