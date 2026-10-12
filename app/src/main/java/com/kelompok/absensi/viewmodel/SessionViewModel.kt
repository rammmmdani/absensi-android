package com.kelompok.absensi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok.absensi.auth.AuthRepository
import com.kelompok.absensi.data.User
import kotlinx.coroutines.launch

/** Siapa yang sedang login. sedangMemuat = true selama aplikasi masih memeriksa. */
data class SessionState(
    val sedangMemuat: Boolean = true,
    val pengguna: User? = null
)

/**
 * Menyimpan data pengguna yang sedang login selama aplikasi berjalan.
 * Saat aplikasi dibuka, ia memeriksa apakah masih ada sesi login sebelumnya.
 */
class SessionViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    var state by mutableStateOf(SessionState())
        private set

    init {
        val uid = repository.uidSekarang
        if (uid == null) {
            state = SessionState(sedangMemuat = false)
        } else {
            viewModelScope.launch {
                runCatching { repository.ambilProfil(uid) }
                    .onSuccess { state = SessionState(sedangMemuat = false, pengguna = it) }
                    .onFailure {
                        // Profil tidak bisa diambil (misalnya tanpa internet): minta login ulang.
                        repository.keluar()
                        state = SessionState(sedangMemuat = false)
                    }
            }
        }
    }

    /** Dipanggil setelah login atau registrasi berhasil. */
    fun setPengguna(pengguna: User) {
        state = SessionState(sedangMemuat = false, pengguna = pengguna)
    }

    /** Logout. */
    fun keluar() {
        repository.keluar()
        state = SessionState(sedangMemuat = false)
    }
}