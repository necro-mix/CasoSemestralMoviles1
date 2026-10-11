package com.example.casosemestral.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.casosemestral.data.CollaborationType
import com.example.casosemestral.data.DatabaseHelper
import com.example.casosemestral.data.InterestRegistration
import com.example.casosemestral.data.Product
import com.example.casosemestral.data.Program
import com.example.casosemestral.data.UserRole
import com.example.casosemestral.util.CrashLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CorpalivUiState(
    val selectedTab: Int = 0,
    val selectedCatalogCategory: String = "Todos",
    val formState: InterestRegistration = InterestRegistration(),
    val isFormSubmitting: Boolean = false,
    val isFormSubmittedSuccessfully: Boolean = false,
    val formErrorMessage: String? = null,
    val selectedProductForInquiry: Product? = null,
    val selectedProgramForInquiry: Program? = null,
    val isLoggedIn: Boolean = false,
    val currentUserRole: UserRole = UserRole.GUEST,
    val userProfileName: String = "Invitado",
    val userProfileUrl: String? = null,
    val receivedRegistrations: List<InterestRegistration> = emptyList(),
    val authErrorMessage: String? = null
)

class CorpalivViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application.applicationContext)

    private val _uiState = MutableStateFlow(CorpalivUiState())
    val uiState: StateFlow<CorpalivUiState> = _uiState.asStateFlow()

    init {
        cargarRegistrosDesdeSQLite()
    }

    fun cargarRegistrosDesdeSQLite() {
        try {
            val list = dbHelper.obtenerTodosLosRegistros()
            _uiState.update { it.copy(receivedRegistrations = list) }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_cargarRegistros")
        }
    }

    fun selectTab(tabIndex: Int) {
        try {
            _uiState.update { it.copy(selectedTab = tabIndex) }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_selectTab")
        }
    }

    fun setCatalogCategory(category: String) {
        try {
            _uiState.update { it.copy(selectedCatalogCategory = category) }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_setCatalogCategory")
        }
    }

    fun updateFormName(name: String) {
        try {
            _uiState.update { it.copy(formState = it.formState.copy(fullName = name)) }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_updateFormName")
        }
    }

    fun updateFormEmail(email: String) {
        try {
            _uiState.update { it.copy(formState = it.formState.copy(email = email)) }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_updateFormEmail")
        }
    }

    fun updateFormPhone(phone: String) {
        try {
            _uiState.update { it.copy(formState = it.formState.copy(phone = phone)) }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_updateFormPhone")
        }
    }

    fun updateFormOrganization(org: String) {
        try {
            _uiState.update { it.copy(formState = it.formState.copy(organization = org)) }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_updateFormOrganization")
        }
    }

    fun updateFormType(type: CollaborationType) {
        try {
            _uiState.update { it.copy(formState = it.formState.copy(type = type)) }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_updateFormType")
        }
    }

    fun updateFormMessage(msg: String) {
        try {
            _uiState.update { it.copy(formState = it.formState.copy(message = msg)) }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_updateFormMessage")
        }
    }

    fun initiateProductInquiry(product: Product) {
        try {
            _uiState.update {
                it.copy(
                    selectedProductForInquiry = product,
                    formState = it.formState.copy(
                        type = CollaborationType.DONACION_EQUIPAMIENTO,
                        message = "Hola, deseo aportar / apadrinar la donación médica '${product.name}'."
                    ),
                    selectedTab = 3
                )
            }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_initiateProductInquiry")
        }
    }

    fun initiateProgramInquiry(program: Program) {
        try {
            _uiState.update {
                it.copy(
                    selectedProgramForInquiry = program,
                    formState = it.formState.copy(
                        type = CollaborationType.DONACION_TRATAMIENTO,
                        message = "Deseo recibir información para colaborar con el '${program.title}'."
                    ),
                    selectedTab = 3
                )
            }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_initiateProgramInquiry")
        }
    }

    fun submitInterestRegistration() {
        try {
            val state = _uiState.value.formState
            if (state.fullName.isBlank() || state.email.isBlank()) {
                _uiState.update { it.copy(formErrorMessage = "Por favor completa tu nombre y correo electrónico.") }
                return
            }

            _uiState.update { it.copy(isFormSubmitting = true, formErrorMessage = null) }

            dbHelper.insertarRegistroInteres(state)
            val updatedList = dbHelper.obtenerTodosLosRegistros()

            _uiState.update {
                it.copy(
                    isFormSubmitting = false,
                    isFormSubmittedSuccessfully = true,
                    receivedRegistrations = updatedList
                )
            }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_submitInterestRegistration")
            _uiState.update {
                it.copy(
                    isFormSubmitting = false,
                    formErrorMessage = "No se pudo guardar la solicitud."
                )
            }
        }
    }

    fun resetFormSubmission() {
        try {
            _uiState.update {
                it.copy(
                    isFormSubmittedSuccessfully = false,
                    formState = InterestRegistration(),
                    selectedProductForInquiry = null,
                    selectedProgramForInquiry = null
                )
            }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_resetFormSubmission")
        }
    }

    fun markAsContacted(registrationId: Long) {
        try {
            dbHelper.actualizarEstadoContactado(registrationId)
            cargarRegistrosDesdeSQLite()
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_markAsContacted")
        }
    }

    fun eliminarRegistroInteres(registrationId: Long) {
        try {
            dbHelper.eliminarRegistroInteres(registrationId)
            cargarRegistrosDesdeSQLite()
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_eliminarRegistroInteres")
        }
    }

    fun registrarNuevoUsuario(nombre: String, email: String, clave: String, role: UserRole): Boolean {
        return try {
            if (nombre.isBlank() || email.isBlank() || clave.isBlank()) {
                _uiState.update { it.copy(authErrorMessage = "Todos los campos son obligatorios.") }
                return false
            }

            val (success, error) = dbHelper.registrarUsuario(nombre, email, clave, role)
            if (success) {
                _uiState.update {
                    it.copy(
                        isLoggedIn = true,
                        currentUserRole = role,
                        userProfileName = nombre,
                        userProfileUrl = if (role == UserRole.ADMIN_TEAM) "https://picsum.photos/201" else "https://picsum.photos/200",
                        authErrorMessage = null
                    )
                }
                true
            } else {
                _uiState.update { it.copy(authErrorMessage = error ?: "No se pudo completar el registro.") }
                false
            }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_registrarNuevoUsuario")
            _uiState.update { it.copy(authErrorMessage = "Error: ${t.localizedMessage}") }
            false
        }
    }

    fun autenticarUsuario(email: String, clave: String): Boolean {
        return try {
            if (email.isBlank() || clave.isBlank()) {
                _uiState.update { it.copy(authErrorMessage = "Ingresa correo y contraseña.") }
                return false
            }

            val (user, error) = dbHelper.autenticarUsuario(email, clave)
            if (user != null) {
                _uiState.update {
                    it.copy(
                        isLoggedIn = true,
                        currentUserRole = user.rol,
                        userProfileName = user.nombre,
                        userProfileUrl = if (user.rol == UserRole.ADMIN_TEAM) "https://picsum.photos/201" else "https://picsum.photos/200",
                        authErrorMessage = null
                    )
                }
                true
            } else {
                _uiState.update { it.copy(authErrorMessage = error ?: "Credenciales incorrectas.") }
                false
            }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_autenticarUsuario")
            _uiState.update { it.copy(authErrorMessage = "Error: ${t.localizedMessage}") }
            false
        }
    }

    fun clearAuthErrorMessage() {
        try {
            _uiState.update { it.copy(authErrorMessage = null) }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_clearAuthErrorMessage")
        }
    }

    fun logout() {
        try {
            _uiState.update {
                it.copy(
                    isLoggedIn = false,
                    currentUserRole = UserRole.GUEST,
                    userProfileName = "Invitado",
                    userProfileUrl = null,
                    authErrorMessage = null
                )
            }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(getApplication(), t, "ViewModel_logout")
        }
    }
}
