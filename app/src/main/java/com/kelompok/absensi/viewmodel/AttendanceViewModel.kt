package com.kelompok.absensi.ui.attendance

import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AttendanceUiState(
    val jamCheckIn: String? = null,
    val jamCheckOut: String? = null
) {
    val sudahCheckIn get() = jamCheckIn != null
    val sudahCheckOut get() = jamCheckOut != null
}

class AttendanceViewModel : ViewModel() {

    private val _state = MutableStateFlow(AttendanceUiState())
    val state: StateFlow<AttendanceUiState> = _state.asStateFlow()

    fun checkIn() {
        if (_state.value.sudahCheckIn) return
        _state.update { it.copy(jamCheckIn = jamSekarang()) }
    }

    fun checkOut() {
        val s = _state.value
        if (!s.sudahCheckIn || s.sudahCheckOut) return
        _state.update { it.copy(jamCheckOut = jamSekarang()) }
    }

    private fun jamSekarang(): String =
        SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date())
}