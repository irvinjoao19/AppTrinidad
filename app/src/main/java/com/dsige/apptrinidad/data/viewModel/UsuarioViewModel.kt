package com.dsige.apptrinidad.data.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dsige.apptrinidad.data.local.model.Registro
import com.dsige.apptrinidad.data.local.model.RegistroDetalle
import com.dsige.apptrinidad.data.local.model.Usuario
import com.dsige.apptrinidad.data.local.repository.ApiError
import com.dsige.apptrinidad.data.local.repository.AppRepository
import com.dsige.apptrinidad.helper.Mensaje
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UsuarioViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    private val mensajeError = MutableLiveData<String>()
    private val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    val success: LiveData<String>
        get() = mensajeSuccess

    val error: LiveData<String>
        get() = mensajeError

    val user: LiveData<Usuario>
        get() = roomRepository.getUsuario()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getLogin(usuario: String, pass: String, imei: String, version: String) {
        roomRepository.getUsuarioService(usuario, pass, imei, version)
            .delay(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Usuario> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(usuario: Usuario) {
                    insertUsuario(usuario)
                }

                override fun onError(t: Throwable) {
                    if (t is HttpException) {
                        val body = t.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error.Message)
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                        }
                    } else {
                        mensajeError.postValue(t.message)
                    }
                }

                override fun onComplete() {
                }
            })
    }

    fun insertUsuario(u: Usuario) {
        roomRepository.insertUsuario(u)
            .delay(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    mensajeSuccess.value = "Logeado"
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun logout() {
        roomRepository.deleteUsuario()
            .delay(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    mensajeSuccess.value = "Close"
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun getSizeRegistro(): LiveData<Int> {
        return roomRepository.getSizeRegistro()
    }

    fun sendData() {
        val auditorias: Observable<List<Registro>> = roomRepository.getDataRegistro(1)
        auditorias.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->
                val b = MultipartBody.Builder()
//                val detalles: List<RegistroDetalle>? = a.detalles
//                if (detalles != null) {
//                    for (p: RegistroDetalle in detalles) {
//                        if (p.fot.isNotEmpty()) {
//                            val file =
//                                File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + p.rutaFoto)
//                            if (file.exists()) {
//                                b.addFormDataPart(
//                                    "fotos",
//                                    file.name,
//                                    RequestBody.create(MediaType.parse("multipart/form-data"), file)
//                                )
//                            }
//                        }
//                    }
//                }

                val json = Gson().toJson(a)
                Log.i("TAG", json)
                b.setType(MultipartBody.FORM)
                b.addFormDataPart("model", json)

                val body = b.build()
                Observable.zip(
                    Observable.just(a), roomRepository.saveData(body),
                    BiFunction<Registro, Mensaje, Mensaje> { _, mensaje ->
                        mensaje
                    })
            }
        }.subscribeOn(Schedulers.io())
            .delay(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {

                override fun onSubscribe(d: Disposable) {
                    Log.i("TAG", d.toString())
                }

                override fun onNext(m: Mensaje) {
                    Log.i("TAG", "RECIBIENDO LOS DATOS")
                    updateRegistro(m)
                }

                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        val body = e.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error.Message)
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                            Log.i("TAG", e1.toString())
                        }
                    } else {
                        mensajeError.postValue(e.toString())
                    }
                }

                override fun onComplete() {
                    verificationData()
                }
            })
    }

    fun verificationData() {
        val auditorias: Observable<List<Registro>> = roomRepository.getDataRegistro(0)
        auditorias
            .delay(1000, TimeUnit.MILLISECONDS)
            .flatMap { observable ->
                Observable.fromIterable(observable).flatMap { a ->
                    val b = MultipartBody.Builder()
//                    val detalles: List<RegistroDetalle>? = a.detalles
//                    if (detalles != null) {
//                        for (p: RegistroDetalle in detalles) {
//                            if (p.rutaFoto.isNotEmpty()) {
//                                val file =
//                                    File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + p.rutaFoto)
//                                if (file.exists()) {
//                                    b.addFormDataPart(
//                                        "fotos",
//                                        file.name,
//                                        RequestBody.create(
//                                            MediaType.parse("multipart/form-data"),
//                                            file
//                                        )
//                                    )
//                                }
//                            }
//                        }
//                    }
                    b.setType(MultipartBody.FORM)
                    val body = b.build()
                    Observable.zip(
                        Observable.just(a), roomRepository.verification(body),
                        BiFunction<Registro, String, String> { _, mensaje ->
                            mensaje
                        })
                }
            }.subscribeOn(Schedulers.io())
            .delay(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {

                override fun onSubscribe(d: Disposable) {
                    Log.i("TAG", d.toString())
                }

                override fun onNext(m: String) {

                }

                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        val body = e.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error.Message)
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                            Log.i("TAG", e1.toString())
                        }
                    } else {
                        mensajeError.postValue(e.toString())
                    }
                }

                override fun onComplete() {
                    mensajeSuccess.postValue("Ok")
                }
            })
    }

    private fun updateRegistro(messages: Mensaje) {
        roomRepository.updateRegistro(messages)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    Log.i("TAG", "ENVIOS ACTUALIZADOS")
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    Log.i("TAG", e.toString())
                }
            })
    }
}
