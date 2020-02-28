package com.dsige.apptrinidad.ui.activities

import android.os.Bundle
import com.dsige.apptrinidad.R
import dagger.android.support.DaggerAppCompatActivity

class PreviewCameraActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_camera)
    }
}
