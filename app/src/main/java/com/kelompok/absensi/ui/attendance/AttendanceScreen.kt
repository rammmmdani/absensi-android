package com.kelompok.absensi.ui.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Halaman check-in dan check-out. */
@Composable
fun AttendanceScreen(
    modifier: Modifier = Modifier,
    viewModel: AttendanceViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val tanggal = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")).format(Date())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Absensi", style = MaterialTheme.typography.headlineMedium)
        Text(tanggal, style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = when {
                        state.sudahCheckOut -> "Absensi hari ini selesai"
                        state.sudahCheckIn -> "Kamu sudah check-in"
                        else -> "Belum check-in"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Check-in")
                    Text(state.jamCheckIn ?: "-")
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Check-out")
                    Text(state.jamCheckOut ?: "-")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = viewModel::checkIn,
            enabled = !state.sudahCheckIn,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Check-in") }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = viewModel::checkOut,
            enabled = state.sudahCheckIn && !state.sudahCheckOut,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Check-out") }
    }
}