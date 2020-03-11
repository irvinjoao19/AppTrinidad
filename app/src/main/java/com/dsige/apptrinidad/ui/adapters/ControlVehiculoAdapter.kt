package com.dsige.apptrinidad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.VehiculoControl
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_control.view.*

class ControlVehiculoAdapter(private val listener: OnItemClickListener.ControlListener) :
    RecyclerView.Adapter<ControlVehiculoAdapter.ViewHolder>() {

    private var resumen = emptyList<VehiculoControl>()

    fun addItems(list: List<VehiculoControl>) {
        resumen = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_control, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(resumen[position], listener)
    }

    override fun getItemCount(): Int {
        return resumen.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(c: VehiculoControl, listener: OnItemClickListener.ControlListener) =
            with(itemView) {
                textView1.text = String.format("Atención : %s", c.fecha)
                textViewSalida.text = String.format("%.2f Km", c.kmSalida)
                textViewEntrada.text = String.format("%.2f Km", c.kmIngreso)
                val total = (c.kmIngreso - c.kmSalida).toInt()
                gaugeKm.value = total
                textViewNameKm.text = total.toString()
                itemView.setOnClickListener { view ->
                    listener.onItemClick(c, view, adapterPosition)
                }
            }
    }
}