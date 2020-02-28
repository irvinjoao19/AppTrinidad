package com.dsige.apptrinidad.data.local.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class RegistroPhoto {
    @PrimaryKey(autoGenerate = true)
    var registroPhotoId: Int = 0
    var registroId: Int = 0
    var rutaFoto: String = ""
    var fechaSincronizacion: String = ""
    var generalId: String = ""
    var flagPrioridad: String = ""
    var latitud: String = ""
    var longitud: String = ""
    var estado: Int = 0
}