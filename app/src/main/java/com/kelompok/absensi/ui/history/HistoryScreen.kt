package com.kelompok.absensi.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kelompok.absensi.data.Attendance
import com.kelompok.absensi.data.StatusAbsensi
import com.kelompok.absensi.utils.DateTimeUtils

/** Halaman riwayat absensi. Dikerjakan di branch feature/history. */
@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {
    val daftar = contohRiwayat() // sementara: data contoh

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Riwayat Absensi",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(daftar, key = { it.id }) { absensi ->
                KartuRiwayat(absensi)
            }
        }
    }
}

@Composable
private fun KartuRiwayat(absensi: Attendance) {
    val masuk = absensi.checkInTime?.toDate()
    val keluar = absensi.checkOutTime?.toDate()
    val judul = if (masuk != null) DateTimeUtils.tanggalLengkap(masuk) else absensi.tanggal
    val terlambat = absensi.status == StatusAbsensi.TERLAMBAT

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = judul, style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Masuk: ${DateTimeUtils.jam(masuk)}")
                Text(text = "Keluar: ${DateTimeUtils.jam(keluar)}")
            }
            Text(
                text = if (terlambat) "Terlambat" else "Hadir",
                color = if (terlambat) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}