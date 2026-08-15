package com.roboticswala.hub.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object BookingTimeUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val readableDateFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
    private val time12Format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val time24Format = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun getTodayDateString(): String {
        return dateFormat.format(Date())
    }

    fun formatReadableDate(dateStr: String): String {
        return try {
            val date = dateFormat.parse(dateStr) ?: return dateStr
            readableDateFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatTime12Hour(hour: Int, minute: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return time12Format.format(cal.time)
    }

    fun timeToMinutes(timeStr: String): Int {
        val trimmed = timeStr.trim()
        return try {
            if (trimmed.contains("AM", ignoreCase = true) || trimmed.contains("PM", ignoreCase = true)) {
                val date = time12Format.parse(trimmed) ?: return 0
                val cal = Calendar.getInstance().apply { time = date }
                cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
            } else {
                val parts = trimmed.split(":")
                val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
                val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                h * 60 + m
            }
        } catch (e: Exception) {
            0
        }
    }

    fun isDateInPast(dateStr: String): Boolean {
        return try {
            val target = dateFormat.parse(dateStr) ?: return false
            val todayStr = getTodayDateString()
            val today = dateFormat.parse(todayStr) ?: return false
            target.before(today)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Checks if two time intervals [startA, endA) and [startB, endB) overlap.
     * Example:
     * - [10:00, 12:00) (600..720) and [11:00, 13:00) (660..780) -> OVERLAP (max(600,660) < min(720,780) => 660 < 720)
     * - [10:00, 12:00) (600..720) and [12:00, 14:00) (720..840) -> NO OVERLAP (720 < 720 is FALSE)
     */
    fun isOverlapping(startA: Int, endA: Int, startB: Int, endB: Int): Boolean {
        return maxOf(startA, startB) < minOf(endA, endB)
    }
}
