package com.kelompok.absensi.utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Semua urusan tanggal dan jam ada di sini, supaya format seragam di seluruh aplikasi.
 * Zona waktu yang dipakai: Asia/Jakarta (WIB).
 */
object DateTimeUtils {

    private val ZONA_WIB: TimeZone = TimeZone.getTimeZone("Asia/Jakarta")
    private val LOCALE_ID: Locale = Locale.forLanguageTag("id-ID")

    /** Batas jam masuk. Check-in setelah jam ini dianggap terlambat. */
    const val BATAS_JAM_MASUK = 8
    const val BATAS_MENIT_MASUK = 0

    private fun formatter(pola: String): SimpleDateFormat =
        SimpleDateFormat(pola, LOCALE_ID).apply { timeZone = ZONA_WIB }

    /** Contoh: 2026-10-10. Dipakai sebagai field "tanggal" dan bagian dari ID dokumen absensi. */
    fun tanggalKey(waktu: Date = Date()): String =
        formatter("yyyy-MM-dd").format(waktu)

    /** Contoh: 2026-10. Dipakai untuk filter riwayat per bulan. */
    fun bulanKey(waktu: Date = Date()): String =
        formatter("yyyy-MM").format(waktu)

    /** Contoh: Sabtu, 10 Oktober 2026. Untuk tampilan di layar. */
    fun tanggalLengkap(waktu: Date = Date()): String =
        formatter("EEEE, d MMMM yyyy").format(waktu)

    /** Contoh: 07:45. Kalau waktu kosong, ditampilkan "-". */
    fun jam(waktu: Date?): String =
        if (waktu == null) "-" else formatter("HH:mm").format(waktu)

    /** Contoh: 07:45:30. Untuk jam yang berjalan di dashboard. */
    fun jamDetik(waktu: Date = Date()): String =
        formatter("HH:mm:ss").format(waktu)

    /** True kalau waktu check-in lewat dari batas jam masuk (zona WIB). */
    fun isTerlambat(waktu: Date): Boolean {
        val kalender = Calendar.getInstance(ZONA_WIB).apply { time = waktu }
        val menitMasuk = kalender.get(Calendar.HOUR_OF_DAY) * 60 + kalender.get(Calendar.MINUTE)
        val menitBatas = BATAS_JAM_MASUK * 60 + BATAS_MENIT_MASUK
        return menitMasuk > menitBatas
    }
}