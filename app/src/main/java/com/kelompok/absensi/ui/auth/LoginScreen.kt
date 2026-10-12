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
 * Halaman login.
 * onLoginBerhasil dipanggil dengan data pengguna kalau login berhasil.
 * onKeDaftar dipanggil saat pengguna menekan tautan "Daftar".
 */
@Composable
fun LoginScreen(
    onLoginBerhasil: (User) -> Unit,
    onKeDaftar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val state = viewModel.uiState
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.pengguna) {
        state.pengguna?.let(onLoginBerhasil)
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
            text = "Absensi",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Masuk ke akun Anda",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
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
            label = "Password",
            imeAction = ImeAction.Done
        )

        PesanError(pesan = state.pesanError)

        TombolUtama(
            teks = "Masuk",
            sedangMemuat = state.sedangMemuat,
            onClick = { viewModel.masuk(email, password) },
            modifier = Modifier.padding(top = 12.dp)
        )

        TextButton(onClick = onKeDaftar) {
            Text("Belum punya akun? Daftar")
        }
    }
}