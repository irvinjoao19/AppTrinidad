package com.dsige.apptrinidad.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.apptrinidad.data.local.model.Parametro

@Dao
interface ParametroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertParametroTask(c: Parametro)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertParametroListTask(c: List<Parametro>)

    @Update
    fun updateParametroTask(vararg c: Parametro)

    @Delete
    fun deleteParametroTask(c: Parametro)

    @Query("SELECT * FROM Parametro")
    fun getParametroTask(): LiveData<Parametro>

    @Query("SELECT * FROM Parametro")
    fun getParametro(): Parametro

    @Query("SELECT * FROM Parametro WHERE tipo =:t")
    fun getComboByTipo(t:Int) : LiveData<List<Parametro>>

    @Query("DELETE FROM Parametro")
    fun deleteAll()
}