package com.dsige.apptrinidad.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.apptrinidad.data.local.model.Servicio

@Dao
interface ServicioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertServicioTask(c: Servicio)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertServicioListTask(c: List<Servicio>)

    @Update
    fun updateServicioTask(vararg c: Servicio)

    @Delete
    fun deleteServicioTask(c: Servicio)

    @Query("SELECT * FROM Servicio")
    fun getServicio(): LiveData<Servicio>

    @Query("SELECT * FROM Servicio ORDER BY orden")
    fun getServicioTask(): List<Servicio>

    @Query("SELECT * FROM Servicio WHERE servicioId =:id")
    fun getServicioById(id: Int): LiveData<Servicio>

    @Query("DELETE FROM Servicio")
    fun deleteAll()
}