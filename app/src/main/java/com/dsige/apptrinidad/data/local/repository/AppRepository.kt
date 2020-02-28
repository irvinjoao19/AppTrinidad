package com.dsige.apptrinidad.data.local.repository

import androidx.lifecycle.LiveData
import com.dsige.apptrinidad.data.local.model.*
import com.dsige.apptrinidad.helper.Mensaje
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call

interface AppRepository {

    fun getUsuario(): LiveData<Usuario>

    fun getUsuarioId(): LiveData<Int>

    fun getUsuarioIdTask(): Int

    fun getUsuarioActive(): Observable<Int>

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

    fun verificateInspecciones(id:Int,fecha:String) : Observable<Mensaje>

    fun saveInspection(body: RequestBody): Observable<Mensaje>

    fun saveInspectionDetail(body: RequestBody): Observable<Mensaje>

    fun saveGps(body: RequestBody): Observable<Mensaje>

    fun saveGpsTask(body: RequestBody): Call<Mensaje>

    fun saveMovil(body: RequestBody): Observable<Mensaje>

    fun saveMovilTask(body: RequestBody): Call<Mensaje>

    fun updateRegistro(m: Mensaje): Completable

    fun getDataRegistro(estado : Int): Observable<List<Registro>>

    // TODO SERVICIOS

    fun getServices(): Observable<List<Servicio>>

    // TODO REGISTRO

    fun getRegistroBySuministro(suministroId: Int): LiveData<Registro>

    fun getRegistroBySuministroTask(suministroId: Int): Observable<Registro>

    fun getSizeRegistroByTipo(id: Int): LiveData<Int>

    fun getSizeRegistroByTipoRx(id: Int): Observable<Int>

    fun getAllRegistroVerificate(valor: Int): LiveData<Int>

    fun insertRegistro(id: Int): Completable

    fun insertOrUpdateRegistro(r: Registro, type: Int): Completable

    fun insertOrUpdateRegistroPhoto(p: RegistroPhoto): Completable
}