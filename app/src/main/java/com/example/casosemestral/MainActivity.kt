package com.example.casosemestral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.casosemestral.ui.theme.CasoSemestralTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CasoSemestralTheme {
                var isUserLoggedIn by remember { mutableStateOf(false) }
                var userProfileUrl by remember { mutableStateOf<String?>(null) }
                val onProfileClick = {
                    isUserLoggedIn = !isUserLoggedIn
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Mi Aplicación") },
                            actions = {
                                ProfileIconButton(
                                    isLoggedIn = isUserLoggedIn,
                                    imageUrl = userProfileUrl,
                                    onClick = onProfileClick
                                )
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.generic_background),
                            contentDescription = "Background",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        PrincipalScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileIconButton(
    isLoggedIn: Boolean,
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseModifier = modifier
        .padding(end = 16.dp)
        .size(40.dp)
        .clip(CircleShape)
        .clickable { onClick() }

    if (isLoggedIn && !imageUrl.isNullOrEmpty()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = baseModifier.border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Iniciar sesión",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = baseModifier
        )
    }
}

@Composable
fun Vista1(onNavegarAVista2: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Promovemos la inclusión laboral y social de personas con discapacidad múltiple.\n\n" +
                        "Propuesta de valor: Conecta con nuestra causa, descubre productos con sentido y sé parte del cambio con tu empresa o donación.",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { onNavegarAVista2() }) {
                Text(text = "Ir a vista 2")
            }
        }
    }
}

@Composable
fun Vista2(onNavegarAVista3: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Vista 2",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { onNavegarAVista3() }) {
                Text(text = "Ir a vista 3")
            }
        }
    }
}

@Composable
fun Vista3(onNavegarAVista1: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Vista 3",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { onNavegarAVista1() }) {
                Text(text = "Ir a vista 1")
            }
        }
    }
}

@Composable
fun PrincipalScreen(modifier: Modifier = Modifier) {
    var vistaActual by remember { mutableIntStateOf(1) }

    Column(modifier = modifier.fillMaxSize()) {
        when (vistaActual) {
            1 -> Vista1(onNavegarAVista2 = { vistaActual = 2 })
            2 -> Vista2(onNavegarAVista3 = { vistaActual = 3 })
            3 -> Vista3(onNavegarAVista1 = { vistaActual = 1 })
            else -> Vista1(onNavegarAVista2 = { vistaActual = 2 })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CasoSemestralTheme {
        PrincipalScreen()
    }
}