package com.huertas.rivera.wikibusqueda.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.huertas.rivera.wikibusqueda.data.entity.ArticuloConsulta
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticuloConsultaDao {

    @Insert
    suspend fun insert(articulo: ArticuloConsulta)

    @Query("SELECT * FROM articulo_consulta WHERE `key` = :key ORDER BY fechaConsulta DESC")
    fun getConsultasByKey(key: String): Flow<List<ArticuloConsulta>>

    @Query("SELECT COUNT(*) FROM articulo_consulta WHERE `key` = :key")
    fun getCantidadConsultas(key: String): Flow<Int>
}