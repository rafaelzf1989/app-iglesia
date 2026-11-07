package com.iglesia.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Usuario::class, PermisoUsuario::class, Persona::class, Grupo::class,
        GrupoMiembro::class, TipoCulto::class, Culto::class, CultoAmbito::class,
        AsistenciaCulto::class, AlertaAtendida::class, Fondo::class, Ingreso::class,
        Gasto::class, Presupuesto::class, CierreCaja::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BaseDatos : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun permisoDao(): PermisoDao
    abstract fun estadisticasDao(): EstadisticasDao

    companion object {
        @Volatile
        private var instancia: BaseDatos? = null

        fun obtener(contexto: Context): BaseDatos {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    BaseDatos::class.java,
                    "iglesia.db"
                ).build().also { instancia = it }
            }
        }
    }
}
