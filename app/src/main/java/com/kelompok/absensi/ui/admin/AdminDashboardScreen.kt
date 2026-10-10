package com.kelompok.absensi.ui.admin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kelompok.absensi.ui.common.PlaceholderScreen

/**
 * Dashboard admin. Dikerjakan di branch feature/admin-dashboard.
 * onLogout dipanggil saat admin keluar dari aplikasi.
 */
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlaceholderScreen(
        judul = "Dashboard Admin",
        keterangan = "Dikerjakan di branch feature/admin-dashboard",
        modifier = modifier
    )
}