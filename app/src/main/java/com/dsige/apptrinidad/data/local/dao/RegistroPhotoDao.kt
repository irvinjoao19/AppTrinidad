package com.dsige.apptrinidad.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.apptrinidad.data.local.model.RegistroPhoto

@Dao
interface RegistroPhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegistroPhotoTask(c: RegistroPhoto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegistroPhotoListTask(c: List<RegistroPhoto>)

    @Update
    fun updateRegistroPhotoTask(vararg c: RegistroPhoto)

    @Delete
    fun deleteRegistroPhotoTask(c: RegistroPhoto)

    @Query("SELECT * FROM RegistroPhoto")
    fun getRegistroPhotoTask(): LiveData<RegistroPhoto>

    @Query("SELECT * FROM RegistroPhoto")
    fun getRegistroPhoto(): RegistroPhoto

    @Query("SELECT * FROM RegistroPhoto WHERE registroId=:id AND estado = 1")
    fun getAllRegistroPhotoTask(id: Int): List<RegistroPhoto>

    @Query("SELECT * FROM RegistroPhoto WHERE registroPhotoId =:id")
    fun getRegistroPhotoById(id: Int): LiveData<RegistroPhoto>

    @Query("DELETE FROM RegistroPhoto")
    fun deleteAll()

    @Query("UPDATE RegistroPhoto SET estado = 0 WHERE registroId =:id")
    fun updateRegistroPhotoEstado(id: Int)

    @Query("SELECT * FROM RegistroPhoto WHERE registroId =:id")
    fun getExistRegistroPhoto(id:Int) : RegistroPhoto

    @Query("UPDATE RegistroPhoto SET estado =:e ")
    fun updateRegistroPhotoMasivoEstado(e: Int)

}