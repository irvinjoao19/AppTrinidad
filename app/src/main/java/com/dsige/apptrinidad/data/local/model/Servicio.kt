package com.dsige.apptrinidad.data.local.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Servicio {

    @PrimaryKey(autoGenerate = true)
    var servicioId: Int = 0
    var nombre: String = ""
    var estado: Int = 0
    var orden: Int = 0
    var size: Int = 0
}