package com.dsige.apptrinidad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.apptrinidad.data.local.model.MenuPrincipal

import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_menu.view.*

class MenuAdapter(var layout: Int, var listener: OnItemClickListener.MenuListener) :
        RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private var menus = emptyList<MenuPrincipal>()

    fun addItems(list: List<MenuPrincipal>) {
        menus = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menus[position], listener)
    }

    override fun getItemCount(): Int {
        return menus.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(m: MenuPrincipal, listener: OnItemClickListener.MenuListener) = with(itemView) {
            imageViewPhoto.setImageResource(m.image)
            textViewTitulo.text = m.title
            itemView.setOnClickListener { v -> listener.onItemClick(m, v, adapterPosition) }
        }
    }
}