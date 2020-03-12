package com.dsige.apptrinidad.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.system.Os.close
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
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
import com.dsige.apptrinidad.helper.Util
import com.dsige.apptrinidad.ui.activities.VehicleRegisterActivity
import com.dsige.apptrinidad.ui.adapters.VehiculoAdapter
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_vehicle.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VehicleFragment : DaggerFragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var vehiculoViewModel: VehiculoViewModel

    lateinit var vehiculoAdapter: VehiculoAdapter

    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null


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

        val layoutManager = LinearLayoutManager(context)
        vehiculoAdapter = VehiculoAdapter(object : OnItemClickListener.VehiculoListener {
            override fun onItemClick(v: Vehiculo, view: View, position: Int) {
                val popupMenu = PopupMenu(context!!, view)
                popupMenu.menu.add(1, 1, 1, getText(R.string.close))
                popupMenu.menu.add(2, 2, 2, getText(R.string.ver))
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        1 -> {
                            load()
                            vehiculoViewModel.closeVerificationVehiculo(v.placa)
                        }
                        2 -> {
                            startActivity(
                                Intent(context, VehicleRegisterActivity::class.java).putExtra(
                                    "id",
                                    v.placa
                                )
                            )
                        }
                    }
                    false
                }
                popupMenu.show()
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = vehiculoAdapter
        vehiculoViewModel.populateVehiculo()
            .observe(viewLifecycleOwner, Observer { v ->
                vehiculoAdapter.addItems(v)
            })

        vehiculoViewModel.mensajeSuccess.observe(viewLifecycleOwner, Observer { s ->
            if (s != null) {
                closeLoad()
                Util.toastMensaje(context!!, s)
            }
        })

        vehiculoViewModel.mensajeError.observe(viewLifecycleOwner, Observer { s ->
            if (s != null) {
                closeLoad()
                Util.dialogMensaje(context!!, "Mensaje", s)
            }
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

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textView)
        textViewTitle.text = String.format("%s", "Verificando")
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun closeLoad() {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }
}