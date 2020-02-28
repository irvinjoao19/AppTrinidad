package com.dsige.apptrinidad.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.apptrinidad.data.local.model.Usuario

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsuarioTask(c: Usuario)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsuarioListTask(c: List<Usuario>)

    @Update
    fun updateUsuarioTask(vararg c: Usuario)

    @Delete
    fun deleteUsuarioTask(c: Usuario)

    @Query("SELECT * FROM Usuario")
    fun getUsuario(): LiveData<Usuario>

    @Query("SELECT operarioId FROM Usuario")
    fun getUsuarioId(): LiveData<Int>

    @Query("SELECT operarioId FROM Usuario")
    fun getUsuarioIdTask(): Int

    @Query("SELECT activeInspeccion FROM Usuario")
    fun getUsuarioActive(): Int

    @Query("SELECT * FROM Usuario WHERE operarioId =:id")
    fun getUsuarioById(id: Int): LiveData<Usuario>

    @Query("UPDATE Usuario SET activeInspeccion=:estado")
    fun updateActiveInspeccion(estado: Int)


    @Query("DELETE FROM Usuario")
    fun deleteAll()
}