package com.kelompok.absensi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok.absensi.auth.AuthRepository
import com.kelompok.absensi.auth.pesanErrorAuth
import com.kelompok.absensi.data.User
import com.kelompok.absensi.utils.InputValidator
import kotlinx.coroutines.launch

/** Kondisi halaman login dan registrasi. */
data class AuthUiState(
    val sedangMemuat: Boolean = false,
    val pesanError: String? = null,
    val pengguna: User? = null // terisi kalau login atau daftar berhasil
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun masuk(email: String, password: String) {
        if (uiState.sedangMemuat) return

        val emailBersih = email.trim()
        val error = InputValidator.email(emailBersih)
            ?: InputValidator.password(password)
        if (error != null) {
            uiState = AuthUiState(pesanError = error)
            return
        }

        viewModelScope.launch {
            uiState = AuthUiState(sedangMemuat = true)
            repository.masuk(emailBersih, password)
                .onSuccess { uiState = AuthUiState(pengguna = it) }
                .onFailure { uiState = AuthUiState(pesanError = it.pesanErrorAuth()) }
        }
    }

    fun daftar(
        nama: String,
        email: String,
        nim: String,
        password: String,
        konfirmasiPassword: String
    ) {
        if (uiState.sedangMemuat) return

        val namaBersih = nama.trim()
        val emailBersih = email.trim()
        val nimBersih = nim.trim()
        val error = InputValidator.nama(namaBersih)
            ?: InputValidator.nim(nimBersih)
            ?: InputValidator.email(emailBersih)
            ?: InputValidator.password(password)
            ?: InputValidator.konfirmasiPassword(password, konfirmasiPassword)
        if (error != null) {
            uiState = AuthUiState(pesanError = error)
            return
        }

        viewModelScope.launch {
            uiState = AuthUiState(sedangMemuat = true)
            repository.daftar(namaBersih, emailBersih, nimBersih, password)
                .onSuccess { uiState = AuthUiState(pengguna = it) }
                .onFailure { uiState = AuthUiState(pesanError = it.pesanErrorAuth()) }
        }
    }

    /** Menghapus pesan error, dipanggil saat pengguna mulai mengetik lagi. */
    fun hapusError() {
        uiState = uiState.copy(pesanError = null)
    }
}