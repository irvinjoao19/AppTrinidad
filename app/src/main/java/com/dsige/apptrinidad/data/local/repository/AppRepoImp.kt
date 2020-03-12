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

    override fun getSync(id: String, version: String): Observable<Sync> {
        return apiService.getSync()
    }

    override fun saveSync(s: Sync): Completable {
        return Completable.fromAction {
            val p: List<Vehiculo>? = s.vehiculos
            if (p != null) {
                dataBase.vehiculoDao().insertVehiculoListTask(p)
            }

            val c: List<Parametro>? = s.parametros
            if (c != null) {
                dataBase.parametroDao().insertParametroListTask(c)
            }
        }
    }

    override fun getSizeRegistro(): LiveData<Int> {
        return dataBase.registroDao().getSizeRegistro()
    }

    override fun saveData(body: RequestBody): Observable<Mensaje> {
        return apiService.save(body)
    }

    override fun saveVehiculo(body: RequestBody): Observable<Mensaje> {
        return apiService.saveVehiculo(body)
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
            dataBase.registroDao().updateRegistroEstado(m.codigoBase, m.codigoRetorno)
//            dataBase.registroDetalleDao().updateRegistroPhotoEstado(m.codigoBase)
        }
    }

    /**
     * estado = 1 -> activos por enviar
     * 0 -> faltantes
     */
    override fun getDataRegistro(estado: Int): Observable<List<Registro>> {
        return Observable.create { emitter ->
            val v: List<Registro> = dataBase.registroDao().getAllRegistroTask(estado)
            if (v.isNotEmpty()) {
                val list: ArrayList<Registro> = ArrayList()
                for (r: Registro in v) {
                    val detalle =
                        dataBase.registroDetalleDao().getAllRegistroDetalleTask(r.registroId)
                    r.list = detalle
                    list.add(r)
                }
                emitter.onNext(list)
            } else
                emitter.onError(Throwable("No hay datos disponibles por enviar"))

            emitter.onComplete()
        }
    }

    override fun getDataVehiculo(estado: Int): Observable<List<Vehiculo>> {
        return Observable.create { emitter ->
            val v: List<Vehiculo> = dataBase.vehiculoDao().getAllVehiculoTask(estado)
            if (v.isNotEmpty()) {
                val list: ArrayList<Vehiculo> = ArrayList()
                for (c: Vehiculo in v) {
                    val control: List<VehiculoControl> =
                        dataBase.vehiculoControlDao().getVehiculoControlTaskById(c.placa)
                    c.control = control
                    val vale: List<VehiculoVales> =
                        dataBase.vehiculoValesDao().getVehiculoValeTaskById(c.placa)
                    c.registros = vale
                    list.add(c)
                }
                emitter.onNext(list)
            } else
                emitter.onError(Throwable("No hay datos disponibles por enviar"))

            emitter.onComplete()
        }
    }

    override fun insertOrUpdateRegistro(r: Registro, id: Int): Completable {
        return Completable.fromAction {
            if (r.registroId == 0) {
                if (id == 0) {
                    dataBase.registroDao().insertRegistroTask(r)
                    val identity = dataBase.registroDao().getTaskIdentity()
                    r.detalles!!.registroId = identity
                    dataBase.registroDetalleDao().insertRegistroPhotoTask(r.detalles!!)
                } else {
                    dataBase.registroDetalleDao()
                        .updateObservacion(r.detalles!!.detalleId, r.detalles!!.observacion)
                }
            } else {
                if (r.tipo == 1) {
                    if (id == 0) {
                        r.detalles!!.registroId = r.registroId
                        dataBase.registroDetalleDao().insertRegistroPhotoTask(r.detalles!!)
                    }
                }
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

    override fun updatePhoto(tipo: Int, name: String, detalleId: Int, id: Int): Completable {
        return Completable.fromAction {
            when (tipo) {
                1 -> {
                    val d: RegistroDetalle =
                        dataBase.registroDetalleDao().getRegistroDetalleById(detalleId)
                    when {
                        d.foto1PuntoAntes.isEmpty() -> d.foto1PuntoAntes = name
                        d.foto2PuntoAntes.isEmpty() -> d.foto2PuntoAntes = name
                        else -> d.foto3PuntoAntes = name
                    }
                    dataBase.registroDetalleDao().updateRegistroPhotoTask(d)
                }
                2 -> {
                    val d: RegistroDetalle =
                        dataBase.registroDetalleDao().getRegistroDetalleById(detalleId)
                    when {
                        d.foto1PuntoDespues.isEmpty() -> d.foto1PuntoDespues = name
                        d.foto2PuntoDespues.isEmpty() -> d.foto2PuntoDespues = name
                        else -> d.foto3PuntoDespues = name
                    }
                    dataBase.registroDetalleDao().updateRegistroPhotoTask(d)
                }
                else -> dataBase.registroDao().updatePhoto(id, name)
            }
        }
    }

    override fun getRegistroDetalle(tipo: Int, id: Int): LiveData<RegistroDetalle> {
        return if (tipo == 1) {
            dataBase.registroDetalleDao().getRegistroDetalle(id)
        } else {
            dataBase.registroDetalleDao().getRegistroDetalleFk(id)
        }
    }

    override fun getRegistroDetalleById(id: Int): LiveData<List<RegistroDetalle>> {
        return dataBase.registroDetalleDao().getRegistroDetalleByRegistroId(id)
    }

    override fun populateVehiculo(): LiveData<List<Vehiculo>> {
        return dataBase.vehiculoDao().getVehiculo()
    }

    override fun getVehiculo(placa: String): LiveData<Vehiculo> {
        return dataBase.vehiculoDao().getVehiculoById(placa)
    }

    override fun getControlVehiculo(placa: String): LiveData<List<VehiculoControl>> {
        return dataBase.vehiculoControlDao().getControlVehiculo(placa)
    }

    override fun saveControl(v: VehiculoControl): Completable {
        return Completable.fromAction {
            if (v.controlId == 0) {
                dataBase.vehiculoControlDao().insertVehiculoControlTask(v)
            } else {
                v.estado = 1
                dataBase.vehiculoControlDao().updateVehiculoControlTask(v)
            }

        }
    }

    override fun getControVehiculoById(controlId: Int): LiveData<VehiculoControl> {
        return dataBase.vehiculoControlDao().getControVehiculoById(controlId)
    }

    override fun getComboByTipo(tipo: Int): LiveData<List<Parametro>> {
        return dataBase.parametroDao().getComboByTipo(tipo)
    }

    override fun saveVales(c: VehiculoVales): Completable {
        return Completable.fromAction {
            if (c.valeId == 0) {
                dataBase.vehiculoValesDao().insertVehiculoValesTask(c)
            } else
                dataBase.vehiculoValesDao().updateVehiculoValesTask(c)
        }
    }

    override fun getValeVehiculo(placa: String): LiveData<List<VehiculoVales>> {
        return dataBase.vehiculoValesDao().getValeVehiculo(placa)
    }

    override fun getValeVehiculoById(id: Int): LiveData<VehiculoVales> {
        return dataBase.vehiculoValesDao().getValeVehiculoById(id)
    }

    override fun closeRegistroDetalle(
        registroId: Int, detalleId: Int, tipoDetalle: Int, tipo: Int
    ): Completable {
        return Completable.fromAction {
            when (tipo) {
                1 -> when (tipoDetalle) {
                    1 -> dataBase.registroDetalleDao().closeRegistroDetalle1(detalleId)
                    2 -> dataBase.registroDetalleDao().closeRegistroDetalle2(detalleId)
                }
                2 -> {
                    dataBase.registroDetalleDao().closeRegistroDetalle1(detalleId)
                    dataBase.registroDao().closeRegistro(registroId)
                }
            }
        }
    }

    override fun validarRegistro(registroId: Int): Observable<Int> {
        return Observable.create { emitter ->
            val v: List<RegistroDetalle>? =
                dataBase.registroDetalleDao().getAllRegistroDetalleTask(registroId)
            if (v != null) {
                val size = v.size
                var count = 0
                for (r: RegistroDetalle in v) {
                    if (r.active1 == 1 && r.active2 == 1) {
                        count++
                    }
                }
                if (size == count) {
                    emitter.onNext(1)
                } else
                    emitter.onNext(2)
            } else {
                emitter.onNext(3)
            }
            emitter.onComplete()
        }
    }

    override fun updateVehiculo(messages: Mensaje): Completable {
        return Completable.fromAction {
            dataBase.vehiculoDao().updateEnabledVehiculo(2)
            dataBase.vehiculoValesDao().updateEnabledVale(0)
            dataBase.vehiculoControlDao().updateEnabledControl(0)
        }
    }

    override fun closeVerificationVehiculo(placa: String): Observable<String> {
        return Observable.create { emitter ->
            val va: List<VehiculoVales> = dataBase.vehiculoValesDao().getVehiculoValeTaskById(placa)
            if (va.isEmpty()) {
                emitter.onError(Throwable("No hay registros en Combustibles"))
                emitter.onComplete()
                return@create
            }
            val v: List<VehiculoControl> =
                dataBase.vehiculoControlDao().getVehiculoControlTaskById(placa)
            if (v.isNotEmpty()) {
                var count = 0
                val size = v.size
                for (r: VehiculoControl in v) {
                    if (r.estado == 1) {
                        count++
                    }
                }
                if (size == count) {
                    dataBase.vehiculoDao().updateEnabledVehiculoByPlaca(placa)
                    emitter.onNext("Cerrado")
                    emitter.onComplete()
                    return@create
                } else {
                    emitter.onError(Throwable("Cerrar Kilometraje"))
                    emitter.onComplete()
                    return@create
                }
            } else {
                emitter.onError(Throwable("No hay registros en Kilometraje"))
                emitter.onComplete()
                return@create
            }

        }
    }
}