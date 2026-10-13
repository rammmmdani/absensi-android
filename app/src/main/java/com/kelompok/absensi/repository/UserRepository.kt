package com.kelompok.absensi.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/** Mengurus data profil pengguna di koleksi "users". */
class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val users = firestore.collection("users")

    /**
     * Mengubah nama dan NIM pengguna.
     * Hanya dua field ini yang diizinkan oleh Security Rules,
     * jadi email dan role tidak boleh disentuh di sini.
     */
    suspend fun ubahProfil(uid: String, nama: String, nim: String): Result<Unit> = runCatching {
        users.document(uid).update(
            mapOf(
                "nama" to nama,
                "nim" to nim
            )
        ).await()
        Unit
    }
}