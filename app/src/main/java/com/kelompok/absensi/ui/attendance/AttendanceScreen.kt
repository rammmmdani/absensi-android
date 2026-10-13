package com.kelompok.absensi.ui.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

/** Halaman check-in dan check-out. */
@Composable
fun AttendanceScreen(
    modifier: Modifier = Modifier,
    viewModel: AttendanceViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val locale = Locale("id", "ID")

    // Jam berjalan, diperbarui tiap detik
    var sekarang by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            sekarang = Date()
            delay(1000)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text("Absensi", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        // Kartu jam besar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = SimpleDateFormat("HH:mm:ss", locale).format(sekarang),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = SimpleDateFormat("EEEE, d MMMM yyyy", locale).format(sekarang),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Dua kartu: Check-in dan Check-out berdampingan
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            JamCard("Check-in", state.jamCheckIn, Modifier.weight(1f))
            JamCard("Check-out", state.jamCheckOut, Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        // Status hari ini
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = if (state.sudahCheckIn) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.padding(4.dp))
            Text(
                text = when {
                    state.sudahCheckOut -> "Absensi hari ini selesai"
                    state.sudahCheckIn -> "Kamu sudah check-in, jangan lupa check-out"
                    else -> "Kamu belum check-in hari ini"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.weight(1f))

        // Satu tombol utama yang berubah sesuai tahap
        Button(
            onClick = {
                if (!state.sudahCheckIn) viewModel.checkIn() else viewModel.checkOut()
            },
            enabled = !state.sudahCheckOut,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = when {
                    state.sudahCheckOut -> "Selesai"
                    state.sudahCheckIn -> "Check-out"
                    else -> "Check-in"
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun JamCard(judul: String, jam: String?, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(judul, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                text = jam ?: "--:--",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}