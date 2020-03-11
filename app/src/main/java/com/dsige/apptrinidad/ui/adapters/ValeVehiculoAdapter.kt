package com.dsige.apptrinidad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.VehiculoVales
import com.dsige.apptrinidad.helper.Util
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_vale.view.*

class ValeVehiculoAdapter(private val listener: OnItemClickListener.ValeListener) :
    RecyclerView.Adapter<ValeVehiculoAdapter.ViewHolder>() {

    private var resumen = emptyList<VehiculoVales>()

    fun addItems(list: List<VehiculoVales>) {
        resumen = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_vale, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(resumen[position], listener)
    }

    override fun getItemCount(): Int {
        return resumen.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(c: VehiculoVales, listener: OnItemClickListener.ValeListener) =
            with(itemView) {
//                textView1.setText(
//                    Util.getTextHTML("<font color='#FF5722'>" + c.fecha + "</font>"),
//                    TextView.BufferType.SPANNABLE
//                )
                textView1.text = c.fecha
                textView2.text = c.nombreTipo
                textView3.text = String.format("Nro Voucher : %s", c.nroVale)
                textView4.text = String.format("Total : %.2f", (c.cantidadGalones * c.precioIGV))
                textView5.text = String.format("Galones : %s", c.cantidadGalones)
                textView6.text = String.format("%.2f Km ", c.kmValeCombustible)
                itemView.setOnClickListener { view ->
                    listener.onItemClick(c, view, adapterPosition)
                }
            }
    }
}