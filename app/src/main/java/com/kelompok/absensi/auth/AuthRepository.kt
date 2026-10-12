package com.kelompok.absensi.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kelompok.absensi.data.Role
import com.kelompok.absensi.data.User
import kotlinx.coroutines.tasks.await

/**
 * Mengurus akun: daftar, masuk, keluar, dan mengambil profil pengguna.
 * Hanya file ini yang boleh memanggil FirebaseAuth untuk urusan akun.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val users = firestore.collection("users")

    /** True kalau ada pengguna yang sedang login. */
    val sudahMasuk: Boolean
        get() = auth.currentUser != null

    /** UID pengguna yang sedang login, atau null. */
    val uidSekarang: String?
        get() = auth.currentUser?.uid

    /**
     * Membuat akun baru. Role selalu "mahasiswa".
     * Kalau penyimpanan profil gagal, akun yang baru dibuat dihapus lagi
     * supaya tidak ada akun tanpa profil.
     */
    suspend fun daftar(
        nama: String,
        email: String,
        nim: String,
        password: String
    ): Result<User> = runCatching {
        val hasil = auth.createUserWithEmailAndPassword(email, password).await()
        val akunBaru = hasil.user ?: error("Akun gagal dibuat")

        try {
            users.document(akunBaru.uid).set(
                mapOf(
                    "nama" to nama,
                    "email" to email,
                    "nim" to nim,
                    "role" to Role.MAHASISWA,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            akunBaru.delete().await()
            throw e
        }

        ambilProfil(akunBaru.uid)
    }

    /** Login dengan email dan password, lalu mengambil profilnya. */
    suspend fun masuk(email: String, password: String): Result<User> = runCatching {
        val hasil = auth.signInWithEmailAndPassword(email, password).await()
        val uid = hasil.user?.uid ?: error("Login gagal")

        try {
            ambilProfil(uid)
        } catch (e: Exception) {
            auth.signOut()
            throw e
        }
    }

    /** Mengambil profil dari koleksi "users". */
    suspend fun ambilProfil(uid: String): User {
        val dokumen = users.document(uid).get().await()
        return dokumen.toObject(User::class.java)
            ?: error("Data pengguna tidak ditemukan")
    }

    /** Logout. */
    fun keluar() {
        auth.signOut()
    }
}

/** Mengubah error dari Firebase menjadi pesan bahasa Indonesia yang mudah dipahami. */
fun Throwable.pesanErrorAuth(): String = when (this) {
    // WeakPassword harus di atas InvalidCredentials karena merupakan turunannya.
    is FirebaseAuthWeakPasswordException ->
        "Password terlalu lemah. Gunakan minimal 6 karakter."
    is FirebaseAuthUserCollisionException ->
        "Email sudah terdaftar. Silakan login."
    is FirebaseAuthInvalidUserException,
    is FirebaseAuthInvalidCredentialsException ->
        "Email atau password salah."
    is FirebaseNetworkException ->
        "Tidak ada koneksi internet. Periksa jaringan Anda."
    else ->
        "Terjadi kesalahan: ${message ?: "tidak diketahui"}"
}