package com.dsige.apptrinidad.data.module

import android.app.Application
import androidx.room.Room
import com.dsige.apptrinidad.data.local.AppDataBase
import com.dsige.apptrinidad.data.local.dao.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseModule {

    @Provides
    @Singleton
    internal fun provideRoomDatabase(application: Application): AppDataBase {
        if (AppDataBase.INSTANCE == null) {
            synchronized(AppDataBase::class.java) {
                if (AppDataBase.INSTANCE == null) {
                    AppDataBase.INSTANCE = Room.databaseBuilder(
                        application.applicationContext,
                        AppDataBase::class.java, AppDataBase.DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
        }
        return AppDataBase.INSTANCE!!
    }

    @Provides
    internal fun provideUsuarioDao(appDataBase: AppDataBase): UsuarioDao {
        return appDataBase.usuarioDao()
    }


    @Provides
    internal fun provideParametroDao(appDataBase: AppDataBase): ParametroDao {
        return appDataBase.parametroDao()
    }

    @Provides
    internal fun provideRegistroDao(appDataBase: AppDataBase): RegistroDao {
        return appDataBase.registroDao()
    }

    @Provides
    internal fun provideRegistroPhotoDao(appDataBase: AppDataBase): RegistroPhotoDao {
        return appDataBase.registroPhotoDao()
    }

    @Provides
    internal fun provideServicioDao(appDataBase: AppDataBase): ServicioDao {
        return appDataBase.servicioDao()
    }
}