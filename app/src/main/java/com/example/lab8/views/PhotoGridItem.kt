package com.example.lab8.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.lab8.data.local.entities.PhotoEntity

@Composable
fun PhotoGridItem(
    photo: PhotoEntity,
    onPhotoClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable(onClick = onPhotoClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            AsyncImage(
                model = photo.srcMedium ?: photo.srcSmall ?: photo.srcOriginal,
                contentDescription = photo.alt ?: photo.photographer,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Favorite button overlay
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.5f)
            ) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (photo.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (photo.isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (photo.isFavorite) Color.Red else Color.White
                    )
                }
            }

            // Photographer name at bottom
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Text(
                    text = photo.photographer,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier.padding(8.dp),
                    maxLines = 1
                )
            }
        }
    }
}