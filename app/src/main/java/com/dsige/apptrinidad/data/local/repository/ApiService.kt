package com.dsige.apptrinidad.data.local.repository

import com.dsige.apptrinidad.data.local.model.Sync
import com.dsige.apptrinidad.data.local.model.Usuario
import com.dsige.apptrinidad.helper.Mensaje
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Headers("Cache-Control: no-cache")
    @POST("Login")
    fun getLogin(@Body body: RequestBody): Observable<Usuario>

    @Headers("Cache-Control: no-cache")
    @GET("Sync")
    fun getSync(): Observable<Sync>
//    fun getSync(@Query("operarioId") operarioId: Int, @Query("version") version: String ): Observable<Sync>

    @Headers("Cache-Control: no-cache")
    @POST("SaveRegistro")
    fun save(@Body body: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SaveVehiculo")
    fun saveVehiculo(@Body body: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("Verification")
    fun verification(@Body body: RequestBody): Observable<String>

    @Headers("Cache-Control: no-cache")
    @POST("SaveGps")
    fun saveGps(@Body body: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SaveGps")
    fun saveGpsTask(@Body body: RequestBody): Call<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SaveMovil")
    fun saveMovil(@Body body: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SaveMovil")
    fun saveMovilTask(@Body body: RequestBody): Call<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SaveInspeccionAsync")
    fun saveInspection(@Body body: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SaveInspeccionDetalle")
    fun saveInspeccionDetalle(@Body body: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @GET("VerificateInspecciones")
    fun verificateInspecciones(@Query("operarioId") operarioId: Int,@Query("fecha") fecha :String): Observable<Mensaje>
}