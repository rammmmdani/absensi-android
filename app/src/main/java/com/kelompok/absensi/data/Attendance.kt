package com.kelompok.absensi.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/** Status yang disimpan di Firestore. */
object StatusAbsensi {
    const val HADIR = "hadir"
    const val TERLAMBAT = "terlambat"
}

/**
 * Data absensi, disimpan di koleksi "attendance".
 * ID dokumen = "{uid}_{tanggal}", contoh: abc123_2026-10-10
 */
data class Attendance(
    @DocumentId val id: String = "",
    val userId: String = "",
    val nama: String = "",
    val tanggal: String = "",          // format yyyy-MM-dd
    val checkInTime: Timestamp? = null,
    val checkOutTime: Timestamp? = null,
    val status: String = StatusAbsensi.HADIR
)