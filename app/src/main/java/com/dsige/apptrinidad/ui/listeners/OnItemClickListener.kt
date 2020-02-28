package com.dsige.apptrinidad.ui.listeners

import android.view.View
import com.dsige.apptrinidad.data.local.model.MenuPrincipal
import com.dsige.apptrinidad.data.local.model.Registro
import com.dsige.apptrinidad.data.local.model.Servicio

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
}