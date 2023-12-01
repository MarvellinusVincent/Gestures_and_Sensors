// GestureViewModel.kt
package com.example.gesturesandsensors

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel class for managing gestures and sensors-related data and operations.
 */
class GestureViewModel : ViewModel() {
    /**
     * A mutable list of log entries to be observed by the UI.
     */
    val logEntries = mutableStateListOf<String>()

    /**
     * Adds a new log entry to the list.
     *
     * @param entry The log entry to be added.
     */
    fun addLogEntry(entry: String) {
        logEntries.add(entry)
    }
}
