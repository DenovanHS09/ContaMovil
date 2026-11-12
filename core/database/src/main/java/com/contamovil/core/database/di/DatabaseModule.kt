package com.contamovil.core.database.di

import android.content.Context
import androidx.room.Room
import com.contamovil.core.database.AppDatabase
import com.contamovil.core.database.dao.CuentaDao
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
            // TODO: añadir SQLCipher (SupportFactory) para cifrar la base de datos
            .build()

    @Provides
    fun provideCuentaDao(db: AppDatabase): CuentaDao = db.cuentaDao()
}
