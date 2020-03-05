package com.dsige.apptrinidad.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.data.local.model.RegistroDetalle
import com.dsige.apptrinidad.helper.Util
import com.dsige.apptrinidad.ui.listeners.OnItemClickListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_photo.view.*
import java.io.File

class PhotoPagingAdapter(private var listener: OnItemClickListener.PhotoListener) :
    PagedListAdapter<RegistroDetalle, PhotoPagingAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_photo, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(p: RegistroDetalle, listener: OnItemClickListener.PhotoListener) =
            with(itemView) {
                val url = File(Util.getFolder(itemView.context), p.foto1PuntoAntes)
                Picasso.get()
                    .load(url)
                    .into(imageViewPhoto, object : Callback {
                        override fun onSuccess() {
                            progress.visibility = View.GONE
                            imageViewPhoto.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception) {

                        }
                    })
                itemView.setOnClickListener { v -> listener.onItemClick(p, v, adapterPosition) }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<RegistroDetalle>() {
            override fun areItemsTheSame(oldItem: RegistroDetalle, newItem: RegistroDetalle): Boolean =
                oldItem.detalleId == newItem.detalleId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: RegistroDetalle, newItem: RegistroDetalle
            ): Boolean =
                oldItem == newItem
        }
    }
}