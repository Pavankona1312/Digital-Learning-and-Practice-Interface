package com.example.vault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.vault.navigation.AppScaffold
import com.example.vault.ui.theme.VaultTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VaultTheme {
                AppScaffold()
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun VaultPreview() {
    VaultTheme {
           AppScaffold()
    }
}