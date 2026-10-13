package com.kelompok.absensi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok.absensi.auth.AuthRepository
import com.kelompok.absensi.data.User
import com.kelompok.absensi.repository.UserRepository
import com.kelompok.absensi.utils.InputValidator
import kotlinx.coroutines.launch

/** Kondisi halaman profil. */
data class ProfileUiState(
    val sedangMemuat: Boolean = true,
    val sedangMenyimpan: Boolean = false,
    val sedangMengubah: Boolean = false,
    val pengguna: User? = null,
    val pesanError: String? = null,
    val pesanSukses: String? = null
)

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        muatProfil()
    }

    /** Mengambil profil pengguna yang sedang login dari Firestore. */
    fun muatProfil() {
        val uid = authRepository.uidSekarang
        if (uid == null) {
            uiState = ProfileUiState(sedangMemuat = false, pesanError = "Anda belum login.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(sedangMemuat = true, pesanError = null)
            runCatching { authRepository.ambilProfil(uid) }
                .onSuccess { uiState = uiState.copy(sedangMemuat = false, pengguna = it) }
                .onFailure {
                    uiState = uiState.copy(
                        sedangMemuat = false,
                        pesanError = "Gagal memuat profil. Periksa koneksi Anda."
                    )
                }
        }
    }

    fun mulaiUbah() {
        uiState = uiState.copy(sedangMengubah = true, pesanError = null, pesanSukses = null)
    }

    fun batalUbah() {
        uiState = uiState.copy(sedangMengubah = false, pesanError = null)
    }

    /** Menghapus pesan saat pengguna mulai mengetik lagi. */
    fun hapusPesan() {
        uiState = uiState.copy(pesanError = null, pesanSukses = null)
    }

    /** Memeriksa isian, lalu menyimpan nama dan NIM baru. */
    fun simpan(nama: String, nim: String) {
        val pengguna = uiState.pengguna ?: return
        if (uiState.sedangMenyimpan) return

        val namaBersih = nama.trim()
        val nimBersih = nim.trim()
        val error = InputValidator.nama(namaBersih) ?: InputValidator.nim(nimBersih)
        if (error != null) {
            uiState = uiState.copy(pesanError = error)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(sedangMenyimpan = true, pesanError = null)
            userRepository.ubahProfil(pengguna.uid, namaBersih, nimBersih)
                .onSuccess {
                    uiState = uiState.copy(
                        sedangMenyimpan = false,
                        sedangMengubah = false,
                        pengguna = pengguna.copy(nama = namaBersih, nim = nimBersih),
                        pesanSukses = "Profil berhasil diperbarui."
                    )
                }
                .onFailure {
                    uiState = uiState.copy(
                        sedangMenyimpan = false,
                        pesanError = "Gagal menyimpan profil. Coba lagi."
                    )
                }
        }
    }
}