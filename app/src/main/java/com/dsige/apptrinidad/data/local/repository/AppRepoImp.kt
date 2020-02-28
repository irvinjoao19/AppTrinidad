package com.dsige.apptrinidad.data.local.repository

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
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

    override fun getUsuarioId(): LiveData<Int> {
        return dataBase.usuarioDao().getUsuarioId()
    }

    override fun getUsuarioIdTask(): Int {
        return dataBase.usuarioDao().getUsuarioIdTask()
    }

    override fun getUsuarioActive(): Observable<Int> {
        return Observable.create { e ->
            val n = dataBase.usuarioDao().getUsuarioActive()
            e.onNext(n)
            e.onComplete()
        }
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
            dataBase.registroPhotoDao().deleteAll()
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
            dataBase.registroPhotoDao().deleteAll()
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
            dataBase.registroPhotoDao().updateRegistroPhotoEstado(m.codigoBase)
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
                val photos = dataBase.registroPhotoDao().getAllRegistroPhotoTask(r.registroId)
                r.photos = photos
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

    override fun getRegistroBySuministro(suministroId: Int): LiveData<Registro> {
        return dataBase.registroDao().getRegistroBySuministro(suministroId)
    }

    override fun getRegistroBySuministroTask(suministroId: Int): Observable<Registro> {
        return Observable.create { emitter ->
            val r: Registro? = dataBase.registroDao().getRegistroBySuministroTask(suministroId)
            if (r != null) {
                emitter.onNext(r)
            } else {
                emitter.onError(Throwable(String.format("%s", "Registro no existe")))
            }
            emitter.onComplete()
        }
    }

    override fun getSizeRegistroByTipo(id: Int): LiveData<Int> {
        return dataBase.registroDao().getSizeRegistroByTipo(id)
    }

    override fun getSizeRegistroByTipoRx(id: Int): Observable<Int> {
        return Observable.create { e ->
            val n = dataBase.registroDao().getSizeRegistroByTipoRx(id)
            e.onNext(n)
            e.onComplete()
        }
    }

    override fun getAllRegistroVerificate(valor: Int): LiveData<Int> {
        return dataBase.registroDao().getAllRegistroVerificate(valor)
    }

    override fun insertRegistro(id: Int): Completable {
        return Completable.fromAction {
            val boolean: Boolean = dataBase.registroDao().getExistRegistro(id)
            if (!boolean) {
//                dataBase.suministroDao().updateSuministroActive(id)
            }
        }
    }

    override fun insertOrUpdateRegistro(r: Registro, type: Int): Completable {
        return Completable.fromAction {
            val boolean: Boolean = dataBase.registroDao().getExistRegistro(r.suministroId)
            if (boolean) {
                dataBase.registroDao().updateRegistroTask(r)
                if (type == 1) {
//                    dataBase.suministroDao().updateSuministroActive(r.suministroId)
                }
                val photos: List<RegistroPhoto>? = r.photos
                if (photos != null) {
                    for (p: RegistroPhoto in photos) {
                        val f: RegistroPhoto? =
                            dataBase.registroPhotoDao().getExistRegistroPhoto(p.registroId)
                        if (f != null) {
                            f.rutaFoto = p.rutaFoto
                            f.fechaSincronizacion = p.fechaSincronizacion
                            f.latitud = p.latitud
                            f.longitud = p.longitud
                            dataBase.registroPhotoDao().updateRegistroPhotoTask(f)
                        } else {
                            dataBase.registroPhotoDao().insertRegistroPhotoTask(p)
                        }
                    }
                }
            } else {
                if (type == 1) {
//                    dataBase.suministroDao().updateSuministroActive(r.suministroId)
                }
                dataBase.registroDao().insertRegistroTask(r)
            }
        }
    }

    override fun insertOrUpdateRegistroPhoto(p: RegistroPhoto): Completable {
        return Completable.fromAction {
            if (p.registroPhotoId == 0) {
                dataBase.registroPhotoDao().insertRegistroPhotoTask(p)
            } else {
                dataBase.registroPhotoDao().updateRegistroPhotoTask(p)
            }
        }
    }


}