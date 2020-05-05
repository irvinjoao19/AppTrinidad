package com.dsige.apptrinidad.ui.listeners

import android.view.View
import com.dsige.apptrinidad.data.local.model.*

interface OnItemClickListener {

    interface MenuListener {
        fun onItemClick(m: MenuPrincipal, view: View, position: Int)
    }

    interface ServicesListener {
        fun onItemClick(s: Servicio, view: View, position: Int)
    }

    interface RegistroListener {
        fun onItemClick(r: Registro, view: View, position: Int)
    }

    interface DetalleListener {
        fun onItemClick(r: RegistroDetalle, view: View, position: Int)
    }

    interface PhotoListener {
        fun onItemClick(r: RegistroDetalle, view: View, position: Int)
    }

    interface VehiculoListener {
        fun onItemClick(v: Vehiculo, view: View, position: Int)
    }

    interface ControlListener {
        fun onItemClick(c: VehiculoControl, view: View, position: Int)
    }

    interface ParametroListener {
        fun onItemClick(p: Parametro, view: View, position: Int)
    }

    interface ValeListener {
        fun onItemClick(c: VehiculoVales, view: View, position: Int)
    }

    interface EstadoListener {
        fun onItemClick(c: Estado, view: View, position: Int)
    }
}