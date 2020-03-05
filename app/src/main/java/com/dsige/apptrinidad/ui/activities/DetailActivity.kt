package com.dsige.apptrinidad.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.RegistroDetalle
import com.dsige.apptrinidad.data.viewModel.RegistroViewModel
import com.dsige.apptrinidad.data.viewModel.ViewModelFactory
import com.dsige.apptrinidad.ui.adapters.DetalleAdapter
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import javax.inject.Inject

class DetailActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabNew -> startActivity(
                Intent(this@DetailActivity, RegistroActivity::class.java)
                    .putExtra("tipo", tipo)
                    .putExtra("usuarioId", usuarioId)
                    .putExtra("id", registroId)
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var registroViewModel: RegistroViewModel

    private var tipo: Int = 0
    private var usuarioId: String = ""
    private var registroId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val b = intent.extras
        if (b != null) {
            tipo = b.getInt("tipo")
            usuarioId = b.getString("usuarioId")!!
            registroId = b.getInt("id")
            bindUI(b.getInt("id"), b.getString("obra")!!, b.getString("nroPoste")!!)
        }
    }

    private fun bindUI(id: Int, obra: String, nroPoste: String) {
        registroViewModel =
            ViewModelProvider(this, viewModelFactory).get(RegistroViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Detalle"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        textView1.text = String.format("Obra : %s", obra)
        textView2.text = String.format("Nro Poste : %s", nroPoste)

        fabCamera.setOnClickListener(this)
        fabNew.setOnClickListener(this)

        val detalleAdapter = DetalleAdapter(object : OnItemClickListener.DetalleListener {
            override fun onItemClick(r: RegistroDetalle, view: View, position: Int) {
                when (view.id) {
                    R.id.buttonAntes -> {
                        startActivity(
                            Intent(this@DetailActivity, RegistroActivity::class.java)
                                .putExtra("tipo", tipo)
                                .putExtra("usuarioId", usuarioId)
                                .putExtra("id", registroId)
                                .putExtra("detalleId", r.detalleId)
                                .putExtra("tipoDetalle", 1)
                        )
                    }
                    R.id.buttonDespues -> {
                        startActivity(
                            Intent(this@DetailActivity, RegistroActivity::class.java)
                                .putExtra("tipo", tipo)
                                .putExtra("usuarioId", usuarioId)
                                .putExtra("id", registroId)
                                .putExtra("detalleId", r.detalleId)
                                .putExtra("tipoDetalle", 2)
                        )
                    }
                }

            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = detalleAdapter

        registroViewModel.getRegistroDetalleById(id)
            .observe(this, Observer<List<RegistroDetalle>> { s ->
                if (s != null) {
                    detalleAdapter.addItems(s)
                }
            })
    }
}