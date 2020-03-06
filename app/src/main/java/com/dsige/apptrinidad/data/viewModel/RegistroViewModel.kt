package com.dsige.apptrinidad.data.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.dsige.apptrinidad.data.local.model.MenuPrincipal
import com.dsige.apptrinidad.data.local.model.Registro
import com.dsige.apptrinidad.data.local.model.RegistroDetalle
import com.dsige.apptrinidad.data.local.repository.AppRepository
import com.dsige.apptrinidad.helper.Util
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.NumberFormatException
import java.text.DecimalFormat
import javax.inject.Inject

class RegistroViewModel @Inject
internal constructor(private val roomRepository: AppRepository) :
    ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getValidateRango(lectura: String, min: String, max: String): Boolean {
        return try {
            if (min.isNotEmpty() || max.isNotEmpty()) {
                min.toDouble() < lectura.toInt() && max.toDouble() > lectura.toInt()
            } else {
                false
            }
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun getLecturaTotal(lectura: String, anterior: String): String {
        try {
            val l = lectura.toDouble() - anterior.toDouble()
            val valor: Double = DecimalFormat("0.0").format(l).toDouble()
            if (valor >= (-1) && valor <= (-0.1)) {
                return anterior
            }
            return lectura
        } catch (e: NumberFormatException) {
            return lectura
        }
    }

    fun getConsumo(lectura: String, anterior: String): Double {
        return lectura.toDouble() - anterior.toDouble()
    }

    fun validateRegistro(r: Registro, id: Int, tipo: String) {

        if (r.nroObra.isEmpty()) {
            mensajeError.value = "Digitar Obra"
            return
        }

        if (r.nroPoste.isEmpty()) {
            mensajeError.value = "Digitar nro Poste."
            return
        }

        if (r.tipo == 1) {
            if (r.detalles!!.nombrePunto.isEmpty()) {
                mensajeError.value = "Digitar Nombre de Punto"
                return
            }

            if (r.detalles!!.largo == 0.0) {
                mensajeError.value = "Digitar Nombre de Punto"
                return
            }

            if (r.detalles!!.ancho == 0.0) {
                mensajeError.value = "Digitar Nombre de Punto"
                return
            }
        }

        insertOrUpdateRegistro(r, id, tipo)
    }

    private fun insertOrUpdateRegistro(r: Registro, id: Int, tipo: String) {
        roomRepository.insertOrUpdateRegistro(r, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeSuccess.value = tipo

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    private fun generateImage(direction: String) {
        val image: Observable<Boolean> = Util.generateImageAsync(direction)
        image.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Boolean> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Boolean) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun getIdentity(): LiveData<Int> {
        return roomRepository.getIdentity()
    }

    fun getDetalleIdentity(): LiveData<Int> {
        return roomRepository.getDetalleIdentity()
    }

    fun getRegistroPhotoById(id: Int): LiveData<PagedList<RegistroDetalle>> {
        return roomRepository.getRegistroPhotoById(id)
    }

    fun getRegistroById(id: Int): LiveData<Registro> {
        return roomRepository.getRegistroById(id)
    }

    fun getRegistroDetalle(tipo: Int, id: Int): LiveData<RegistroDetalle> {
        return roomRepository.getRegistroDetalle(tipo, id)
    }

    fun deleteGaleria(d: MenuPrincipal, c: Context) {
        roomRepository.deleteGaleria(d, c)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    mensajeSuccess.value = "Eliminado"
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun updateFoto(tipo: Int, name: String, detalleId: Int, id: Int) {
        roomRepository.updatePhoto(tipo, name, detalleId, id)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    mensajeSuccess.value = "Ok"
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }


    fun getRegistroByTipo(tipo: Int): LiveData<List<Registro>> {
        return roomRepository.getRegistroByTipo(tipo)
    }

    fun getRegistroPagingByTipo(tipo: Int): LiveData<PagedList<Registro>> {
        return roomRepository.getRegistroPagingByTipo(tipo)
    }

    fun getRegistroByObra(o: String): LiveData<List<Registro>> {
        return roomRepository.getRegistroByObra(o)
    }

    fun getRegistroDetalleById(id: Int): LiveData<List<RegistroDetalle>> {
        return roomRepository.getRegistroDetalleById(id)
    }
}