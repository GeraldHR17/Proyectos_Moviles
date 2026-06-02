package com.huertas.rivera.wikibusqueda.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.huertas.rivera.wikibusqueda.data.dao.ArticuloConsultaDao
import com.huertas.rivera.wikibusqueda.data.dao.FavoritoDao
import com.huertas.rivera.wikibusqueda.data.entity.ArticuloConsulta
import com.huertas.rivera.wikibusqueda.data.entity.Favorito

@Database(entities = [ArticuloConsulta::class, Favorito::class], version = 2, exportSchema = false)
abstract class WikiDatabase : RoomDatabase() {

    abstract fun articuloConsultaDao(): ArticuloConsultaDao
    abstract fun favoritoDao(): FavoritoDao

    companion object {
        @Volatile
        private var INSTANCE: WikiDatabase? = null

        fun getInstance(context: Context): WikiDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WikiDatabase::class.java,
                    "wiki_db"
                )
                .fallbackToDestructiveMigration() // Para simplificar en desarrollo si hay cambios de esquema
                .build().also {
                    INSTANCE = it
                }
            }
    }
}