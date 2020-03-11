package com.dsige.apptrinidad.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.Vehiculo
import com.dsige.apptrinidad.data.viewModel.VehiculoViewModel
import com.dsige.apptrinidad.data.viewModel.ViewModelFactory
import com.dsige.apptrinidad.helper.Util
import com.dsige.apptrinidad.ui.adapters.TabLayoutAdapter
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_vehicle_register.*
import kotlinx.android.synthetic.main.content_toolbar.*
import javax.inject.Inject

class VehicleRegisterActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var vehiculoViewModel: VehiculoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_register)

        val b = intent.extras
        if (b != null) {
            bind(b.getString("id")!!)
        }
    }

    private fun bind(placa: String) {
        vehiculoViewModel =
            ViewModelProvider(this, viewModelFactory).get(VehiculoViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Registro"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        vehiculoViewModel.getVehiculo(placa).observe(this, Observer { v ->
            if (v != null) {
                textViewPlaca.text = v.placa
                textViewMarca.text = v.marca
                textViewModelo.text = v.modelo
                textViewYear.text = v.anio
            }
        })

        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab1))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab2))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab3))

        val tabLayoutAdapter =
            TabLayoutAdapter.TabLayoutRegister(supportFragmentManager, tabLayout.tabCount, placa)
        viewPager.adapter = tabLayoutAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                viewPager.currentItem = position
                when (position) {
                    0 -> fab.text = String.format("%s", "Agregar Kilometraje")
                    1 -> fab.text = String.format("%s", "Agregar Vale Combustible")
                }

                fab.setOnClickListener {
                    when (position) {
                        0 -> startActivity(
                            Intent(this@VehicleRegisterActivity, ControlActivity::class.java)
                                .putExtra("id", 0)
                                .putExtra("placa", placa)
                        )
                        1 -> startActivity(
                            Intent(this@VehicleRegisterActivity, RegistroValeActivity::class.java)
                                .putExtra("placa", placa))
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


    }
}