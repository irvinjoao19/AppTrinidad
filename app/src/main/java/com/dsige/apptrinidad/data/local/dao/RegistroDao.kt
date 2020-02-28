package com.dsige.apptrinidad.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.apptrinidad.data.local.model.Registro

@Dao
interface RegistroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegistroTask(c: Registro)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegistroListTask(c: List<Registro>)

    @Update
    fun updateRegistroTask(vararg c: Registro)

    @Delete
    fun deleteRegistroTask(c: Registro)

    @Query("SELECT * FROM Registro")
    fun getRegistro(): LiveData<Registro>

    @Query("SELECT COUNT(estado) FROM Registro WHERE estado = 1")
    fun getSizeRegistro(): LiveData<Int>

    @Query("SELECT * FROM Registro WHERE suministroId =:id")
    fun getRegistroBySuministro(id: Int): LiveData<Registro>

    @Query("SELECT COUNT(estado) FROM Registro WHERE tipoLectura=:id AND estado != 2")
    fun getSizeRegistroByTipo(id: Int): LiveData<Int>

    @Query("SELECT COUNT(estado) FROM Registro WHERE tipoLectura=:id AND estado != 2")
    fun getSizeRegistroByTipoRx(id: Int): Int

    @Query("SELECT * FROM Registro WHERE estado =:valor")
    fun getAllRegistroTask(valor: Int): List<Registro>

    @Query("SELECT COUNT(estado) FROM Registro WHERE estado =:valor")
    fun getAllRegistroVerificate(valor: Int): LiveData<Int>

    @Query("SELECT * FROM Registro WHERE registroId =:id")
    fun getRegistroById(id: Int): LiveData<Registro>

    @Query("SELECT * FROM Registro WHERE registroId =:id")
    fun getRegistroTaskById(id: Int): Registro

    @Query("DELETE FROM Registro")
    fun deleteAll()

    @Query("UPDATE Registro SET estado = 0 WHERE registroId =:id")
    fun updateRegistroEstado(id: Int)

    @Query("SELECT registroId FROM Registro  WHERE suministroId=:id")
    fun getExistRegistro(id: Int): Boolean

    @Query("SELECT registroId FROM Registro  WHERE suministroId=:id")
    fun getRegistroId(id: Int): Int

    @Query("SELECT * FROM Registro  WHERE suministroId=:id")
    fun getRegistroBySuministroTask(id: Int): Registro

    @Query("UPDATE Registro SET estado =:e")
    fun updateRegistroMasivoEstado(e: Int)
}