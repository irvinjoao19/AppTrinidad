package com.dsige.apptrinidad.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Usuario {

    @PrimaryKey
    var usuarioId: String = ""
    var nombre : String = ""
    var dni : String = ""
}