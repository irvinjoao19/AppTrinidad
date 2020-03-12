package com.dsige.apptrinidad.ui.activities

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.VehiculoControl
import com.dsige.apptrinidad.data.viewModel.VehiculoViewModel
import com.dsige.apptrinidad.data.viewModel.ViewModelFactory
import com.dsige.apptrinidad.helper.Util
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_control.*
import javax.inject.Inject

class ControlActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabSave -> formControl()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var vehiculoViewModel: VehiculoViewModel

    lateinit var c: VehiculoControl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        val b = intent.extras
        if (b != null) {
            bindUI(b.getInt("id"), b.getString("placa")!!)
        }
    }

    private fun bindUI(controlId: Int, placa: String) {
        vehiculoViewModel =
            ViewModelProvider(this, viewModelFactory).get(VehiculoViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = if (controlId == 0) "Salida de la Base" else "Ingreso a la Base"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        fabSave.setOnClickListener(this)

        c = VehiculoControl()
        c.controlId = controlId
        c.placa = placa
        c.fecha = Util.getFecha()

        vehiculoViewModel.getControVehiculoById(controlId)
            .observe(this, Observer { v ->
                if (v != null) {
                    c = v
                }
            })

        vehiculoViewModel.mensajeError.observe(this, Observer { v ->
            if (v != null) {
                Util.toastMensaje(this, v)
            }
        })

        vehiculoViewModel.mensajeSuccess.observe(this, Observer { v ->
            if (v != null) {
                finish()
            }
        })
    }

    private fun formControl() {
        when (c.controlId) {
            0 -> when {
                editTextKm.text.toString().isEmpty() -> c.kmSalida = 0.0
                else -> c.kmSalida = editTextKm.text.toString().toDouble()
            }
            else -> when {
                editTextKm.text.toString().isEmpty() -> c.kmIngreso = 0.0
                else -> {
                    c.kmIngreso = editTextKm.text.toString().toDouble()
                }
            }
        }
        vehiculoViewModel.validateControl(c)
    }
}
