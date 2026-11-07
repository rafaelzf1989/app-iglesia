package com.iglesia.app

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.unit.dp

@Composable
fun PantallaLogin(db: BaseDatos, alEntrar: () -> Unit) {
    val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("ajustes", Context.MODE_PRIVATE) }
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Gestión de Iglesia",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text("Iglesia Alfa y Omega", color = Gris)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        CampoClave("Contraseña", clave) { clave = it }
        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }
        Button(
            onClick = {
                scope.launch {
                    val r = iniciarSesion(db, usuario.trim(), clave)
                    if (r == null) alEntrar() else error = r
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Entrar") }
        if (!prefs.getBoolean("clave_cambiada", false)) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Primer ingreso: usuario admin, contraseña admin123. Cámbiela al entrar.",
                color = Gris
            )
        }
    }
}

@Composable
fun PantallaInicio(db: BaseDatos, alNavegar: (Pantalla) -> Unit, alCerrarSesion: () -> Unit) {
    val ctx = LocalContext.current
    var datos by remember { mutableStateOf<List<Dato>>(emptyList()) }

    LaunchedEffect(Unit) {
        val e = db.estadisticasDao()
        val mes = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
        val ing = e.ingresosMes(mes)
        val gas = e.gastosMes(mes)
        val saldo = e.totalIngresos() - e.totalGastos()
        datos = listOf(
            Dato("Total de miembros", "${e.totalPersonas()}", Azul),
            Dato("Damas", "${e.damas()}", Verde),
            Dato("Caballeros", "${e.caballeros()}", Verde),
            Dato("Jóvenes", "${e.jovenes()}", Ambar),
            Dato("Tercera edad", "${e.terceraEdad()}", Ambar),
            Dato("Grupos", "${e.totalGrupos()}", Ambar),
            Dato("Ingresos del mes", "%.2f".format(ing), Verde),
            Dato("Gastos del mes", "%.2f".format(gas), Rojo),
            Dato("Saldo general", "%.2f".format(saldo), if (saldo >= 0) Azul else Rojo)
        )
    }

    Marco("Panel de inicio") {
        Text("Hola, ${Sesion.nombre}", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        CuadriculaDatos(datos)
        Spacer(Modifier.height(8.dp))
        Text(
            "Módulos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        for (item in itemsMenu) {
            if (Sesion.puedeVer(item.modulo)) {
                OutlinedButton(
                    onClick = {
                        if (item.destino != null) {
                            alNavegar(item.destino)
                        } else {
                            Toast.makeText(ctx, "Disponible en la Fase ${item.fase}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) { Text(item.etiqueta) }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { alNavegar(Pantalla.CambiarClave) },
            colors = ButtonDefaults.buttonColors(containerColor = Gris),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cambiar contraseña") }
        Spacer(Modifier.height(6.dp))
        Button(
            onClick = alCerrarSesion,
            colors = ButtonDefaults.buttonColors(containerColor = Rojo),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cerrar sesión") }
    }
}

@Composable
fun PantallaCambiarClave(db: BaseDatos, alVolver: () -> Unit) {
    val ctx = LocalContext.current
    var actual by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var esError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Marco("Cambiar contraseña", alVolver) {
        Text("Usuario: ${Sesion.nombre}", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        CampoClave("Contraseña actual", actual) { actual = it }
        CampoClave("Nueva contraseña (mínimo 6 caracteres)", nueva) { nueva = it }
        CampoClave("Confirmar nueva contraseña", confirmar) { confirmar = it }
        if (mensaje.isNotEmpty()) {
            Text(mensaje, color = if (esError) MaterialTheme.colorScheme.error else Color(0xFF059669))
            Spacer(Modifier.height(8.dp))
        }
        Button(
            onClick = {
                scope.launch {
                    val u = db.usuarioDao().porId(Sesion.usuarioId)
                    if (u == null || Clave.hash(actual, u.sal) != u.hash) {
                        mensaje = "La contraseña actual no es correcta."
                        esError = true
                        return@launch
                    }
                    if (nueva.length < 6) {
                        mensaje = "La nueva contraseña debe tener al menos 6 caracteres."
                        esError = true
                        return@launch
                    }
                    if (nueva != confirmar) {
                        mensaje = "La nueva contraseña y su confirmación no coinciden."
                        esError = true
                        return@launch
                    }
                    val sal = Clave.nuevaSal()
                    db.usuarioDao().actualizar(u.copy(hash = Clave.hash(nueva, sal), sal = sal))
                    ctx.getSharedPreferences("ajustes", Context.MODE_PRIVATE)
                        .edit().putBoolean("clave_cambiada", true).apply()
                    actual = ""
                    nueva = ""
                    confirmar = ""
                    mensaje = "Contraseña actualizada correctamente."
                    esError = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar nueva contraseña") }
    }
}
