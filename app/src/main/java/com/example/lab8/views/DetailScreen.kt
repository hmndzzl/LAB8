package com.example.lab8.views

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.lab8.data.local.AppDatabase
import com.example.lab8.data.local.entities.PhotoEntity
import com.example.lab8.data.repository.PhotoRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    photoId: Long,
    database: AppDatabase,
    onBackClick: () -> Unit
) {
    val repository = remember { PhotoRepository(database) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var photo by remember { mutableStateOf<PhotoEntity?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(photoId) {
        loading = true
        photo = repository.fetchPhotoDetails(photoId)
        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (photo != null) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    repository.toggleFavorite(photoId)
                                    photo = repository.fetchPhotoDetails(photoId)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (photo?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (photo?.isFavorite == true) Color.Red else LocalContentColor.current
                            )
                        }

                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, photo?.srcOriginal ?: "")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Compartir foto"))
                            }
                        ) {
                            Icon(Icons.Default.Share, "Compartir")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                photo == null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Foto no encontrada")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onBackClick) {
                            Text("Volver")
                        }
                    }
                }
                else -> {
                    PhotoDetailContent(photo = photo!!)
                }
            }
        }
    }
}

@Composable
fun PhotoDetailContent(photo: PhotoEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Large image
        AsyncImage(
            model = photo.srcLarge ?: photo.srcOriginal,
            contentDescription = photo.alt,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(photo.width.toFloat() / photo.height.toFloat()),
            contentScale = ContentScale.Crop
        )

        // Details
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = photo.alt ?: "Sin título",
                style = MaterialTheme.typography.headlineSmall
            )

            Divider()

            DetailRow(label = "Fotógrafo", value = photo.photographer)
            DetailRow(label = "Dimensiones", value = "${photo.width} x ${photo.height}")

            if (photo.avgColor != null) {
                DetailRow(label = "Color promedio", value = photo.avgColor)
            }

            DetailRow(
                label = "Favorito",
                value = if (photo.isFavorite) "Sí" else "No"
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}