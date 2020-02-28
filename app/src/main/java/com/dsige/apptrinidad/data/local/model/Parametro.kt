package com.dsige.apptrinidad.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Parametro {

    @PrimaryKey(autoGenerate = true)
    var configurationId: Int = 0
    var nombre: String = ""
    var valor: Int = 0

}