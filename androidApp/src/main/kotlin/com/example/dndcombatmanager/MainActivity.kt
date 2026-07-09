package com.example.dndcombatmanager

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.dndcombatmanager.combat.platform.AndroidImagePickerBridge

class MainActivity : ComponentActivity() {
    private var pendingImageResult: ((ByteArray?) -> Unit)? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val onResult = pendingImageResult
        pendingImageResult = null
        val bytes = uri?.let { runCatching { contentResolver.openInputStream(it)?.use { s -> s.readBytes() } }.getOrNull() }
        onResult?.invoke(bytes)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        AndroidImagePickerBridge.launch = { onResult ->
            pendingImageResult = onResult
            pickImageLauncher.launch("image/*")
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}