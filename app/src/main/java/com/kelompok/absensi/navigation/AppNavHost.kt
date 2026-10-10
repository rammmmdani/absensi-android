package com.kelompok.absensi.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelompok.absensi.ui.admin.AdminDashboardScreen
import com.kelompok.absensi.ui.attendance.AttendanceScreen
import com.kelompok.absensi.ui.dashboard.DashboardScreen
import com.kelompok.absensi.ui.history.HistoryScreen
import com.kelompok.absensi.ui.profile.ProfileScreen

/** Satu tombol di menu bawah. */
private data class MenuBawah(
    val route: String,
    val label: String,
    val icon: ImageVector
)

// Sementara semua menu tampil. Nanti menu Admin hanya muncul untuk role admin.
private val daftarMenu = listOf(
    MenuBawah(Routes.DASHBOARD, "Beranda", Icons.Default.Home),
    MenuBawah(Routes.ATTENDANCE, "Absensi", Icons.Default.CheckCircle),
    MenuBawah(Routes.HISTORY, "Riwayat", Icons.Default.DateRange),
    MenuBawah(Routes.PROFILE, "Profil", Icons.Default.Person),
    MenuBawah(Routes.ADMIN, "Admin", Icons.Default.Settings)
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val ruteSekarang = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                daftarMenu.forEach { menu ->
                    NavigationBarItem(
                        selected = ruteSekarang == menu.route,
                        onClick = {
                            navController.navigate(menu.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(menu.icon, contentDescription = menu.label) },
                        label = { Text(menu.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) { DashboardScreen() }
            composable(Routes.ATTENDANCE) { AttendanceScreen() }
            composable(Routes.HISTORY) { HistoryScreen() }
            composable(Routes.PROFILE) {
                ProfileScreen(onLogout = { /* nanti: kembali ke halaman login */ })
            }
            composable(Routes.ADMIN) {
                AdminDashboardScreen(onLogout = { /* nanti: kembali ke halaman login */ })
            }
        }
    }
}