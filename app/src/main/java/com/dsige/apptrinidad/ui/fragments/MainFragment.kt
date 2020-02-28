package com.dsige.apptrinidad.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.ui.activities.RegistroActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main.*

private const val ARG_PARAM1 = "param1"

class MainFragment : DaggerFragment(), View.OnClickListener {

    private var tipo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tipo = it.getInt(ARG_PARAM1)
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
        fab.setOnClickListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> startActivity(
                Intent(context, RegistroActivity::class.java)
                    .putExtra("tipo", tipo)
            )
        }
    }
}