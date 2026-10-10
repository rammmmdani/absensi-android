package com.kelompok.absensi.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kelompok.absensi.ui.common.PlaceholderScreen

/**
 * Halaman profil. Dikerjakan di branch feature/profile.
 * onLogout dipanggil saat tombol logout ditekan.
 */
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlaceholderScreen(
        judul = "Profil",
        keterangan = "Dikerjakan di branch feature/profile",
        modifier = modifier
    )
}