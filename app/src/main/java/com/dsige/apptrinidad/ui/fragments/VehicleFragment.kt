package com.dsige.apptrinidad.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.Vehiculo
import com.dsige.apptrinidad.data.viewModel.VehiculoViewModel
import com.dsige.apptrinidad.data.viewModel.ViewModelFactory
import com.dsige.apptrinidad.ui.activities.VehicleRegisterActivity
import com.dsige.apptrinidad.ui.adapters.VehiculoAdapter
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_vehicle.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VehicleFragment : DaggerFragment(), OnItemClickListener.VehiculoListener,
    View.OnClickListener {

    override fun onClick(v: View) {
        startActivity(Intent(context, VehicleRegisterActivity::class.java))
//        showPopupMenu(v, context!!)
    }

    override fun onItemClick(v: Vehiculo, view: View, position: Int) {
        startActivity(
            Intent(context, VehicleRegisterActivity::class.java).putExtra("id", v.placa)
        )
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var vehiculoViewModel: VehiculoViewModel

    lateinit var vehiculoAdapter: VehiculoAdapter

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vehicle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vehiculoViewModel =
            ViewModelProvider(this, viewModelFactory).get(VehiculoViewModel::class.java)

        fab.setOnClickListener(this)

        val layoutManager = LinearLayoutManager(context)
        vehiculoAdapter = VehiculoAdapter(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = vehiculoAdapter
        vehiculoViewModel.populateVehiculo()
            .observe(viewLifecycleOwner, Observer { v ->
                vehiculoAdapter.addItems(v)
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VehicleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun showPopupMenu(v: View, context: Context) {
//        val popup = PopupMenu(v.context, v)
//        val inflater = popup.menuInflater
//        inflater.inflate(R.menu.main, popup.menu)
//
//        val menuBuilder = popup.menu as MenuBuilder
//        menuBuilder.setOptionalIconsVisible(true)
//        popup.setOnMenuItemClickListener { item ->
//            when (item.itemId) {
//                R.id.nuevo -> {
//                    startActivity(Intent(context, VehicleRegisterActivity::class.java))
//                    true
//                }
//
//                else -> false
//            }
//        }
//        popup.show()
    }
}