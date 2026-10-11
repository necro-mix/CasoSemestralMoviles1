package com.example.casosemestral.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.casosemestral.util.CrashLogger
import com.example.casosemestral.util.PasswordHasher

data class UserAccount(
    val id: Long,
    val email: String,
    val nombre: String,
    val rol: UserRole
)

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "fundacion_salud_v6.db"
        private const val DATABASE_VERSION = 6

        private const val TABLE_REGISTROS = "registros_interes"
        private const val COLUMN_REG_ID = "id"
        private const val COLUMN_REG_NOMBRE = "nombre"
        private const val COLUMN_REG_EMAIL = "email"
        private const val COLUMN_REG_TELEFONO = "telefono"
        private const val COLUMN_REG_ORGANIZACION = "organizacion"
        private const val COLUMN_REG_TIPO = "tipo_colaboracion"
        private const val COLUMN_REG_MENSAJE = "mensaje"
        private const val COLUMN_REG_ESTADO = "estado"

        private const val TABLE_USUARIOS = "usuarios"
        private const val COLUMN_USR_ID = "id"
        private const val COLUMN_USR_EMAIL = "email"
        private const val COLUMN_USR_NOMBRE = "nombre"
        private const val COLUMN_USR_ROL = "rol"
        private const val COLUMN_USR_CLAVE = "clave"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            val createRegistrosTable = """
                CREATE TABLE $TABLE_REGISTROS (
                    $COLUMN_REG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_REG_NOMBRE TEXT NOT NULL,
                    $COLUMN_REG_EMAIL TEXT NOT NULL,
                    $COLUMN_REG_TELEFONO TEXT,
                    $COLUMN_REG_ORGANIZACION TEXT,
                    $COLUMN_REG_TIPO TEXT NOT NULL,
                    $COLUMN_REG_MENSAJE TEXT,
                    $COLUMN_REG_ESTADO TEXT DEFAULT 'Pendiente'
                )
            """.trimIndent()

            val createUsuariosTable = """
                CREATE TABLE $TABLE_USUARIOS (
                    $COLUMN_USR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_USR_EMAIL TEXT UNIQUE NOT NULL,
                    $COLUMN_USR_NOMBRE TEXT NOT NULL,
                    $COLUMN_USR_ROL TEXT NOT NULL,
                    $COLUMN_USR_CLAVE TEXT NOT NULL
                )
            """.trimIndent()

            db.execSQL(createRegistrosTable)
            db.execSQL(createUsuariosTable)

            insertarUsuarioInicial(db, "donante@fundacion.cl", "Donante Colaborador", "DONOR", "123")
            insertarUsuarioInicial(db, "equipo.vinculacion@fundacion.cl", "Equipo de Vinculación (Admin)", "ADMIN_TEAM", "123")

            insertarRegistroInicial(
                db,
                "Empresa Innovación S.A.",
                "contacto@innovacion.cl",
                "+56 9 1234 5678",
                "Empresa Innovación S.A.",
                CollaborationType.ALIANZA_EMPRESA_SALUD.name,
                "Nos gustaría coordinar una alianza RSE de donación de equipos ortopédicos."
            )

            insertarRegistroInicial(
                db,
                "Dra. Camila Torres",
                "ctorres@salud.cl",
                "+56 9 8765 4321",
                "Voluntariado Médico",
                CollaborationType.VOLUNTARIADO_SALUD.name,
                "Deseo participar como médica voluntaria en los próximos operativos territoriales."
            )
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(context, t, "DatabaseHelper_onCreate")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_REGISTROS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
            onCreate(db)
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(context, t, "DatabaseHelper_onUpgrade")
        }
    }

    private fun insertarUsuarioInicial(db: SQLiteDatabase, email: String, nombre: String, rol: String, clavePlana: String) {
        try {
            val claveCifrada = PasswordHasher.hashPassword(clavePlana)
            val values = ContentValues().apply {
                put(COLUMN_USR_EMAIL, email)
                put(COLUMN_USR_NOMBRE, nombre)
                put(COLUMN_USR_ROL, rol)
                put(COLUMN_USR_CLAVE, claveCifrada)
            }
            db.insertWithOnConflict(TABLE_USUARIOS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(context, t, "DatabaseHelper_insertarUsuarioInicial")
        }
    }

    private fun insertarRegistroInicial(
        db: SQLiteDatabase,
        nombre: String,
        email: String,
        telefono: String,
        org: String,
        tipo: String,
        mensaje: String
    ) {
        try {
            val values = ContentValues().apply {
                put(COLUMN_REG_NOMBRE, nombre)
                put(COLUMN_REG_EMAIL, email)
                put(COLUMN_REG_TELEFONO, telefono)
                put(COLUMN_REG_ORGANIZACION, org)
                put(COLUMN_REG_TIPO, tipo)
                put(COLUMN_REG_MENSAJE, mensaje)
                put(COLUMN_REG_ESTADO, "Pendiente")
            }
            db.insert(TABLE_REGISTROS, null, values)
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(context, t, "DatabaseHelper_insertarRegistroInicial")
        }
    }

    fun registrarUsuario(nombre: String, email: String, clavePlana: String, rol: UserRole): Pair<Boolean, String?> {
        return try {
            val claveCifrada = PasswordHasher.hashPassword(clavePlana.trim())
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_USR_NOMBRE, nombre.trim())
                put(COLUMN_USR_EMAIL, email.trim().lowercase())
                put(COLUMN_USR_ROL, rol.name)
                put(COLUMN_USR_CLAVE, claveCifrada)
            }
            val result = db.insertWithOnConflict(TABLE_USUARIOS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
            if (result != -1L) {
                Pair(true, null)
            } else {
                Pair(false, "El correo '${email.trim()}' ya se encuentra registrado.")
            }
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(context, t, "DatabaseHelper_registrarUsuario")
            Pair(false, "Error al registrar usuario: ${t.localizedMessage}")
        }
    }

    fun autenticarUsuario(email: String, clavePlana: String): Pair<UserAccount?, String?> {
        return try {
            val db = readableDatabase
            val query = "SELECT * FROM $TABLE_USUARIOS WHERE LOWER($COLUMN_USR_EMAIL) = ?"
            val cursor = db.rawQuery(query, arrayOf(email.trim().lowercase()))

            cursor.use { c ->
                if (c.moveToFirst()) {
                    val idIndex = c.getColumnIndex(COLUMN_USR_ID)
                    val emailIndex = c.getColumnIndex(COLUMN_USR_EMAIL)
                    val nombreIndex = c.getColumnIndex(COLUMN_USR_NOMBRE)
                    val rolIndex = c.getColumnIndex(COLUMN_USR_ROL)
                    val claveIndex = c.getColumnIndex(COLUMN_USR_CLAVE)

                    val id = if (idIndex != -1) c.getLong(idIndex) else 0L
                    val usrEmail = if (emailIndex != -1) c.getString(emailIndex) ?: "" else ""
                    val usrNombre = if (nombreIndex != -1) c.getString(nombreIndex) ?: "" else ""
                    val usrRolStr = if (rolIndex != -1) c.getString(rolIndex) ?: "" else ""
                    val storedHash = if (claveIndex != -1) c.getString(claveIndex) ?: "" else ""

                    if (PasswordHasher.verifyPassword(clavePlana.trim(), storedHash)) {
                        val usrRol = try {
                            UserRole.valueOf(usrRolStr)
                        } catch (t: Throwable) {
                            UserRole.DONOR
                        }
                        return Pair(UserAccount(id = id, email = usrEmail, nombre = usrNombre, rol = usrRol), null)
                    } else {
                        return Pair(null, "Contraseña incorrecta.")
                    }
                }
            }
            Pair(null, "El correo ingresado no existe.")
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(context, t, "DatabaseHelper_autenticarUsuario")
            Pair(null, "Error al autenticar: ${t.localizedMessage}")
        }
    }

    fun insertarRegistroInteres(registration: InterestRegistration): Long {
        return try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_REG_NOMBRE, registration.fullName)
                put(COLUMN_REG_EMAIL, registration.email)
                put(COLUMN_REG_TELEFONO, registration.phone)
                put(COLUMN_REG_ORGANIZACION, registration.organization)
                put(COLUMN_REG_TIPO, registration.type.name)
                put(COLUMN_REG_MENSAJE, registration.message)
                put(COLUMN_REG_ESTADO, if (registration.isContacted) "Contactado" else "Pendiente")
            }
            db.insert(TABLE_REGISTROS, null, values)
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(context, t, "DatabaseHelper_insertarRegistroInteres")
            -1L
        }
    }

    fun obtenerTodosLosRegistros(): List<InterestRegistration> {
        val list = mutableListOf<InterestRegistration>()
        return try {
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TABLE_REGISTROS ORDER BY $COLUMN_REG_ID DESC", null)

            cursor.use { c ->
                val idIndex = c.getColumnIndex(COLUMN_REG_ID)
                val nombreIndex = c.getColumnIndex(COLUMN_REG_NOMBRE)
                val emailIndex = c.getColumnIndex(COLUMN_REG_EMAIL)
                val telefonoIndex = c.getColumnIndex(COLUMN_REG_TELEFONO)
                val orgIndex = c.getColumnIndex(COLUMN_REG_ORGANIZACION)
                val tipoIndex = c.getColumnIndex(COLUMN_REG_TIPO)
                val mensajeIndex = c.getColumnIndex(COLUMN_REG_MENSAJE)
                val estadoIndex = c.getColumnIndex(COLUMN_REG_ESTADO)

                while (c.moveToNext()) {
                    val id = if (idIndex != -1) c.getLong(idIndex) else 0L
                    val nombre = if (nombreIndex != -1) c.getString(nombreIndex) ?: "" else ""
                    val email = if (emailIndex != -1) c.getString(emailIndex) ?: "" else ""
                    val telefono = if (telefonoIndex != -1) c.getString(telefonoIndex) ?: "" else ""
                    val org = if (orgIndex != -1) c.getString(orgIndex) ?: "" else ""
                    val tipoStr = if (tipoIndex != -1) c.getString(tipoIndex) ?: "" else ""
                    val mensaje = if (mensajeIndex != -1) c.getString(mensajeIndex) ?: "" else ""
                    val estado = if (estadoIndex != -1) c.getString(estadoIndex) ?: "" else ""

                    val tipoEnum = try {
                        CollaborationType.valueOf(tipoStr)
                    } catch (t: Throwable) {
                        CollaborationType.DONACION_TRATAMIENTO
                    }

                    list.add(
                        InterestRegistration(
                            id = id,
                            fullName = nombre,
                            email = email,
                            phone = telefono,
                            organization = org,
                            type = tipoEnum,
                            message = mensaje,
                            isContacted = estado == "Contactado"
                        )
                    )
                }
            }
            list
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(context, t, "DatabaseHelper_obtenerTodosLosRegistros")
            list
        }
    }

    fun actualizarEstadoContactado(id: Long) {
        try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_REG_ESTADO, "Contactado")
            }
            db.update(TABLE_REGISTROS, values, "$COLUMN_REG_ID = ?", arrayOf(id.toString()))
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(context, t, "DatabaseHelper_actualizarEstadoContactado")
        }
    }

    fun eliminarRegistroInteres(id: Long): Boolean {
        return try {
            val db = writableDatabase
            val count = db.delete(TABLE_REGISTROS, "$COLUMN_REG_ID = ?", arrayOf(id.toString()))
            count > 0
        } catch (t: Throwable) {
            CrashLogger.saveCrashLog(context, t, "DatabaseHelper_eliminarRegistroInteres")
            false
        }
    }
}
