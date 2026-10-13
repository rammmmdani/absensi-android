package com.kelompok.absensi.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.kelompok.absensi.data.Attendance
import com.kelompok.absensi.data.StatusAbsensi
import com.kelompok.absensi.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Dashboard mahasiswa. */
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
    onAbsenClick: () -> Unit = {},
    onLihatSemuaClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tanggal = remember {
        SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")).format(Date())
    }

    if (state.isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = if (state.nama.isBlank()) "Halo" else "Halo, ${state.nama}",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(tanggal, style = MaterialTheme.typography.bodyMedium)
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = viewModel::muat) { Text("Coba lagi") }
        }

        // Status hari ini
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Status hari ini", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = when {
                        state.sudahKeluar -> "Absensi selesai"
                        state.sudahMasuk -> "Sudah masuk ${state.hariIni?.checkInTime.keJam()}"
                        else -> "Belum absen"
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
                if (state.sudahMasuk) {
                    Text(
                        "Masuk: ${state.hariIni?.checkInTime.keJam()}   Keluar: ${state.hariIni?.checkOutTime.keJam()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (!state.sudahKeluar) {
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onAbsenClick, modifier = Modifier.fillMaxWidth()) {
                        Text(if (state.sudahMasuk) "Absen keluar" else "Absen sekarang")
                    }
                }
            }
        }

        // Ringkasan bulan ini
        val bulan = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(Date())
        Text("Ringkasan $bulan", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RingkasanCard("Hadir", state.jumlahHadir, Modifier.weight(1f))
            RingkasanCard("Terlambat", state.jumlahTerlambat, Modifier.weight(1f))
        }

        // Riwayat singkat
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Terakhir", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onLihatSemuaClick) { Text("Lihat semua") }
        }
        if (state.terakhir.isEmpty()) {
            Text("Belum ada riwayat absen.", style = MaterialTheme.typography.bodyMedium)
        } else {
            state.terakhir.forEach { RiwayatItem(it) }
        }
    }
}

@Composable
private fun RingkasanCard(label: String, jumlah: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(jumlah.toString(), style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
private fun RiwayatItem(a: Attendance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(a.tanggal.keTanggalIndonesia(), style = MaterialTheme.typography.titleSmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Masuk: ${a.checkInTime.keJam()}")
                Text("Keluar: ${a.checkOutTime.keJam()}")
            }
            val terlambat = a.status == StatusAbsensi.TERLAMBAT
            Text(
                text = if (terlambat) "Terlambat" else "Hadir",
                style = MaterialTheme.typography.labelMedium,
                color = if (terlambat) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun Timestamp?.keJam(): String =
    if (this == null) "-" else SimpleDateFormat("HH:mm", Locale.getDefault()).format(toDate())

private fun String.keTanggalIndonesia(): String = try {
    val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(this)!!
    SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")).format(d)
} catch (e: Exception) { this }