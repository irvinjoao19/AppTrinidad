package com.dsige.apptrinidad.data.local.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.dsige.apptrinidad.data.local.model.*
import com.dsige.apptrinidad.helper.Mensaje
import com.dsige.apptrinidad.helper.Util
import com.dsige.apptrinidad.data.local.AppDataBase
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class AppRepoImp(private val apiService: ApiService, private val dataBase: AppDataBase) :
    AppRepository {

    override fun getUsuario(): LiveData<Usuario> {
        return dataBase.usuarioDao().getUsuario()
    }

    override fun getUsuarioId(): LiveData<String> {
        return dataBase.usuarioDao().getUsuarioId()
    }

    override fun getUsuarioIdTask(): String {
        return dataBase.usuarioDao().getUsuarioIdTask()
    }

    override fun getUsuarioService(
        usuario: String, password: String, imei: String, version: String
    ): Observable<Usuario> {
        val u = Filtro(usuario, password, imei, version)
        val json = Gson().toJson(u)
        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getLogin(body)
    }

    override fun insertUsuario(u: Usuario): Completable {
        return Completable.fromAction {
            dataBase.usuarioDao().insertUsuarioTask(u)
        }
    }

    override fun deleteUsuario(): Completable {
        return Completable.fromAction {
            dataBase.usuarioDao().deleteAll()
            dataBase.servicioDao().deleteAll()
            dataBase.registroDao().deleteAll()
            dataBase.registroDetalleDao().deleteAll()
            dataBase.parametroDao().deleteAll()
        }
    }

    override fun deleteTotal(): Completable {
        return Completable.fromAction {

            Util.deleteDirectory(
                File(
                    Environment.getExternalStorageDirectory(), Util.FolderImg
                )
            )
            dataBase.servicioDao().deleteAll()
            dataBase.registroDao().deleteAll()
            dataBase.registroDetalleDao().deleteAll()
            dataBase.parametroDao().deleteAll()
        }
    }

    override fun getParametroById(id: Int): Int {
        return dataBase.parametroDao().getParametroByIdTask(id)
    }

    override fun getSync(operarioId: Int, version: String): Observable<Sync> {
        return apiService.getSync(operarioId, version)
    }

    override fun saveSync(s: Sync): Completable {
        return Completable.fromAction {

            val p: List<Parametro>? = s.parametros
            if (p != null) {
                dataBase.parametroDao().insertParametroListTask(p)
            }

            val m: List<Servicio>? = s.servicios
            if (m != null) {
                dataBase.servicioDao().insertServicioListTask(m)
            }

        }
    }

    override fun getSizeRegistro(): LiveData<Int> {
        return dataBase.registroDao().getSizeRegistro()
    }

    override fun saveData(body: RequestBody): Observable<Mensaje> {
        return apiService.save(body)
    }

    override fun verification(body: RequestBody): Observable<String> {
        return apiService.verification(body)
    }

    override fun verificateInspecciones(id: Int, fecha: String): Observable<Mensaje> {
        return apiService.verificateInspecciones(id, fecha)
    }

    override fun saveInspection(body: RequestBody): Observable<Mensaje> {
        return apiService.saveInspection(body)
    }

    override fun saveInspectionDetail(body: RequestBody): Observable<Mensaje> {
        return apiService.saveInspeccionDetalle(body)
    }

    override fun saveGps(body: RequestBody): Observable<Mensaje> {
        return apiService.saveGps(body)
    }

    override fun saveGpsTask(body: RequestBody): Call<Mensaje> {
        return apiService.saveGpsTask(body)
    }

    override fun saveMovil(body: RequestBody): Observable<Mensaje> {
        return apiService.saveMovil(body)
    }

    override fun saveMovilTask(body: RequestBody): Call<Mensaje> {
        return apiService.saveMovilTask(body)
    }

    override fun updateRegistro(m: Mensaje): Completable {
        return Completable.fromAction {
            dataBase.registroDao().updateRegistroEstado(m.codigoBase)
            dataBase.registroDetalleDao().updateRegistroPhotoEstado(m.codigoBase)
        }
    }

    /**
     * estado = 1 -> activos por enviar
     * 0 -> faltantes
     */
    override fun getDataRegistro(estado: Int): Observable<List<Registro>> {
        return Observable.create { emitter ->
            val list: ArrayList<Registro> = ArrayList()
            val v: List<Registro> = dataBase.registroDao().getAllRegistroTask(1)
            for (r: Registro in v) {
//                val photos = dataBase.registroDetalleDao().getAllRegistroPhotoTask(r.registroId)
//                r.detalles = photos
                list.add(r)
            }
            emitter.onNext(list)
            emitter.onComplete()
        }
    }

    override fun getServices(): Observable<List<Servicio>> {
        return Observable.create { emitter ->
            val list: ArrayList<Servicio> = ArrayList()
            val v: List<Servicio> = dataBase.servicioDao().getServicioTask()
            for (r: Servicio in v) {
                if (r.servicioId != 5) {
//                    r.size = dataBase.suministroDao().getSuministroSizeTask(r.servicioId.toString())
                }
                list.add(r)
            }
            emitter.onNext(list)
            emitter.onComplete()
        }
    }

    override fun insertOrUpdateRegistro(r: Registro, id: Int): Completable {
        return Completable.fromAction {
            if (id == 0) {
                dataBase.registroDao().insertRegistroTask(r)
                val identity = dataBase.registroDao().getTaskIdentity()
                r.detalles!!.registroId = identity
                dataBase.registroDetalleDao().insertRegistroPhotoTask(r.detalles!!)
            }
        }
    }

    override fun insertOrUpdateRegistroPhoto(p: RegistroDetalle): Completable {
        return Completable.fromAction {
            if (p.detalleId == 0) {
                dataBase.registroDetalleDao().insertRegistroPhotoTask(p)
            } else {
                dataBase.registroDetalleDao().updateRegistroPhotoTask(p)
            }
        }
    }

    override fun getIdentity(): LiveData<Int> {
        return dataBase.registroDao().getIdentity()
    }

    override fun getRegistroPhotoById(id: Int): LiveData<PagedList<RegistroDetalle>> {
        return dataBase.registroDetalleDao().getRegistroPhotoPaging(id).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getRegistroById(id: Int): LiveData<Registro> {
        return dataBase.registroDao().getRegistroById(id)
    }

    override fun deleteGaleria(d: MenuPrincipal, c: Context): Completable {
        return Completable.fromAction {
            Util.deleteImage(d.title, c)
            val a = dataBase.registroDetalleDao().getRegistroDetalleById(d.id)
            when (d.image) {
                1 -> a.foto1PuntoAntes = ""
                2 -> a.foto2PuntoAntes = ""
                3 -> a.foto3PuntoAntes = ""
                4 -> a.foto1PuntoDespues = ""
                5 -> a.foto2PuntoDespues = ""
                6 -> a.foto3PuntoDespues = ""
            }
            dataBase.registroDetalleDao().updateRegistroPhotoTask(a)
        }
    }

    override fun getRegistroByTipo(tipo: Int): LiveData<List<Registro>> {
        return dataBase.registroDao().getRegistroByTipo(tipo)
    }

    override fun getRegistroPagingByTipo(tipo: Int): LiveData<PagedList<Registro>> {
        return dataBase.registroDao().getRegistroPagingByTipo(tipo).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getRegistroByObra(o: String): LiveData<List<Registro>> {
        return dataBase.registroDao().getRegistroByObra(o)
    }

    override fun getDetalleIdentity(): LiveData<Int> {
        return dataBase.registroDetalleDao().getIdentity()
    }

    override fun updatePhoto(tipo: Int, name: String, id: Int): Completable {
        return Completable.fromAction {
            val d: RegistroDetalle = dataBase.registroDetalleDao().getRegistroDetalleById(id)
            if (tipo == 1) {
                when {
                    d.foto1PuntoAntes.isEmpty() -> d.foto1PuntoAntes = name
                    d.foto2PuntoAntes.isEmpty() -> d.foto2PuntoAntes = name
                    else -> d.foto3PuntoAntes = name
                }
            } else {
                when {
                    d.foto1PuntoDespues.isEmpty() -> d.foto1PuntoDespues = name
                    d.foto2PuntoDespues.isEmpty() -> d.foto2PuntoDespues = name
                    else -> d.foto3PuntoDespues = name
                }
            }
            dataBase.registroDetalleDao().updateRegistroPhotoTask(d)
        }
    }

    override fun getRegistroDetalle(id: Int): LiveData<RegistroDetalle> {
        return dataBase.registroDetalleDao().getRegistroDetalle(id)
    }

    override fun getRegistroDetalleById(id: Int): LiveData<List<RegistroDetalle>> {
        return dataBase.registroDetalleDao().getRegistroDetalleByRegistroId(id)
    }
}