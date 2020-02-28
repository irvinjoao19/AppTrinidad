package com.dsige.apptrinidad.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dsige.apptrinidad.data.local.model.Registro
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

class SuministroViewModel @Inject
internal constructor(private val roomRepository: AppRepository) :
    ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getUsuarioActive(): Observable<Int> {
        return roomRepository.getUsuarioActive()
    }

    fun getSizeRegistroByTipo(id: Int): LiveData<Int> {
        return roomRepository.getSizeRegistroByTipo(id)
    }

    fun getSizeRegistroByTipoRx(id: Int): Observable<Int> {
        return roomRepository.getSizeRegistroByTipoRx(id)
    }

    fun getAllRegistroVerificate(valor: Int): LiveData<Int> {
        return roomRepository.getAllRegistroVerificate(valor)
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

    fun validateRegistro(r: Registro, type: Int) {

        if (r.registroLectura.isEmpty()) {
            mensajeError.value = "Digitar Lectura"
            return
        }

        if (r.observacion.isEmpty()) {
            mensajeError.value = "Digitar codigo de observación"
            return
        }

        if (r.observacion == "23") {
            if (r.registroMedidor.isEmpty()) {
                mensajeError.value = "Digitar medidor"
                return
            }
        }

        if (r.registroUbicacion.isEmpty()) {
            mensajeError.value = "Digitar codigo de ubicación"
            return
        }

        insertOrUpdateRegistro(r, type)
    }

    private fun insertOrUpdateRegistro(r: Registro, type: Int) {
        roomRepository.insertOrUpdateRegistro(r, type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    if (type == 1) {
                        mensajeSuccess.value = "Registro Guardado"
                    } else {
                        mensajeSuccess.value = "Tomar Foto"
                    }
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun getRegistroBySuministro(suministroId: Int): LiveData<Registro> {
        return roomRepository.getRegistroBySuministro(suministroId)
    }

    fun getRegistroBySuministroTask(suministroId: Int): Observable<Registro> {
        return roomRepository.getRegistroBySuministroTask(suministroId)
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
}