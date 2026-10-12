package com.kelompok.absensi.ui.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/** Kolom isian teks biasa. */
@Composable
fun KolomTeks(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        modifier = modifier.fillMaxWidth()
    )
}

/** Kolom password dengan tombol Tampilkan/Sembunyikan. */
@Composable
fun KolomPassword(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next
) {
    var terlihat by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (terlihat) VisualTransformation.None
        else PasswordVisualTransformation(),
        trailingIcon = {
            TextButton(onClick = { terlihat = !terlihat }) {
                Text(if (terlihat) "Sembunyikan" else "Tampilkan")
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/** Tombol utama. Saat memuat, tombol nonaktif dan menampilkan lingkaran loading. */
@Composable
fun TombolUtama(
    teks: String,
    sedangMemuat: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !sedangMemuat,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        if (sedangMemuat) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        } else {
            Text(teks)
        }
    }
}

/** Menampilkan pesan error berwarna merah. Tidak menampilkan apa pun kalau pesannya null. */
@Composable
fun PesanError(pesan: String?, modifier: Modifier = Modifier) {
    if (pesan != null) {
        Text(
            text = pesan,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
        )
    }
}