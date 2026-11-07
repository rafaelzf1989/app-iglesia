package com.iglesia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

enum class Pantalla { Cargando, Login, Inicio, CambiarClave }

class ItemMenu(
    val modulo: String,
    val etiqueta: String,
    val fase: Int,
    val destino: Pantalla? = null
)

val itemsMenu = listOf(
    ItemMenu("miembros", "Miembros", 2),
    ItemMenu("grupos", "Grupos y ministerios", 2),
    ItemMenu("estadisticas_miembros", "Estadísticas de miembros", 2),
    ItemMenu("cultos_administrar", "Tipos de culto y cultos programados", 3),
    ItemMenu("cultos_administrar", "Alertas de ausencia", 3),
    ItemMenu("asistencia_tomar", "Mi asistencia", 3),
    ItemMenu("tesoreria_panel", "Panel de tesorería", 4),
    ItemMenu("movimientos", "Movimientos", 4),
    ItemMenu("fondos", "Fondos", 4),
    ItemMenu("reportes_financieros", "Reportes financieros", 4),
    ItemMenu("presupuesto", "Presupuesto", 4),
    ItemMenu("cierre_caja", "Cierre de caja", 4),
    ItemMenu("resumen_donantes", "Resumen de donantes", 4),
    ItemMenu("usuarios", "Gestión de usuarios", 5),
    ItemMenu("respaldo", "Respaldo", 5)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TemaIglesia { Raiz() } }
    }
}

@Composable
fun Raiz() {
    val contexto = LocalContext.current
    val db = remember { BaseDatos.obtener(contexto) }
    var pantalla by remember { mutableStateOf(Pantalla.Cargando) }

    LaunchedEffect(Unit) {
        asegurarAdmin(db)
        pantalla = Pantalla.Login
    }

    BackHandler(enabled = pantalla == Pantalla.CambiarClave) {
        pantalla = Pantalla.Inicio
    }

    when (pantalla) {
        Pantalla.Cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        Pantalla.Login -> PantallaLogin(db) { pantalla = Pantalla.Inicio }
        Pantalla.Inicio -> PantallaInicio(
            db,
            alNavegar = { pantalla = it },
            alCerrarSesion = {
                Sesion.cerrar()
                pantalla = Pantalla.Login
            }
        )
        Pantalla.CambiarClave -> PantallaCambiarClave(db) { pantalla = Pantalla.Inicio }
    }
}
