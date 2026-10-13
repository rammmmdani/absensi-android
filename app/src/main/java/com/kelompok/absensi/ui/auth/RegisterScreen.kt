package com.kelompok.absensi.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok.absensi.data.User
import com.kelompok.absensi.viewmodel.AuthViewModel

/**
 * Halaman registrasi. Akun baru selalu berperan sebagai mahasiswa.
 * onDaftarBerhasil dipanggil dengan data pengguna kalau registrasi berhasil.
 * onKeMasuk dipanggil saat pengguna menekan tautan "Masuk".
 */
@Composable
fun RegisterScreen(
    onDaftarBerhasil: (User) -> Unit,
    onKeMasuk: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val state = viewModel.uiState
    var nama by rememberSaveable { mutableStateOf("") }
    var nim by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var konfirmasi by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.pengguna) {
        state.pengguna?.let(onDaftarBerhasil)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Buat Akun",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Daftar sebagai mahasiswa",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        KolomTeks(
            value = nama,
            onValueChange = { nama = it; viewModel.hapusError() },
            label = "Nama lengkap"
        )
        KolomTeks(
            value = nim,
            onValueChange = { nim = it; viewModel.hapusError() },
            label = "NIM",
            keyboardType = KeyboardType.Number
        )
        KolomTeks(
            value = email,
            onValueChange = { email = it; viewModel.hapusError() },
            label = "Email",
            keyboardType = KeyboardType.Email
        )
        KolomPassword(
            value = password,
            onValueChange = { password = it; viewModel.hapusError() },
            label = "Password"
        )
        KolomPassword(
            value = konfirmasi,
            onValueChange = { konfirmasi = it; viewModel.hapusError() },
            label = "Konfirmasi password",
            imeAction = ImeAction.Done
        )

        PesanError(pesan = state.pesanError)

        TombolUtama(
            teks = "Daftar",
            sedangMemuat = state.sedangMemuat,
            onClick = { viewModel.daftar(nama, email, nim, password, konfirmasi) },
            modifier = Modifier.padding(top = 12.dp)
        )

        TextButton(onClick = onKeMasuk) {
            Text("Sudah punya akun? Masuk")
        }
    }
}