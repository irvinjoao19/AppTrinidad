package com.dsige.apptrinidad.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.VehiculoVales
import com.dsige.apptrinidad.data.viewModel.VehiculoViewModel
import com.dsige.apptrinidad.data.viewModel.ViewModelFactory
import com.dsige.apptrinidad.ui.activities.RegistroValeActivity
import com.dsige.apptrinidad.ui.adapters.ValeVehiculoAdapter
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_vehicle_combustible.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"

class VehicleCombustibleFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var vehiculoViewModel: VehiculoViewModel
    private var placa: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            placa = it.getString(ARG_PARAM1)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vehicle_combustible, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        vehiculoViewModel =
            ViewModelProvider(this, viewModelFactory).get(VehiculoViewModel::class.java)
        val controlAdapter = ValeVehiculoAdapter(object : OnItemClickListener.ValeListener {
            override fun onItemClick(c: VehiculoVales, view: View, position: Int) {
                startActivity(
                    Intent(context, RegistroValeActivity::class.java)
                        .putExtra("placa", c.placa)
                        .putExtra("id", c.valeId)
                )
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = controlAdapter
        vehiculoViewModel.getValeVehiculo(placa)
            .observe(viewLifecycleOwner, Observer { v ->
                controlAdapter.addItems(v)
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            VehicleCombustibleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}
