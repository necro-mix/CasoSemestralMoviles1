package com.example.casosemestral

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.casosemestral.util.CrashLogger

class CasoSemestralApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // CTM DEJA DE CRAAAASHEEAAAAAR
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("CrashPrevention", "Se evitó el cierre de la app: ${throwable.localizedMessage}", throwable)

            // guardar log crasheo
            val savedFile = CrashLogger.saveCrashLog(applicationContext, throwable, "GlobalUncaughtHandler")

            Handler(Looper.getMainLooper()).post {
                val fileName = savedFile?.name ?: "crash.txt"
                Toast.makeText(
                    applicationContext,
                    "Se guardó el informe de error en la carpeta crash_logs ($fileName)",
                    Toast.LENGTH_LONG
                ).show()
            }

            try {
                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
            } catch (e: Exception) {
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }
}
