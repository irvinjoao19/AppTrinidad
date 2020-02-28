package com.dsige.apptrinidad.data.local.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class Registro {

    @PrimaryKey(autoGenerate = true)
    var registroId: Int = 0
    var suministroId: Int = 0
    var tipoLectura: Int = 0
    var registroFecha: String = ""
    var latitud: String = ""
    var longitud: String = ""
    var registroLectura: String = ""
    var registroConfirmarLectura: String = ""
    var observacion: String = ""
    var grupoIncidenciaCodigo: String = ""
    var registroDns: String = ""
    var registroUbicacion: String = ""
    var registroTieneFoto: String = ""
    var generalId: String = ""
    var operarioId: Int = 0
    var fechaSincronizacion: String = ""
    var registroTipoProceso: String = ""
    var registroConstancia: String = ""
    var registroMedidor: String = ""
    var flagRecepcion: String = ""
    var registroSuministro: String = ""
    var estado: Int = 0
    @Ignore
    var photos: List<RegistroPhoto>? = null
}