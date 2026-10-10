package com.kelompok.absensi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kelompok.absensi.navigation.AppNavHost
import com.kelompok.absensi.ui.theme.AbsensiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AbsensiTheme {
                AppNavHost()
            }
        }
    }
}