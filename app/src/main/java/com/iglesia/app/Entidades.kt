package com.iglesia.app

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios", indices = [Index(value = ["usuario"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuario: String,
    val hash: String,
    val sal: String,
    val rol: String,
    val activo: Int = 1
)

@Entity(tableName = "permisos_usuario", indices = [Index(value = ["usuarioId", "modulo"], unique = true)])
data class PermisoUsuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val modulo: String,
    val nivel: String
)

@Entity(tableName = "personas")
data class Persona(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombres: String,
    val apellidos: String,
    val telefono: String = "",
    val correo: String = "",
    val fechaNacimiento: String? = null,
    val estadoMembresia: String = "activo",
    val fechaBautismo: String? = null,
    val carnet: String = "",
    val profesion: String = "",
    val estadoCivil: String = "",
    val sexo: String = "",
    val departamento: String = "",
    val membresia: String = "",
    val predicador: Int = 0,
    val bautizadoEspiritu: Int = 0
)

@Entity(tableName = "grupos")
data class Grupo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipo: String = ""
)

@Entity(tableName = "grupo_miembros", primaryKeys = ["grupoId", "personaId"])
data class GrupoMiembro(
    val grupoId: Long,
    val personaId: Long,
    val rol: String = "miembro"
)

@Entity(tableName = "tipos_culto")
data class TipoCulto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val diaSemana: Int,
    val hora: String? = null,
    val activo: Int = 1
)

@Entity(tableName = "cultos")
data class Culto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipoCultoId: Long? = null,
    val nombre: String,
    val fecha: String,
    val hora: String? = null,
    val estado: String = "programado",
    val esCampana: Int = 0,
    val campanaNombre: String? = null
)

@Entity(tableName = "culto_ambitos", indices = [Index(value = ["cultoId", "ambitoValor"], unique = true)])
data class CultoAmbito(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cultoId: Long,
    val ambitoValor: String,
    val encargadoUsuarioId: Long? = null,
    val estadoAsistencia: String = "pendiente"
)

@Entity(tableName = "asistencias_culto", indices = [Index(value = ["cultoId", "ambitoValor", "personaId"], unique = true)])
data class AsistenciaCulto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cultoId: Long,
    val ambitoValor: String,
    val personaId: Long,
    val estado: String,
    val registradoPor: Long? = null
)

@Entity(tableName = "alertas_atendidas")
data class AlertaAtendida(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personaId: Long,
    val usuarioId: Long? = null,
    val fechaContacto: String,
    val fechaRevision: String,
    val notas: String? = null
)

@Entity(tableName = "fondos", indices = [Index(value = ["nombre"], unique = true)])
data class Fondo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipo: String = "no_restringido",
    val descripcion: String = "",
    val activo: Int = 1
)

@Entity(tableName = "ingresos")
data class Ingreso(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fondoId: Long,
    val personaId: Long? = null,
    val fecha: String,
    val monto: Double,
    val categoria: String = "",
    val formaPago: String = "efectivo",
    val descripcion: String = ""
)

@Entity(tableName = "gastos")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fondoId: Long,
    val fecha: String,
    val monto: Double,
    val categoria: String = "",
    val beneficiario: String = "",
    val formaPago: String = "efectivo",
    val comprobante: String = "",
    val descripcion: String = ""
)

@Entity(tableName = "presupuesto", indices = [Index(value = ["fondoId", "anio", "mes"], unique = true)])
data class Presupuesto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fondoId: Long,
    val anio: Int,
    val mes: Int,
    val montoGasto: Double = 0.0,
    val montoIngreso: Double = 0.0
)

@Entity(tableName = "cierre_caja")
data class CierreCaja(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fecha: String,
    val tipoEvento: String? = null,
    val montoSistema: Double,
    val montoContado: Double,
    val diferencia: Double,
    val notas: String? = null
)
