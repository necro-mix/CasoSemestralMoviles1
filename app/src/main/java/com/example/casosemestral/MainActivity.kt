package com.example.casosemestral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.casosemestral.data.UserRole
import com.example.casosemestral.ui.CorpalivViewModel
import com.example.casosemestral.ui.components.LoginModal
import com.example.casosemestral.ui.screens.CatalogoScreen
import com.example.casosemestral.ui.screens.ContactoScreen
import com.example.casosemestral.ui.screens.HistoriasScreen
import com.example.casosemestral.ui.screens.InicioScreen
import com.example.casosemestral.ui.screens.SolicitudesAdminScreen
import com.example.casosemestral.ui.theme.CasoSemestralTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CasoSemestralTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(corpalivViewModel: CorpalivViewModel = viewModel()) {
    val uiState by corpalivViewModel.uiState.collectAsState()
    var showLoginSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Fundación Salud & Vida",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = if (uiState.currentUserRole == UserRole.ADMIN_TEAM) "Modo Admin: Equipo de Vinculación" else "Donaciones Médicas & Ayuda Social",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    ProfileIconButton(
                        isLoggedIn = uiState.isLoggedIn,
                        imageUrl = uiState.userProfileUrl,
                        role = uiState.currentUserRole,
                        onClick = { showLoginSheet = true }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = uiState.selectedTab == 0,
                    onClick = { corpalivViewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 1,
                    onClick = { corpalivViewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.MedicalServices, contentDescription = "Causas Médicas") },
                    label = { Text("Causas", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 2,
                    onClick = { corpalivViewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Historias") },
                    label = { Text("Historias", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 3,
                    onClick = { corpalivViewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.VolunteerActivism, contentDescription = "Donar") },
                    label = { Text("Donar", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                if (uiState.currentUserRole == UserRole.ADMIN_TEAM) {
                    NavigationBarItem(
                        selected = uiState.selectedTab == 4,
                        onClick = { corpalivViewModel.selectTab(4) },
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Gestión") },
                        label = { Text("Gestión", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.LightGray
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.generic_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier.padding(innerPadding)) {
                AnimatedContent(
                    targetState = uiState.selectedTab,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                                animationSpec = tween(300),
                                initialOffsetX = { fullWidth -> fullWidth / 3 }
                            )).togetherWith(
                                fadeOut(animationSpec = tween(200)) + slideOutHorizontally(
                                    animationSpec = tween(200),
                                    targetOffsetX = { fullWidth -> -fullWidth / 3 }
                                )
                            )
                        } else {
                            (fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                                animationSpec = tween(300),
                                initialOffsetX = { fullWidth -> -fullWidth / 3 }
                            )).togetherWith(
                                fadeOut(animationSpec = tween(200)) + slideOutHorizontally(
                                    animationSpec = tween(200),
                                    targetOffsetX = { fullWidth -> fullWidth / 3 }
                                )
                            )
                        }
                    },
                    label = "TabTransition"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> InicioScreen(
                            onNavigateToCatalogo = { corpalivViewModel.selectTab(1) },
                            onNavigateToContacto = { corpalivViewModel.selectTab(3) }
                        )
                        1 -> CatalogoScreen(
                            selectedCategory = uiState.selectedCatalogCategory,
                            onCategorySelected = { category -> corpalivViewModel.setCatalogCategory(category) },
                            onProductInquiry = { product -> corpalivViewModel.initiateProductInquiry(product) },
                            onProgramInquiry = { program -> corpalivViewModel.initiateProgramInquiry(program) }
                        )
                        2 -> HistoriasScreen()
                        3 -> ContactoScreen(
                            uiState = uiState,
                            onNameChanged = { corpalivViewModel.updateFormName(it) },
                            onEmailChanged = { corpalivViewModel.updateFormEmail(it) },
                            onPhoneChanged = { corpalivViewModel.updateFormPhone(it) },
                            onOrganizationChanged = { corpalivViewModel.updateFormOrganization(it) },
                            onTypeChanged = { corpalivViewModel.updateFormType(it) },
                            onMessageChanged = { corpalivViewModel.updateFormMessage(it) },
                            onSubmit = { corpalivViewModel.submitInterestRegistration() },
                            onDismissSuccessDialog = { corpalivViewModel.resetFormSubmission() }
                        )
                        4 -> SolicitudesAdminScreen(
                            registrations = uiState.receivedRegistrations,
                            onMarkAsContacted = { id -> corpalivViewModel.markAsContacted(id) },
                            onDeleteRegistration = { id -> corpalivViewModel.eliminarRegistroInteres(id) }
                        )
                        else -> InicioScreen(
                            onNavigateToCatalogo = { corpalivViewModel.selectTab(1) },
                            onNavigateToContacto = { corpalivViewModel.selectTab(3) }
                        )
                    }
                }
            }
        }
    }

    if (showLoginSheet) {
        LoginModal(
            sheetState = sheetState,
            isLoggedIn = uiState.isLoggedIn,
            currentUserRole = uiState.currentUserRole,
            currentUserName = uiState.userProfileName,
            errorMessage = uiState.authErrorMessage,
            onDismiss = {
                showLoginSheet = false
                corpalivViewModel.clearAuthErrorMessage()
            },
            onLogin = { email, password ->
                val success = corpalivViewModel.autenticarUsuario(email, password)
                if (success && corpalivViewModel.uiState.value.currentUserRole == UserRole.ADMIN_TEAM) {
                    corpalivViewModel.selectTab(4)
                }
                success
            },
            onRegister = { nombre, email, password, role ->
                val success = corpalivViewModel.registrarNuevoUsuario(nombre, email, password, role)
                if (success && role == UserRole.ADMIN_TEAM) {
                    corpalivViewModel.selectTab(4)
                }
                success
            },
            onLogout = {
                corpalivViewModel.logout()
                corpalivViewModel.selectTab(0)
            },
            onClearError = { corpalivViewModel.clearAuthErrorMessage() }
        )
    }
}

@Composable
fun ProfileIconButton(
    isLoggedIn: Boolean,
    imageUrl: String?,
    role: UserRole,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseModifier = modifier
        .padding(end = 16.dp)
        .size(42.dp)
        .clip(CircleShape)
        .clickable { onClick() }

    if (isLoggedIn && !imageUrl.isNullOrEmpty()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Perfil",
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_launcher_foreground),
            modifier = baseModifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    } else {
        Icon(
            imageVector = if (role == UserRole.ADMIN_TEAM) Icons.Default.AdminPanelSettings else Icons.Default.AccountCircle,
            contentDescription = "Iniciar sesión / Perfil",
            tint = MaterialTheme.colorScheme.primary,
            modifier = baseModifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CasoSemestralTheme {
        MainApp()
    }
}
