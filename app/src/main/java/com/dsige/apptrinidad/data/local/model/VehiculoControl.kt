package com.dsige.apptrinidad.data.local.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class VehiculoControl {

    @PrimaryKey(autoGenerate = true)
    var controlId: Int = 0
    var placa: String = ""
    var fecha: String = ""
    var kmIngreso: Double = 0.0
    var kmSalida: Double = 0.0
    var choferDni: String = ""
    var gesCodigo: String = ""
    var pubCodigo: String = ""
}