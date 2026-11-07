package com.iglesia.app

import java.security.MessageDigest
import java.security.SecureRandom

object Modulos {
    val lista: Map<String, String> = linkedMapOf(
        "miembros" to "Miembros",
        "grupos" to "Grupos",
        "cultos_administrar" to "Cultos (programación)",
        "asistencia_tomar" to "Toma de asistencia",
        "tesoreria_panel" to "Panel de tesorería",
        "movimientos" to "Movimientos",
        "fondos" to "Fondos",
        "estadisticas_miembros" to "Estadísticas de miembros",
        "reportes_financieros" to "Reportes financieros",
        "presupuesto" to "Presupuesto",
        "cierre_caja" to "Cierre de caja",
        "resumen_donantes" to "Resumen de donantes",
        "respaldo" to "Respaldo",
        "usuarios" to "Gestión de usuarios"
    )

    val roles: Map<String, String> = linkedMapOf(
        "administrador" to "Administrador",
        "secretaria" to "Secretaría",
        "tesorero" to "Tesorero",
        "pastor" to "Pastor",
        "encargado" to "Encargado de asistencia"
    )
}

fun permisosPorDefecto(rol: String): Map<String, String> {
    val mods = Modulos.lista.keys
    val p = mods.associateWith { "denegado" }.toMutableMap()
    when (rol) {
        "administrador" -> mods.forEach { p[it] = "completo" }
        "secretaria" -> listOf(
            "miembros", "grupos", "cultos_administrar", "asistencia_tomar", "estadisticas_miembros"
        ).forEach { p[it] = "completo" }
        "tesorero" -> {
            p["miembros"] = "lectura"
            listOf(
                "tesoreria_panel", "movimientos", "fondos", "reportes_financieros",
                "presupuesto", "cierre_caja", "resumen_donantes"
            ).forEach { p[it] = "completo" }
        }
        "pastor" -> {
            mods.forEach { p[it] = "lectura" }
            p["respaldo"] = "denegado"
            p["usuarios"] = "denegado"
        }
        "encargado" -> {
            p["asistencia_tomar"] = "completo"
        }
    }
    return p
}

object Clave {
    fun nuevaSal(): String {
        val b = ByteArray(16)
        SecureRandom().nextBytes(b)
        return b.joinToString("") { "%02x".format(it) }
    }

    fun hash(clave: String, sal: String): String {
        var d = (sal + clave).toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        repeat(10000) { d = md.digest(d) }
        return d.joinToString("") { "%02x".format(it) }
    }
}

object Sesion {
    var usuarioId: Long = 0
    var nombre: String = ""
    var rol: String = ""
    private var permisos: Map<String, String> = emptyMap()

    fun iniciar(u: Usuario, p: Map<String, String>) {
        usuarioId = u.id
        nombre = u.usuario
        rol = u.rol
        permisos = p
    }

    fun cerrar() {
        usuarioId = 0
        nombre = ""
        rol = ""
        permisos = emptyMap()
    }

    fun nivel(modulo: String): String = permisos[modulo] ?: "denegado"
    fun puedeVer(modulo: String): Boolean = nivel(modulo) == "completo" || nivel(modulo) == "lectura"
    fun puedeEditar(modulo: String): Boolean = nivel(modulo) == "completo"
}

suspend fun guardarPermisos(db: BaseDatos, usuarioId: Long, mapa: Map<String, String>) {
    db.permisoDao().borrarDeUsuario(usuarioId)
    for ((modulo, nivel) in mapa) {
        db.permisoDao().insertar(PermisoUsuario(usuarioId = usuarioId, modulo = modulo, nivel = nivel))
    }
}

suspend fun asegurarAdmin(db: BaseDatos) {
    if (db.usuarioDao().total() == 0) {
        val sal = Clave.nuevaSal()
        val id = db.usuarioDao().insertar(
            Usuario(usuario = "admin", hash = Clave.hash("admin123", sal), sal = sal, rol = "administrador")
        )
        guardarPermisos(db, id, permisosPorDefecto("administrador"))
    }
}

suspend fun iniciarSesion(db: BaseDatos, usuario: String, clave: String): String? {
    val u = db.usuarioDao().porNombre(usuario)
    if (u == null || Clave.hash(clave, u.sal) != u.hash) {
        return "Usuario o contraseña incorrectos."
    }
    if (u.activo == 0) {
        return "Esta cuenta está desactivada. Contacte al administrador."
    }
    var permisos: Map<String, String> = db.permisoDao().deUsuario(u.id).associate { it.modulo to it.nivel }
    if (permisos.isEmpty()) permisos = permisosPorDefecto(u.rol)
    Sesion.iniciar(u, permisos)
    return null
}
