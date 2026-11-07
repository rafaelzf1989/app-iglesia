package com.iglesia.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(u: Usuario): Long

    @Update
    suspend fun actualizar(u: Usuario)

    @Query("SELECT * FROM usuarios WHERE usuario = :nombre COLLATE NOCASE LIMIT 1")
    suspend fun porNombre(nombre: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun porId(id: Long): Usuario?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun total(): Int
}

@Dao
interface PermisoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(p: PermisoUsuario)

    @Query("SELECT * FROM permisos_usuario WHERE usuarioId = :uid")
    suspend fun deUsuario(uid: Long): List<PermisoUsuario>

    @Query("DELETE FROM permisos_usuario WHERE usuarioId = :uid")
    suspend fun borrarDeUsuario(uid: Long)
}

@Dao
interface EstadisticasDao {
    @Query("SELECT COUNT(*) FROM personas")
    suspend fun totalPersonas(): Int

    @Query("SELECT COUNT(*) FROM personas WHERE sexo = 'F'")
    suspend fun damas(): Int

    @Query("SELECT COUNT(*) FROM personas WHERE sexo = 'M'")
    suspend fun caballeros(): Int

    @Query("SELECT COUNT(*) FROM personas WHERE departamento LIKE '%JÓVENES%'")
    suspend fun jovenes(): Int

    @Query("SELECT COUNT(*) FROM personas WHERE departamento LIKE '%3RA EDAD%'")
    suspend fun terceraEdad(): Int

    @Query("SELECT COUNT(*) FROM grupos")
    suspend fun totalGrupos(): Int

    @Query("SELECT COALESCE(SUM(monto), 0) FROM ingresos WHERE fecha LIKE :prefijo || '%'")
    suspend fun ingresosMes(prefijo: String): Double

    @Query("SELECT COALESCE(SUM(monto), 0) FROM gastos WHERE fecha LIKE :prefijo || '%'")
    suspend fun gastosMes(prefijo: String): Double

    @Query("SELECT COALESCE(SUM(monto), 0) FROM ingresos")
    suspend fun totalIngresos(): Double

    @Query("SELECT COALESCE(SUM(monto), 0) FROM gastos")
    suspend fun totalGastos(): Double
}
