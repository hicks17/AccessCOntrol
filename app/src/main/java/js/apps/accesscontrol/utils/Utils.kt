package js.apps.accesscontrol.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Utils {
    fun getCurrentFormattedDate(): String {
        val currentDate = LocalDate.now() // Obtiene la fecha actual
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // Define el formato
        return currentDate.format(formatter) // Formatea la fecha
    }

    fun getCurrentFormattedTime(): String {
        val currentTime = LocalTime.now() // Get current time
        val formatter = DateTimeFormatter.ofPattern("HH:mm") // Define time format
        return currentTime.format(formatter) // Format the time
    }

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
}

