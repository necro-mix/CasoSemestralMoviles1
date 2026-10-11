package com.example.casosemestral.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.casosemestral.data.UserRole
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginModal(
    sheetState: SheetState,
    isLoggedIn: Boolean,
    currentUserRole: UserRole,
    currentUserName: String,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onLogin: (email: String, clave: String) -> Boolean,
    onRegister: (nombre: String, email: String, clave: String, role: UserRole) -> Boolean,
    onLogout: () -> Unit,
    onClearError: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val safeDismiss = {
        coroutineScope.launch {
            try {
                sheetState.hide()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                onDismiss()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoggedIn) {
                Icon(
                    imageVector = if (currentUserRole == UserRole.ADMIN_TEAM) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Sesión Activa",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currentUserName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = if (currentUserRole == UserRole.ADMIN_TEAM) "Perfil: Equipo de Vinculación (Admin)" else "Perfil: Donante / Empresa Colaboradora",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onLogout()
                        safeDismiss()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                }

            } else {
                var selectedTabIndex by remember { mutableIntStateOf(0) }

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = {
                            selectedTabIndex = 0
                            onClearError()
                        },
                        text = { Text("Iniciar Sesión", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = {
                            selectedTabIndex = 1
                            onClearError()
                        },
                        text = { Text("Crear Cuenta", fontWeight = FontWeight.Bold) }
                    )
                }

                if (selectedTabIndex == 0) {
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var selectedRole by remember { mutableStateOf<UserRole>(UserRole.DONOR) }

                    Text(
                        text = "Atajos de credenciales de prueba:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedRole = UserRole.DONOR
                                email = "donante@fundacion.cl"
                                password = "123"
                                onClearError()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = if (selectedRole == UserRole.DONOR) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Donante", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                selectedRole = UserRole.ADMIN_TEAM
                                email = "equipo.vinculacion@fundacion.cl"
                                password = "123"
                                onClearError()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = if (selectedRole == UserRole.ADMIN_TEAM) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Equipo Admin", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val success = try {
                                onLogin(email, password)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                false
                            }
                            if (success) {
                                safeDismiss()
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Ingresar", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                } else {
                    var regNombre by remember { mutableStateOf("") }
                    var regEmail by remember { mutableStateOf("") }
                    var regPassword by remember { mutableStateOf("") }
                    var regConfirmPassword by remember { mutableStateOf("") }
                    var regRole by remember { mutableStateOf<UserRole>(UserRole.DONOR) }
                    var localError by remember { mutableStateOf<String?>(null) }

                    OutlinedTextField(
                        value = regNombre,
                        onValueChange = { regNombre = it },
                        label = { Text("Nombre Completo *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Correo Electrónico *") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it },
                        label = { Text("Contraseña *") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = regConfirmPassword,
                        onValueChange = { regConfirmPassword = it },
                        label = { Text("Confirmar Contraseña *") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Selecciona tu perfil de usuario:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { regRole = UserRole.DONOR },
                            shape = RoundedCornerShape(12.dp),
                            colors = if (regRole == UserRole.DONOR) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Donante", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { regRole = UserRole.ADMIN_TEAM },
                            shape = RoundedCornerShape(12.dp),
                            colors = if (regRole == UserRole.ADMIN_TEAM) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Equipo Admin", fontSize = 11.sp)
                        }
                    }

                    val activeError = localError ?: errorMessage
                    if (activeError != null) {
                        Text(
                            text = activeError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (regNombre.isBlank() || regEmail.isBlank() || regPassword.isBlank()) {
                                localError = "Por favor completa todos los campos obligatorios."
                                return@Button
                            }
                            if (regPassword != regConfirmPassword) {
                                localError = "Las contraseñas no coinciden."
                                return@Button
                            }
                            localError = null
                            val success = try {
                                onRegister(regNombre, regEmail, regPassword, regRole)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                false
                            }
                            if (success) {
                                safeDismiss()
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Registrarse", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { safeDismiss() }) {
                    Text("Cancelar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
