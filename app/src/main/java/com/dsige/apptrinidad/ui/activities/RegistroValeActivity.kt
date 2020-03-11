package com.dsige.apptrinidad.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.Parametro
import com.dsige.apptrinidad.data.local.model.VehiculoVales
import com.dsige.apptrinidad.data.viewModel.VehiculoViewModel
import com.dsige.apptrinidad.data.viewModel.ViewModelFactory
import com.dsige.apptrinidad.helper.Util
import com.dsige.apptrinidad.ui.adapters.ComboAdapter
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_registro_vale.*
import java.io.File
import javax.inject.Inject

class RegistroValeActivity : DaggerAppCompatActivity(), View.OnClickListener, TextWatcher {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextFecha -> Util.getDateDialog(this, editTextFecha)
            R.id.editTextTipoCombustible -> spinnerDialog(2, "Tipo de Combustible")
            R.id.editTextTipoGrifo -> spinnerDialog(1, "Tipo de Grifo")
            R.id.imageViewFoto -> startActivityForResult(
                Intent(this, CameraActivity::class.java)
                    .putExtra("tipo", 0)
                    .putExtra("usuarioId", placa)
                , 1
            )
            R.id.fabSave -> formRegistro()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var vehiculoViewModel: VehiculoViewModel

    lateinit var c: VehiculoVales
    private var placa: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_vale)
        val b = intent.extras
        if (b != null) {
            placa = b.getString("placa")!!
            c = VehiculoVales()
            bindUI(b.getInt("id"))
        }
    }

    private fun bindUI(id: Int) {
        vehiculoViewModel =
            ViewModelProvider(this, viewModelFactory).get(VehiculoViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Registro Vale de Combustible"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        editTextFecha.setOnClickListener(this)
        editTextTipoCombustible.setOnClickListener(this)
        editTextTipoGrifo.setOnClickListener(this)
        imageViewFoto.setOnClickListener(this)
        fabSave.setOnClickListener(this)
        editTextFecha.setText(Util.getFecha())
        editTextVoucher.setText(placa)
        editTextCantidad.addTextChangedListener(this)
        editTextPrecio.addTextChangedListener(this)

        vehiculoViewModel.getValeVehiculoById(id).observe(this, Observer { v ->
            if (v != null) {
                c = v
                editTextFecha.setText(v.fecha)
                editTextVoucher.setText(v.nroVale)
                editTextTipoCombustible.setText(v.nombreTipo)
                editTextCantidad.setText(v.cantidadGalones.toString())
                editTextPrecio.setText(v.precioIGV.toString())
                editTextTotal.setText((v.cantidadGalones * v.precioIGV).toString())
                editTextKm.setText(v.kmValeCombustible.toString())
                editTextTipoGrifo.setText(v.nombreGrifo)
                val f = File(Util.getFolder(this), c.foto)
                Picasso.get().load(f).into(imageView)
                fabSave.visibility = View.VISIBLE
            }
        })

        vehiculoViewModel.mensajeError.observe(this, Observer { s ->
            if (s != null) {
                Util.toastMensaje(this, s)
            }
        })
        vehiculoViewModel.mensajeSuccess.observe(this, Observer { s ->
            if (s != null) {
                finish()
            }
        })
    }

    private fun spinnerDialog(tipo: Int, name: String) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(this).inflate(R.layout.dialog_combo, null)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context, DividerItemDecoration.VERTICAL
            )
        )
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        textViewTitulo.text = String.format(name)

        val comboAdapter = ComboAdapter(object : OnItemClickListener.ParametroListener {
            override fun onItemClick(p: Parametro, view: View, position: Int) {
                when (tipo) {
                    1 -> {
                        c.rucGrifo = p.campo1
                        editTextTipoGrifo.setText(p.campo2)
                    }
                    2 -> {
                        c.tipo = p.campo1
                        editTextTipoCombustible.setText(p.campo2)
                    }
                }
                dialog.dismiss()
            }
        })
        recyclerView.adapter = comboAdapter
        vehiculoViewModel.getComboByTipo(tipo).observe(this, Observer { p ->
            if (p != null) {
                comboAdapter.addItems(p)
            }
        })
    }

    override fun afterTextChanged(p0: Editable?) {
        val a = when {
            editTextCantidad.text.toString().isEmpty() -> 0.0
            else -> editTextCantidad.text.toString().toDouble()
        }
        val b = when {
            editTextPrecio.text.toString().isEmpty() -> 0.0
            else -> editTextPrecio.text.toString().toDouble()
        }

        val result = a * b
        editTextTotal.setText(String.format("%.2f", result))
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    private fun formRegistro() {
        c.placa = placa
        c.nroVale = editTextVoucher.text.toString()
        c.fecha = Util.getFecha()
        c.nombreGrifo = editTextTipoGrifo.text.toString()
        c.nombreTipo = editTextTipoCombustible.text.toString()

        when {
            editTextPrecio.text.toString().isEmpty() -> c.precioIGV = 0.0
            else -> c.precioIGV = editTextPrecio.text.toString().toDouble()
        }
        when {
            editTextCantidad.text.toString().isEmpty() -> c.cantidadGalones = 0.0
            else -> c.cantidadGalones = editTextCantidad.text.toString().toDouble()
        }
        when {
            editTextKm.text.toString().isEmpty() -> c.kmValeCombustible = 0.0
            else -> c.kmValeCombustible = editTextKm.text.toString().toDouble()
        }
        vehiculoViewModel.validateVales(c)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra("img")!!
                if (result.isNotEmpty()) {
                    val f = File(Util.getFolder(this), result)
                    Picasso.get().load(f).into(imageView)
                    c.foto = result
                    fabSave.visibility = View.VISIBLE
                }
            }
        }
    }
}