package com.dsige.apptrinidad.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.MenuPrincipal
import com.dsige.apptrinidad.data.local.model.Registro
import com.dsige.apptrinidad.data.local.model.RegistroDetalle
import com.dsige.apptrinidad.data.viewModel.*
import com.dsige.apptrinidad.data.viewModel.ViewModelFactory
import com.dsige.apptrinidad.helper.Util
import com.dsige.apptrinidad.ui.adapters.PhotoAdapter
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_registro.*
import javax.inject.Inject

class RegistroActivity : DaggerAppCompatActivity(), View.OnClickListener, TextWatcher {

    override fun onClick(v: View) {
        if (!verificacion) {
            formRegistro()
            return
        }

        if (count == 3) {
            Util.toastMensaje(this, "Maximo 3 fotos")
            return
        }

        when (v.id) {
            R.id.fabCamara -> goCamera()
            R.id.fabGaleria -> {
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var registroViewModel: RegistroViewModel

    lateinit var r: Registro
    private var verificacion: Boolean = false
    private var detalleId: Int = 0
    private var registroId: Int = 0
    private var tipoDetalle: Int = 0

    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        val b = intent.extras
        if (b != null) {
            r = Registro()
            r.tipo = b.getInt("tipo")
            r.usuarioId = b.getString("usuarioId")!!
            registroId = b.getInt("id")
            detalleId = b.getInt("detalleId")
            tipoDetalle = b.getInt("tipoDetalle")
            bindUI(b.getInt("tipo"), b.getInt("id"), b.getInt("detalleId"), b.getInt("tipoDetalle"))
        }
    }

    private fun bindUI(tipo: Int, id: Int, detalleId: Int, tipoDetalle: Int) {
        registroViewModel =
            ViewModelProvider(this, viewModelFactory).get(RegistroViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Registro"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        fabCamara.setOnClickListener(this)
        fabGaleria.setOnClickListener(this)

        editTextAncho.addTextChangedListener(this)
        editTextLargo.addTextChangedListener(this)

        if (tipo == 2) {
            layoutReparacion.visibility = View.GONE
        }

        if (tipoDetalle != 0) {
            checkbox.isChecked = tipoDetalle != 1
            checkbox.isEnabled = false

            editTextObra.isEnabled = false
            editTextPoste.isEnabled = false
            editTextAncho.isEnabled = false
            editTextLargo.isEnabled = false
            editTextNombrePunto.isEnabled = false
            editTextM3.isEnabled = false
        }

        val photoAdapter = PhotoAdapter(object : OnItemClickListener.MenuListener {
            override fun onItemClick(m: MenuPrincipal, view: View, position: Int) {
                val popupMenu = PopupMenu(this@RegistroActivity, view)
                popupMenu.menu.add(1, 1, 1, getText(R.string.delete))
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        1 -> deleteConfirmation(m)
                    }
                    false
                }
                popupMenu.show()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        val layoutManager = StaggeredGridLayoutManager(2, 1)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = photoAdapter

        registroViewModel.getRegistroById(id).observe(this, Observer<Registro> { re ->
            if (re != null) {
                r = re
                editTextObra.setText(re.nroObra)
                editTextPoste.setText(re.nroPoste)
            }
        })

        registroViewModel.getRegistroDetalle(detalleId)
            .observe(this, Observer<RegistroDetalle> { d ->
                if (d != null) {
                    editTextNombrePunto.setText(d.nombrePunto)
                    editTextLargo.setText(d.largo.toString())
                    editTextAncho.setText(d.ancho.toString())
                    editTextM3.setText(d.totalM3.toString())
                    count = 0
                    if (tipoDetalle == 1) {
                        val a = ArrayList<MenuPrincipal>()
                        if (d.foto1PuntoAntes.isNotEmpty()) {
                            a.add(MenuPrincipal(d.detalleId, d.foto1PuntoAntes, 1))
                            count++
                        }
                        if (d.foto2PuntoAntes.isNotEmpty()) {
                            a.add(MenuPrincipal(d.detalleId, d.foto2PuntoAntes, 2))
                            count++
                        }
                        if (d.foto3PuntoAntes.isNotEmpty()) {
                            a.add(MenuPrincipal(d.detalleId, d.foto3PuntoAntes, 3))
                            count++
                        }
                        photoAdapter.addItems(a)
                    } else {
                        val a = ArrayList<MenuPrincipal>()
                        if (d.foto1PuntoDespues.isNotEmpty()) {
                            a.add(MenuPrincipal(d.detalleId, d.foto1PuntoDespues, 4))
                            count++
                        }
                        if (d.foto2PuntoDespues.isNotEmpty()) {
                            a.add(MenuPrincipal(d.detalleId, d.foto2PuntoDespues, 5))
                            count++
                        }
                        if (d.foto3PuntoDespues.isNotEmpty()) {
                            a.add(MenuPrincipal(d.detalleId, d.foto3PuntoDespues, 6))
                            count++
                        }
                        photoAdapter.addItems(a)
                    }
                }
            })

        registroViewModel.mensajeSuccess.observe(this, Observer<String> { s ->
            if (s != null) {
                if (s != "Eliminado") {
                    goCamera()
                }
                verificacion = true
            }
        })

        registroViewModel.mensajeError.observe(this, Observer<String> { s ->
            if (s != null) {
                verificacion = false
                Util.toastMensaje(this, s)
            }
        })
    }

    private fun formRegistro() {
        r.nroObra = editTextObra.text.toString()
        r.nroPoste = editTextPoste.text.toString()
        r.fecha = Util.getFecha()
        r.punto = editTextNombrePunto.text.toString().trim()

        val d = RegistroDetalle()
        d.nombrePunto = editTextNombrePunto.text.toString().trim()
        d.tipo = if (checkbox.isChecked) 2 else 1
        d.registroId = registroId
        d.detalleId  = detalleId

        when {
            editTextAncho.text.toString().isEmpty() -> d.ancho = 0.0
            else -> d.ancho = editTextAncho.text.toString().toDouble()
        }
        when {
            editTextLargo.text.toString().isEmpty() -> d.largo = 0.0
            else -> d.largo = editTextLargo.text.toString().toDouble()
        }
        when {
            editTextM3.text.toString().isEmpty() -> d.totalM3 = 0.0
            else -> d.totalM3 = editTextM3.text.toString().toDouble()
        }
        r.detalles = d
        registroViewModel.validateRegistro(r, detalleId)
    }

    private fun deleteConfirmation(d: MenuPrincipal) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Deseas Eliminar la foto ?")
            .setPositiveButton("SI") { dialog, _ ->
                registroViewModel.deleteGaleria(d, this)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }

    private fun goCamera() {
        startActivity(
            Intent(this, CameraActivity::class.java)
                .putExtra("tipo", r.tipo)
                .putExtra("usuarioId", r.usuarioId)
                .putExtra("id", r.usuarioId)
                .putExtra("detalleId", detalleId)
                .putExtra("tipoDetalle", if(tipoDetalle == 0) 1 else tipoDetalle)
        )
        finish()
    }

    override fun afterTextChanged(p0: Editable?) {
        val a = when {
            editTextAncho.text.toString().isEmpty() -> 0.0
            else -> editTextAncho.text.toString().toDouble()
        }
        val b = when {
            editTextLargo.text.toString().isEmpty() -> 0.0
            else -> editTextLargo.text.toString().toDouble()
        }

        val result = a * b
        editTextM3.setText(String.format("%.2f", result))
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }
}