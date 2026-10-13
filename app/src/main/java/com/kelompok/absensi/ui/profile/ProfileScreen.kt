package com.kelompok.absensi.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok.absensi.data.Role
import com.kelompok.absensi.data.User
import com.kelompok.absensi.ui.auth.KolomTeks
import com.kelompok.absensi.ui.auth.PesanError
import com.kelompok.absensi.ui.auth.TombolUtama
import com.kelompok.absensi.viewmodel.ProfileUiState
import com.kelompok.absensi.viewmodel.ProfileViewModel

/**
 * Halaman profil. Dikerjakan di branch feature/profile.
 * onLogout dipanggil saat tombol Keluar ditekan.
 */
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val state = viewModel.uiState
    val pengguna = state.pengguna

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Profil",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (state.sedangMemuat) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (pengguna == null) {
            PesanError(pesan = state.pesanError)
            Button(
                onClick = { viewModel.muatProfil() },
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Coba lagi")
            }
        } else {
            IsiProfil(
                state = state,
                pengguna = pengguna,
                viewModel = viewModel,
                onLogout = onLogout
            )
        }
    }
}

@Composable
private fun IsiProfil(
    state: ProfileUiState,
    pengguna: User,
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    // Isian dikembalikan ke data terbaru setiap kali masuk atau keluar dari mode ubah.
    var nama by rememberSaveable(pengguna.nama, state.sedangMengubah) {
        mutableStateOf(pengguna.nama)
    }
    var nim by rememberSaveable(pengguna.nim, state.sedangMengubah) {
        mutableStateOf(pengguna.nim)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = pengguna.nama.take(1).uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.sedangMengubah) {
                    KolomTeks(
                        value = nama,
                        onValueChange = { nama = it; viewModel.hapusPesan() },
                        label = "Nama lengkap"
                    )
                    KolomTeks(
                        value = nim,
                        onValueChange = { nim = it; viewModel.hapusPesan() },
                        label = "NIM",
                        keyboardType = KeyboardType.Number
                    )
                    BarisInfo(label = "Email (tidak dapat diubah)", nilai = pengguna.email)
                } else {
                    BarisInfo(label = "Nama", nilai = pengguna.nama)
                    BarisInfo(label = "Email", nilai = pengguna.email)
                    BarisInfo(label = "NIM", nilai = pengguna.nim)
                    BarisInfo(
                        label = "Peran",
                        nilai = if (pengguna.role == Role.ADMIN) "Admin" else "Mahasiswa"
                    )
                }
            }
        }

        PesanError(pesan = state.pesanError)
        state.pesanSukses?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (state.sedangMengubah) {
            TombolUtama(
                teks = "Simpan",
                sedangMemuat = state.sedangMenyimpan,
                onClick = { viewModel.simpan(nama, nim) }
            )
            TextButton(
                onClick = { viewModel.batalUbah() },
                enabled = !state.sedangMenyimpan
            ) {
                Text("Batal")
            }
        } else {
            Button(
                onClick = { viewModel.mulaiUbah() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ubah Profil")
            }
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Keluar", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun BarisInfo(label: String, nilai: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = nilai.ifBlank { "-" },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}