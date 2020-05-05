package com.dsige.apptrinidad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.Estado

import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_combo.view.*

class EstadoAdapter(var listener: OnItemClickListener.EstadoListener) :
    RecyclerView.Adapter<EstadoAdapter.ViewHolder>() {

    private var states = emptyList<Estado>()

    fun addItems(list: List<Estado>) {
        states = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(states[position], listener)
    }

    override fun getItemCount(): Int {
        return states.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(p: Estado, listener: OnItemClickListener.EstadoListener) = with(itemView) {
            textViewNombre.text = p.nombre
            itemView.setOnClickListener { v -> listener.onItemClick(p, v, adapterPosition) }
        }
    }
}