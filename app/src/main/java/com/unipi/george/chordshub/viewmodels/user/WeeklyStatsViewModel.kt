package com.unipi.george.chordshub.viewmodels.user

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.State


data class DayStat(val day: String, val minutes: String)

class WeeklyStatsViewModel : ViewModel() {

    private val _weeklyStats = mutableStateOf<List<DayStat>>(emptyList())
    val weeklyStats: State<List<DayStat>> = _weeklyStats

    private val db = FirebaseFirestore.getInstance()

    fun fetchWeeklyStats(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) return@addOnSuccessListener

                val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
                val stats = mutableListOf<DayStat>()

                val nestedMap = document.get("totalTimeSpent") as? Map<*, *>

                for (day in days) {
                    val value = when {
                        nestedMap?.containsKey(day) == true -> nestedMap[day]
                        document.contains("totalTimeSpent.$day") -> document.get("totalTimeSpent.$day")
                        else -> "-"
                    }
                    val text = if (value == "-" || value == null) "-" else "$value min"
                    stats.add(DayStat(day, text))
                }

                _weeklyStats.value = stats
            }
    }
}
