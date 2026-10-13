package com.kelompok.absensi.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/** Nilai role yang dipakai di seluruh aplikasi. */
object Role {
    const val ADMIN = "admin"
    const val MAHASISWA = "mahasiswa"
}

/**
 * Data pengguna, disimpan di koleksi "users".
 * ID dokumen = UID dari Firebase Authentication.
 */
data class User(
    @DocumentId val uid: String = "",
    val nama: String = "",
    val email: String = "",
    val nim: String = "",
    val role: String = Role.MAHASISWA,
    val createdAt: Timestamp? = null
)