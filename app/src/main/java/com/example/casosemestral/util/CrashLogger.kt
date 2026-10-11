package com.example.casosemestral.util

import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CrashLogger {

    private const val LOG_FOLDER_NAME = "crash_logs"

    fun getCrashLogsDirectory(context: Context): File {
        val dir = File(context.filesDir, LOG_FOLDER_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun saveCrashLog(context: Context, throwable: Throwable, tag: String = "AppException"): File? {
        return try {
            val dir = getCrashLogsDirectory(context)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(Date())
            val dateFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val logFile = File(dir, "crash_$timeStamp.txt")

            val stackTraceString = Log.getStackTraceString(throwable)

            val content = """
                ==================================================
                REPORTE DE CRASH / EXCEPCIÓN DETECTADA
                ==================================================
                Fecha y Hora : $dateFormatted
                Origen / Tag : $tag
                Dispositivo  : ${Build.MANUFACTURER} ${Build.MODEL} (Android SDK ${Build.VERSION.SDK_INT})
                Mensaje      : ${throwable.localizedMessage ?: "Sin mensaje de error"}
                --------------------------------------------------
                STACK TRACE COMPLETO:
                $stackTraceString
                ==================================================
            """.trimIndent()

            logFile.writeText(content)
            Log.i("CrashLogger", "Informe de crash guardado con éxito en: ${logFile.absolutePath}")
            logFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAllCrashLogs(context: Context): List<File> {
        return try {
            val dir = getCrashLogsDirectory(context)
            dir.listFiles()?.filter { it.isFile && it.name.startsWith("crash_") }?.sortedByDescending { it.lastModified() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun readCrashLogContent(file: File): String {
        return try {
            if (file.exists()) file.readText() else "El archivo no existe."
        } catch (e: Exception) {
            "Error al leer el reporte: ${e.localizedMessage}"
        }
    }

    fun clearAllCrashLogs(context: Context): Boolean {
        return try {
            val dir = getCrashLogsDirectory(context)
            dir.listFiles()?.forEach { it.delete() }
            true
        } catch (e: Exception) {
            false
        }
    }
}
