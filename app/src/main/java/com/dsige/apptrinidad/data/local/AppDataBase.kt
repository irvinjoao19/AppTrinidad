package com.dsige.apptrinidad.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dsige.apptrinidad.data.local.dao.*
import com.dsige.apptrinidad.data.local.model.*

@Database(
    entities = [
        Usuario::class,
        Registro::class,
        RegistroDetalle::class,
        Servicio::class,
        Parametro::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun parametroDao(): ParametroDao
    abstract fun registroDao(): RegistroDao
    abstract fun registroDetalleDao(): RegistroDetalleDao
    abstract fun servicioDao(): ServicioDao

    companion object {
        @Volatile
        var INSTANCE: AppDataBase? = null
        val DB_NAME = "lds_db"
    }


    fun getDatabase(context: Context): AppDataBase {
        if (INSTANCE == null) {
            synchronized(AppDataBase::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java, "lds_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
        }
        return INSTANCE!!
    }
}