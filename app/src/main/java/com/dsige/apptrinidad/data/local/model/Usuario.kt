package com.dsige.apptrinidad.data.local.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Usuario {

    @PrimaryKey(autoGenerate = true)
    var operarioId: Int = 0
    var operarioLogin: String = ""
    var operarioPass: String = ""
    var nombre: String = ""
    var envioEnLinea: Int = 0
    var tipoUsuario: String = ""
    var estado: String = ""
    var estadoSesion: Int = 0
    var tiempoEnvio: Int = 0
    var deviceId: String = ""
    var flagDevice: Int = 0
    var flagLimpiarHistorial: Int = 0
    var activeInspeccion: Int = 0
}