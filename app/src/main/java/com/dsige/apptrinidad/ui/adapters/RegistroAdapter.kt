package com.dsige.apptrinidad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.Registro
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_registro.view.*

class RegistroAdapter(private var listener: OnItemClickListener.RegistroListener) :
    RecyclerView.Adapter<RegistroAdapter.ViewHolder>() {

    private var registros = emptyList<Registro>()

    fun addItems(list: List<Registro>) {
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
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_registro, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(r: Registro, listener: OnItemClickListener.RegistroListener) =
            with(itemView) {
                textViewObra.text = String.format("Obra : %s", r.nroObra)
                textViewNroPoste.text = String.format("Nro Poste : %s", r.nroPoste)
                if (r.tipo == 2) {
                    textViewEstado.text = r.fecha
                } else {
                    textViewEstado.text = r.estado
                }
                itemView.setOnClickListener { v -> listener.onItemClick(r, v, adapterPosition) }
            }
    }
}