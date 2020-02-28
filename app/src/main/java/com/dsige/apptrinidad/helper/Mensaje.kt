package com.dsige.apptrinidad.helper

import com.dsige.apptrinidad.helper.MensajeDetalle

open class Mensaje {

    var codigo: Int = 0
    var codigoBase: Int = 0
    var codigoRetorno: Int = 0
    var mensaje: String = ""
    var detalle: List<MensajeDetalle>? = null
}