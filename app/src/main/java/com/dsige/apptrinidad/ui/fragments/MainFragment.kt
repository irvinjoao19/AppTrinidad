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
import com.dsige.apptrinidad.data.local.model.Registro
import com.dsige.apptrinidad.data.viewModel.RegistroViewModel
import com.dsige.apptrinidad.data.viewModel.ViewModelFactory
import com.dsige.apptrinidad.ui.activities.DetailActivity
import com.dsige.apptrinidad.ui.activities.RegistroActivity
import com.dsige.apptrinidad.ui.adapters.RegistroAdapter
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MainFragment : DaggerFragment(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var registroViewModel: RegistroViewModel

    private var tipo: Int = 0
    private var usuarioId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tipo = it.getInt(ARG_PARAM1)
            usuarioId = it.getString(ARG_PARAM2)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        registroViewModel =
            ViewModelProvider(this, viewModelFactory).get(RegistroViewModel::class.java)

        fab.setOnClickListener(this)

        val registroAdapter = RegistroAdapter(object : OnItemClickListener.RegistroListener {
            override fun onItemClick(r: Registro, view: View, position: Int) {
                startActivity(
                    Intent(context, DetailActivity::class.java)
                        .putExtra("id",r.registroId)
                        .putExtra("obra", r.nroObra)
                        .putExtra("nroPoste", r.nroPoste)
                        .putExtra("tipo", r.tipo)
                        .putExtra("usuarioId", r.usuarioId)
                )
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = registroAdapter

        registroViewModel.getRegistroByTipo(tipo)
            .observe(viewLifecycleOwner, Observer<List<Registro>>{s->
                if (s != null){
                    registroAdapter.addItems(s)
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> startActivity(
                Intent(context, RegistroActivity::class.java)
                    .putExtra("tipo", tipo)
                    .putExtra("usuarioId", usuarioId)
                    .putExtra("id", 0)
                    .putExtra("tipoDetalle", 0)
            )
        }
    }
}