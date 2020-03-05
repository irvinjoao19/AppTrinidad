package com.dsige.apptrinidad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.RegistroDetalle
import com.dsige.apptrinidad.helper.Util
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_registro_detalle.view.*

class DetalleAdapter(private var listener: OnItemClickListener.DetalleListener) :
    RecyclerView.Adapter<DetalleAdapter.ViewHolder>() {

    private var registros = emptyList<RegistroDetalle>()

    fun addItems(list: List<RegistroDetalle>) {
        registros = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(registros[position], listener)
    }

    override fun getItemCount(): Int {
        return registros.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_registro_detalle, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(r: RegistroDetalle, listener: OnItemClickListener.DetalleListener) =
            with(itemView) {
                textViewPunto.text = r.nombrePunto
                textViewFecha.text = Util.getFecha()
                buttonAntes.setOnClickListener { v -> listener.onItemClick(r, v, adapterPosition) }
                buttonDespues.setOnClickListener { v -> listener.onItemClick(r, v, adapterPosition) }
            }
    }
}