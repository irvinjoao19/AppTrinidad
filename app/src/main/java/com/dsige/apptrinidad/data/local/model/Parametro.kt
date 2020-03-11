package com.dsige.apptrinidad.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Parametro {

    @PrimaryKey
    var campo1: String = ""
    var tipo: Int = 0 //1 -> grifo 2-> combustible
    var campo2: String = ""

}