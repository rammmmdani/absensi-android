package com.kelompok.absensi.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok.absensi.data.Attendance
import com.kelompok.absensi.data.StatusAbsensi
import com.kelompok.absensi.utils.DateTimeUtils
import com.kelompok.absensi.viewmodel.HistoryViewModel

/** Halaman riwayat absensi. Dikerjakan di branch feature/history. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel()
) {
    val state = viewModel.uiState

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

        when {
            state.sedangMemuat -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.pesanError != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.pesanError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.muatRiwayat() },
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text("Coba lagi")
                    }
                }
            }

            state.semua.isEmpty() -> {
                PesanKosong("Belum ada riwayat absensi.")
            }

            else -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.kunciDipilih == null,
                            onClick = { viewModel.pilihBulan(null) },
                            label = { Text("Semua") }
                        )
                    }
                    items(state.daftarBulan, key = { it.kunci }) { bulan ->
                        FilterChip(
                            selected = state.kunciDipilih == bulan.kunci,
                            onClick = { viewModel.pilihBulan(bulan.kunci) },
                            label = { Text(bulan.label) }
                        )
                    }
                }

                val tampil = state.tampil
                if (tampil.isEmpty()) {
                    PesanKosong("Tidak ada absensi pada bulan ini.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(tampil, key = { it.id }) { absensi ->
                            KartuRiwayat(absensi)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PesanKosong(pesan: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = pesan,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
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