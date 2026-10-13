package com.kelompok.absensi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok.absensi.data.Attendance
import com.kelompok.absensi.ui.history.contohRiwayat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Satu pilihan filter bulan, misalnya kunci "2026-10" dan label "Oktober 2026". */
data class PilihanBulan(val kunci: String, val label: String)

/** Kondisi halaman riwayat. */
data class HistoryUiState(
    val sedangMemuat: Boolean = true,
    val semua: List<Attendance> = emptyList(),
    val kunciDipilih: String? = null, // null berarti "Semua"
    val pesanError: String? = null
) {
    val daftarBulan: List<PilihanBulan>
        get() = semua.mapNotNull { kunciBulan(it) }
            .distinct()
            .sortedDescending()
            .map { PilihanBulan(it, labelBulan(it)) }

    val tampil: List<Attendance>
        get() = if (kunciDipilih == null) semua
        else semua.filter { kunciBulan(it) == kunciDipilih }
}

/** Mengambil bulan dari waktu masuk. Kalau kosong, dari 7 huruf pertama tanggal (yyyy-MM). */
private fun kunciBulan(a: Attendance): String? {
    val masuk = a.checkInTime?.toDate()
    if (masuk != null) return SimpleDateFormat("yyyy-MM", Locale.US).format(masuk)
    return a.tanggal.take(7).ifBlank { null }
}

private fun labelBulan(kunci: String): String {
    val tanggal = runCatching { SimpleDateFormat("yyyy-MM", Locale.US).parse(kunci) }.getOrNull()
        ?: return kunci
    return SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(tanggal)
}

class HistoryViewModel : ViewModel() {

    // Ubah ke true untuk mencoba tampilan error, lalu kembalikan ke false.
    private val paksaError = false

    // Ubah ke true untuk mencoba tampilan kosong, lalu kembalikan ke false.
    private val paksaKosong = false

    var uiState by mutableStateOf(HistoryUiState())
        private set

    init {
        muatRiwayat()
    }

    fun muatRiwayat() {
        viewModelScope.launch {
            uiState = uiState.copy(sedangMemuat = true, pesanError = null)
            delay(600) // pura-pura menunggu jaringan, nanti diganti Firestore
            if (paksaError) {
                uiState = uiState.copy(
                    sedangMemuat = false,
                    pesanError = "Gagal memuat riwayat. Periksa koneksi Anda."
                )
            } else {
                val data = if (paksaKosong) emptyList() else contohRiwayat()
                uiState = uiState.copy(sedangMemuat = false, semua = data)
            }
        }
    }

    fun pilihBulan(kunci: String?) {
        uiState = uiState.copy(kunciDipilih = kunci)
    }
}