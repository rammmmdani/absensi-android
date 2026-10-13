package com.kelompok.absensi.utils

import android.util.Patterns

/**
 * Memeriksa isian form. Setiap fungsi mengembalikan pesan error,
 * atau null kalau isiannya sudah benar.
 */
object InputValidator {

    fun email(email: String): String? = when {
        email.isBlank() -> "Email wajib diisi."
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email tidak valid."
        else -> null
    }

    fun password(password: String): String? = when {
        password.isEmpty() -> "Password wajib diisi."
        password.length < 6 -> "Password minimal 6 karakter."
        else -> null
    }

    fun nama(nama: String): String? =
        if (nama.isBlank()) "Nama wajib diisi." else null

    fun nim(nim: String): String? = when {
        nim.isBlank() -> "NIM wajib diisi."
        !nim.all { it.isDigit() } -> "NIM hanya boleh berisi angka."
        else -> null
    }

    fun konfirmasiPassword(password: String, konfirmasi: String): String? =
        if (password != konfirmasi) "Konfirmasi password tidak sama." else null
}