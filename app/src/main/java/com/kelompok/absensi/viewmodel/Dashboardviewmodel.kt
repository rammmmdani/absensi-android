package com.kelompok.absensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kelompok.absensi.data.Attendance
import com.kelompok.absensi.data.StatusAbsensi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DashboardUiState(
    val isLoading: Boolean = true,
    val nama: String = "",
    val hariIni: Attendance? = null,
    val jumlahHadir: Int = 0,
    val jumlahTerlambat: Int = 0,
    val terakhir: List<Attendance> = emptyList(),
    val error: String? = null
) {
    val sudahMasuk get() = hariIni != null
    val sudahKeluar get() = hariIni?.checkOutTime != null
}

/**
 * Data diambil dari koleksi "attendance" (field userId, tanggal, checkInTime,
 * checkOutTime, status). Nama: FirebaseAuth.displayName, lalu field nama di absen,
 * lalu users/{uid}.nama.
 */
class DashboardViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init { muat() }

    fun muat() {
        val uid = auth.currentUser?.uid ?: run {
            _state.update { it.copy(isLoading = false, error = "Belum login") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val locale = Locale.getDefault()
                val hari = SimpleDateFormat("yyyy-MM-dd", locale).format(Date())
                val bulan = hari.substring(0, 7) // yyyy-MM

                // Nama
                // Semua absen user, terbaru dulu
                val semua = db.collection("attendance")
                    .whereEqualTo("userId", uid)
                    .orderBy("tanggal", Query.Direction.DESCENDING)
                    .get().await()
                    .toObjects(Attendance::class.java)

                val nama = auth.currentUser?.displayName?.takeIf { it.isNotBlank() }
                    ?: semua.firstOrNull { it.nama.isNotBlank() }?.nama
                    ?: db.collection("users").document(uid).get().await()
                        .getString("nama").orEmpty()

                val bulanIni = semua.filter { it.tanggal.startsWith(bulan) }

                _state.update {
                    it.copy(
                        isLoading = false,
                        nama = nama,
                        hariIni = semua.firstOrNull { a -> a.tanggal == hari },
                        jumlahHadir = bulanIni.count { a -> a.status == StatusAbsensi.HADIR },
                        jumlahTerlambat = bulanIni.count { a -> a.status == StatusAbsensi.TERLAMBAT },
                        terakhir = semua.take(3)
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Gagal memuat data") }
            }
        }
    }
}