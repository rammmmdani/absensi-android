package com.kelompok.absensi.ui.history

import com.google.firebase.Timestamp
import com.kelompok.absensi.data.Attendance
import com.kelompok.absensi.data.StatusAbsensi

private const val JAM = 3_600L
private const val HARI = 86_400L

// Senin, 5 Oktober 2026 pukul 00:00 WIB, dalam hitungan detik sejak 1970
private const val SENIN_5_OKT_00_WIB = 1_791_133_200L

private fun waktu(hariKe: Int, jam: Int, menit: Int): Timestamp =
    Timestamp(SENIN_5_OKT_00_WIB + hariKe * HARI + jam * JAM + menit * 60L, 0)

/** Data contoh sementara, dari yang terbaru. Nanti diganti data dari Firestore. */
fun contohRiwayat(): List<Attendance> = listOf(
    Attendance(
        id = "contoh_5", userId = "contoh", nama = "Contoh", tanggal = "2026-10-09",
        checkInTime = waktu(4, 8, 5), checkOutTime = waktu(4, 16, 10),
        status = StatusAbsensi.TERLAMBAT
    ),
    Attendance(
        id = "contoh_4", userId = "contoh", nama = "Contoh", tanggal = "2026-10-08",
        checkInTime = waktu(3, 7, 55), checkOutTime = null,
        status = StatusAbsensi.HADIR
    ),
    Attendance(
        id = "contoh_3", userId = "contoh", nama = "Contoh", tanggal = "2026-10-07",
        checkInTime = waktu(2, 7, 50), checkOutTime = waktu(2, 16, 15),
        status = StatusAbsensi.HADIR
    ),
    Attendance(
        id = "contoh_2", userId = "contoh", nama = "Contoh", tanggal = "2026-10-06",
        checkInTime = waktu(1, 8, 20), checkOutTime = waktu(1, 16, 0),
        status = StatusAbsensi.TERLAMBAT
    ),
    Attendance(
        id = "contoh_1", userId = "contoh", nama = "Contoh", tanggal = "2026-10-05",
        checkInTime = waktu(0, 7, 45), checkOutTime = waktu(0, 16, 30),
        status = StatusAbsensi.HADIR
    )
)