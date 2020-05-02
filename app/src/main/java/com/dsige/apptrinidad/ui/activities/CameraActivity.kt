package com.dsige.apptrinidad.ui.activities

import android.os.Bundle
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.ui.fragments.CameraFragment
import dagger.android.support.DaggerAppCompatActivity

class CameraActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val b = intent.extras
        if (b != null) {
            savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    CameraFragment.newInstance(
                        b.getInt("tipo"),
                        b.getString("usuarioId")!!,
                        b.getInt("id"),
                        b.getInt("tipoDetalle"),
                        b.getInt("detalleId")
                    )
                )
                .commit()
        } else {
            savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    CameraFragment.newInstance(
                        1,
                        "1",
                        1,
                        1,
                        2
                    )
                )
                .commit()
        }
    }
}