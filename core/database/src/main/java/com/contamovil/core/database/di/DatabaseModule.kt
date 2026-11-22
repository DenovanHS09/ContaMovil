package com.contamovil.core.database.di

import android.content.Context
import androidx.room.Room
import com.contamovil.core.database.AppDatabase
import com.contamovil.core.database.MIGRACION_1_2
import com.contamovil.core.database.MIGRACION_2_3
import com.contamovil.core.database.MIGRACION_3_4
import com.contamovil.core.database.MIGRACION_4_5
import com.contamovil.core.database.MIGRACION_5_6
import com.contamovil.core.database.dao.ComprobanteDao
import com.contamovil.core.database.dao.CuentaDao
import com.contamovil.core.database.dao.InformeDao
import com.contamovil.core.database.dao.NormativaDao
import com.contamovil.core.database.dao.PerfilDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NOMBRE)
            .addMigrations(
                MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4, MIGRACION_4_5, MIGRACION_5_6
            )
            // TODO: añadir SQLCipher (SupportFactory) para cifrar la base de datos
            .build()

    @Provides
    fun provideCuentaDao(db: AppDatabase): CuentaDao = db.cuentaDao()

    @Provides
    fun provideComprobanteDao(db: AppDatabase): ComprobanteDao = db.comprobanteDao()

    @Provides
    fun provideInformeDao(db: AppDatabase): InformeDao = db.informeDao()

    @Provides
    fun provideNormativaDao(db: AppDatabase): NormativaDao = db.normativaDao()

    @Provides
    fun providePerfilDao(db: AppDatabase): PerfilDao = db.perfilDao()
}
