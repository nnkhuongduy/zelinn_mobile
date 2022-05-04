package com.example.zelinn.classes

import com.cesarferreira.tempo.Tempo
import com.cesarferreira.tempo.toString
import java.util.*

data class CardDateTime (val year: Int?, val month: Int?, val date: Int?, val hour: Int?, val minute: Int?) {
    fun isDateValid(): Boolean {
        return year != null && month != null && date != null
    }

    fun isTimeValid(): Boolean {
        return hour != null && minute != null
    }

    fun toISO(): String? {
        if (isDateValid() || isTimeValid()) {
            return Tempo.with(year?: 0, month?: 0, date ?: 0, hour?: 0, minute?: 0).toString("yyyy-MM-dd'T'HH:mm'Z'")
        }
        return null
    }
}