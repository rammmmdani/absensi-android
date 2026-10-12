package com.kelompok.absensi.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelompok.absensi.data.Role
import com.kelompok.absensi.data.User
import com.kelompok.absensi.ui.admin.AdminDashboardScreen
import com.kelompok.absensi.ui.attendance.AttendanceScreen
import com.kelompok.absensi.ui.auth.LoginScreen
import com.kelompok.absensi.ui.auth.RegisterScreen
import com.kelompok.absensi.ui.dashboard.DashboardScreen
import com.kelompok.absensi.ui.history.HistoryScreen
import com.kelompok.absensi.ui.profile.ProfileScreen
import com.kelompok.absensi.viewmodel.SessionViewModel

/** Satu tombol di menu bawah. */
private data class MenuBawah(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val menuMahasiswa = listOf(
    MenuBawah(Routes.DASHBOARD, "Beranda", Icons.Default.Home),
    MenuBawah(Routes.ATTENDANCE, "Absensi", Icons.Default.CheckCircle),
    MenuBawah(Routes.HISTORY, "Riwayat", Icons.Default.DateRange),
    MenuBawah(Routes.PROFILE, "Profil", Icons.Default.Person)
)

private val menuAdmin = listOf(
    MenuBawah(Routes.ADMIN, "Admin", Icons.Default.Settings),
    MenuBawah(Routes.PROFILE, "Profil", Icons.Default.Person)
)

@Composable
fun AppNavHost(sessionViewModel: SessionViewModel = viewModel()) {
    val session = sessionViewModel.state

    if (session.sedangMemuat) {
        // Aplikasi sedang memeriksa apakah masih ada sesi login.
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        AplikasiUtama(sessionViewModel)
    }
}

@Composable
private fun AplikasiUtama(sessionViewModel: SessionViewModel) {
    val pengguna = sessionViewModel.state.pengguna
    val adalahAdmin = pengguna?.role == Role.ADMIN
    val berandaRute = if (adalahAdmin) Routes.ADMIN else Routes.DASHBOARD
    val menu = if (adalahAdmin) menuAdmin else menuMahasiswa

    // Halaman awal ditentukan sekali saja, saat halaman ini pertama tampil.
    val rutaAwal = remember { if (pengguna == null) Routes.LOGIN else berandaRute }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val ruteSekarang = backStackEntry?.destination?.route
    val tampilkanMenu = ruteSekarang != null &&
            ruteSekarang != Routes.LOGIN &&
            ruteSekarang != Routes.REGISTER

    fun masukKeBeranda(user: User) {
        sessionViewModel.setPengguna(user)
        val tujuan = if (user.role == Role.ADMIN) Routes.ADMIN else Routes.DASHBOARD
        navController.navigate(tujuan) {
            popUpTo(Routes.LOGIN) { inclusive = true }
        }
    }

    fun keluar() {
        sessionViewModel.keluar()
        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    Scaffold(
        bottomBar = {
            if (tampilkanMenu) {
                NavigationBar {
                    menu.forEach { item ->
                        NavigationBarItem(
                            selected = ruteSekarang == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(berandaRute) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = rutaAwal,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginBerhasil = { masukKeBeranda(it) },
                    onKeDaftar = { navController.navigate(Routes.REGISTER) }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onDaftarBerhasil = { masukKeBeranda(it) },
                    onKeMasuk = { navController.popBackStack() }
                )
            }
            composable(Routes.DASHBOARD) { DashboardScreen() }
            composable(Routes.ATTENDANCE) { AttendanceScreen() }
            composable(Routes.HISTORY) { HistoryScreen() }
            composable(Routes.PROFILE) { ProfileScreen(onLogout = { keluar() }) }
            composable(Routes.ADMIN) { AdminDashboardScreen(onLogout = { keluar() }) }
        }
    }
}