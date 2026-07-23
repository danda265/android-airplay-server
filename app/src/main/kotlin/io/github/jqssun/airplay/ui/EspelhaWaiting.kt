package io.github.jqssun.airplay.ui

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.net.Inet4Address
import java.net.NetworkInterface

/** LAN IPv4 address of this device (192.168.x / 10.x / 172.16-31.x), or null. */
fun localIpAddress(): String? = try {
    NetworkInterface.getNetworkInterfaces().asSequence()
        .filter { it.isUp && !it.isLoopback }
        .flatMap { it.inetAddresses.asSequence() }
        .filterIsInstance<Inet4Address>()
        .firstOrNull { !it.isLoopbackAddress && it.isSiteLocalAddress }
        ?.hostAddress
} catch (e: Exception) {
    null
}

/** Generate a QR bitmap for [content] at [sizePx] square, or null on failure. */
fun generateQrBitmap(content: String, sizePx: Int): Bitmap? = try {
    val hints = mapOf(
        com.google.zxing.EncodeHintType.ERROR_CORRECTION to com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M,
        com.google.zxing.EncodeHintType.MARGIN to 1
    )
    val matrix = com.google.zxing.qrcode.QRCodeWriter()
        .encode(content, com.google.zxing.BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
    val w = matrix.width
    val h = matrix.height
    val pixels = IntArray(w * h)
    for (y in 0 until h) {
        val row = y * w
        for (x in 0 until w) {
            pixels[row + x] = if (matrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE
        }
    }
    Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply { setPixels(pixels, 0, w, 0, 0, w, h) }
} catch (e: Exception) {
    null
}

/**
 * Branded "waiting for connection" screen for Espelha Danda: brand header,
 * how-to instructions, the device IP, and a QR code.
 *
 * The QR encodes an `espelha://connect` URL — decorative for now (iOS AirPlay
 * can't be started by a QR), but the future Android sender app (Fase 3) will
 * scan it to connect automatically.
 */
@Composable
fun EspelhaWaiting(serverName: String, modifier: Modifier = Modifier) {
    val ip = remember { localIpAddress() }
    val qr = remember(serverName, ip) {
        if (ip == null) null
        else generateQrBitmap("espelha://connect?name=${Uri.encode(serverName)}&ip=$ip&port=7000", 320)
    }
    val onVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier.fillMaxSize().padding(horizontal = 40.dp, vertical = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Cast,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = serverName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Espelhe sua tela aqui",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = onVariant
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "iPhone: Central de Controle → Espelhamento\nAndroid: app Espelha Danda → Transmitir",
                style = MaterialTheme.typography.bodyMedium,
                color = onVariant.copy(alpha = 0.7f)
            )
            if (ip != null) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Endereço na rede",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = ip,
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = onVariant
                )
            }
        }

        if (qr != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(color = androidx.compose.ui.graphics.Color.White, shape = RoundedCornerShape(12.dp)) {
                    Image(
                        bitmap = qr.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp).size(132.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "aponte a câmera",
                    style = MaterialTheme.typography.labelMedium,
                    color = onVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
