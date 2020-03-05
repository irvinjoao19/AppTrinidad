package com.dsige.apptrinidad.data.local.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.dsige.apptrinidad.data.local.model.*
import com.dsige.apptrinidad.helper.Mensaje
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call

interface AppRepository {

    fun getUsuario(): LiveData<Usuario>

    fun getUsuarioId(): LiveData<String>

    fun getUsuarioIdTask(): String

    fun getUsuarioService(
        usuario: String,
        password: String,
        imei: String,
        version: String
    ): Observable<Usuario>

    fun insertUsuario(u: Usuario): Completable

    fun deleteUsuario(): Completable

    fun deleteTotal(): Completable

    fun getParametroById(id: Int): Int

    fun getSync(operarioId: Int, version: String): Observable<Sync>

    fun saveSync(s: Sync): Completable

    // TODO ENVIOS

    fun getSizeRegistro(): LiveData<Int>

    fun saveData(body: RequestBody): Observable<Mensaje>

    fun verification(body: RequestBody): Observable<String>

    fun verificateInspecciones(id: Int, fecha: String): Observable<Mensaje>

    fun saveInspection(body: RequestBody): Observable<Mensaje>

    fun saveInspectionDetail(body: RequestBody): Observable<Mensaje>

    fun saveGps(body: RequestBody): Observable<Mensaje>

    fun saveGpsTask(body: RequestBody): Call<Mensaje>

    fun saveMovil(body: RequestBody): Observable<Mensaje>

    fun saveMovilTask(body: RequestBody): Call<Mensaje>

    fun updateRegistro(m: Mensaje): Completable

    fun getDataRegistro(estado: Int): Observable<List<Registro>>

    // TODO SERVICIOS

    fun getServices(): Observable<List<Servicio>>

    // TODO REGISTRO

    fun insertOrUpdateRegistro(r: Registro,id:Int): Completable

    fun insertOrUpdateRegistroPhoto(p: RegistroDetalle): Completable

    fun getIdentity(): LiveData<Int>

    fun getRegistroPhotoById(id: Int): LiveData<PagedList<RegistroDetalle>>

    fun getRegistroById(id: Int): LiveData<Registro>

    fun deleteGaleria(d: MenuPrincipal, c: Context): Completable

    fun getRegistroByTipo(tipo: Int): LiveData<List<Registro>>

    fun getRegistroPagingByTipo(tipo: Int): LiveData<PagedList<Registro>>

    fun getRegistroByObra(o: String): LiveData<List<Registro>>

    fun getDetalleIdentity() : LiveData<Int>

    fun updatePhoto(tipo:Int,name:String,id:Int) : Completable

    fun getRegistroDetalle(id: Int): LiveData<RegistroDetalle>

    fun getRegistroDetalleById(id: Int): LiveData<List<RegistroDetalle>>
}