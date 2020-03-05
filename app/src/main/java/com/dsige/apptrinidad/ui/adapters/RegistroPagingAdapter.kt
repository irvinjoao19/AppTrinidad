package com.dsige.apptrinidad.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.Registro
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_registro.view.*

class RegistroPagingAdapter(private var listener: OnItemClickListener.RegistroListener) :
    PagedListAdapter<Registro, RegistroPagingAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_registro, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(r: Registro, listener: OnItemClickListener.RegistroListener) =
            with(itemView) {
                textViewObra.text = r.nroObra
                textViewNroPoste.text = r.nroPoste
                textViewEstado.text = r.estado
                itemView.setOnClickListener { v -> listener.onItemClick(r, v, adapterPosition) }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Registro>() {
            override fun areItemsTheSame(oldItem: Registro, newItem: Registro): Boolean =
                oldItem.registroId == newItem.registroId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Registro, newItem: Registro): Boolean =
                oldItem == newItem
        }
    }
}