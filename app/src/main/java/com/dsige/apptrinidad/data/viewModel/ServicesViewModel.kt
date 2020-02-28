package com.dsige.apptrinidad.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dsige.apptrinidad.data.local.model.Servicio
import com.dsige.apptrinidad.data.local.repository.AppRepository
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ServicesViewModel @Inject
internal constructor(private val roomRepository: AppRepository) :
    ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val paginator = PublishProcessor.create<Int>()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val pageNumber: MutableLiveData<Int> = MutableLiveData()

    val servicio: MutableLiveData<Servicio> = MutableLiveData()
    val services: MutableLiveData<List<Servicio>> = MutableLiveData()
    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()
    val mensajeTipo: MutableLiveData<Int> = MutableLiveData()
    val search: MutableLiveData<String> = MutableLiveData()

    val load: LiveData<Boolean>
        get() = loading

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getServices() {
        roomRepository.getServices()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Servicio>> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Servicio>) {
                    services.value = t
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }


    fun getPageNumber(number: Int) {
        pageNumber.value = number
    }

    fun getLoading(load: Boolean) {
        loading.value = load
    }


    fun clear() {
        compositeDisposable.clear()
    }

    fun next(page: Int) {
        paginator.onNext(page)
    }


    fun saveRegistro(suministroId: Int) {
        roomRepository.insertRegistro(suministroId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }
}