package com.dsige.apptrinidad.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.Vehiculo
import com.dsige.apptrinidad.helper.Util
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_vehiculos.view.*
import java.io.File

class VehiculoAdapter(private val listener: OnItemClickListener.VehiculoListener) :
    RecyclerView.Adapter<VehiculoAdapter.ViewHolder>() {

    private var vehiculos = emptyList<Vehiculo>()

    fun addItems(vehiculoList: List<Vehiculo>) {
        vehiculos = vehiculoList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_vehiculos, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(vehiculos[position], listener)
    }

    override fun getItemCount(): Int {
        return vehiculos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(v: Vehiculo, listener: OnItemClickListener.VehiculoListener) = with(itemView) {
//            val f = File(Util.getFolder(itemView.context),v.imagenMarca)
//            Picasso.get().load(f).into(imageViewCar)
            textViewPlaca.text = v.placa
            textViewDescripcion.text = String.format(" %s %s %s", v.marca, v.modelo, v.anio)
            textViewKm.text = v.costo //String.format("KM: %s", v.kmInicial)
            textViewObservacion.text = v.tipoVehiculo
            textViewTipoGasolina.text = v.combustible
            textViewEstado.text = v.condicion

            if (v.estado == 0) {
                imageViewCar.setImageDrawable(
                    itemView.resources.getDrawable(
                        R.drawable.ic_open_lock, itemView.resources.newTheme()
                    )
                )
            }else
                imageViewCar.setImageDrawable(
                    itemView.resources.getDrawable(
                        R.drawable.ic_close_lock, itemView.resources.newTheme()
                    )
                )

//            textViewEstado.setTextColor(Color.parseColor(v.colorEstado))
            itemView.setOnClickListener { view -> listener.onItemClick(v, view, adapterPosition) }
        }
    }
}