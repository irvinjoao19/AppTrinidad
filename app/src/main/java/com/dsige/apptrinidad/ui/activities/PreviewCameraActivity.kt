package com.dsige.apptrinidad.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.viewModel.RegistroViewModel
import com.dsige.apptrinidad.data.viewModel.ViewModelFactory
import com.dsige.apptrinidad.helper.Util
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_preview_camera.*
import java.io.File
import javax.inject.Inject

class PreviewCameraActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabOk -> registroViewModel.updateFoto(tipo, nameImg, detalleId)
            R.id.fabClose -> {
                startActivity(Intent(this, CameraActivity::class.java))
                finish()
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var registroViewModel: RegistroViewModel

    var nameImg: String = ""
    var registroId: Int = 0
    var detalleId: Int = 0

    var tipo: Int = 0
    var usuarioId: String = ""
    var tipoDetalle: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_camera)
        val b = intent.extras
        if (b != null) {
            nameImg = b.getString("nameImg")!!
            tipo = b.getInt("tipo")
            usuarioId = b.getString("usuarioId")!!
            tipoDetalle = b.getInt("tipoDetalle")
            detalleId = b.getInt("detalleId")
            bindUI(b.getInt("id"), b.getInt("detalleId"))
        }
    }

    private fun bindUI(id: Int, dId: Int) {
        registroViewModel =
            ViewModelProvider(this, viewModelFactory).get(RegistroViewModel::class.java)

        fabClose.setOnClickListener(this)
        fabOk.setOnClickListener(this)
        textViewImg.text = nameImg

        Handler().postDelayed({
            val f = File(Util.getFolder(this), nameImg)
            Picasso.get().load(f)
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        Log.i("TAG", e.toString())
                    }
                })
        }, 200)

        if (id == 0) {
            registroViewModel.getIdentity().observe(this, Observer<Int> { i ->
                registroId = i
            })
        } else {
            registroId = id
        }

        if (dId == 0) {
            registroViewModel.getDetalleIdentity().observe(this, Observer<Int> { i ->
                detalleId = i
            })
        } else {
            detalleId = dId
        }

        registroViewModel.mensajeError.observe(this, Observer<String> { s ->
            if (s != null) {
                Util.toastMensaje(this, s)
            }
        })

        registroViewModel.mensajeSuccess.observe(this, Observer<String> { s ->
            if (s != null) {
                startActivity(
                    Intent(this, RegistroActivity::class.java)
                        .putExtra("tipo", tipo)
                        .putExtra("usuarioId", usuarioId)
                        .putExtra("id", registroId)
                        .putExtra("detalleId", detalleId)
                        .putExtra("tipoDetalle", tipoDetalle)
                )
                finish()
            }
        })
    }
}