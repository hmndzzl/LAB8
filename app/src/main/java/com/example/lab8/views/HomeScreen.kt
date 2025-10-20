package com.example.lab8.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.lab8.data.local.AppDatabase
import com.example.lab8.data.local.entities.PhotoEntity
import com.example.lab8.data.repository.PhotoRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun HomeScreen(
    database: AppDatabase,
    onPhotoClick: (Long) -> Unit,
    onProfileClick: () -> Unit
) {
    val repository = remember { PhotoRepository(database) }

    var searchQuery by remember { mutableStateOf("") }
    val searchFlow = remember { MutableStateFlow("curated") }

    // Debounce search
    LaunchedEffect(searchQuery) {
        delay(500)
        val normalized = searchQuery.trim().ifEmpty { "curated" }
        searchFlow.value = normalized
        repository.saveRecentQuery(normalized)
    }

    val currentQuery by searchFlow.collectAsState()
    val pagingItems = remember(currentQuery) {
        repository.getPhotosPaged(currentQuery)
    }.collectAsLazyPagingItems()

    val recentQueries by repository.getRecentQueries().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fotos") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, "Perfil")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Recent queries
            if (recentQueries.isNotEmpty()) {
                Text(
                    "Búsquedas recientes",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(recentQueries) { query ->
                        SuggestionChip(
                            onClick = { searchQuery = query.query },
                            label = { Text(query.query) }
                        )
                    }
                }
            }

            // Photo grid with paging
            PhotoGrid(
                pagingItems = pagingItems,
                repository = repository,
                onPhotoClick = onPhotoClick,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun PhotoGrid(
    pagingItems: LazyPagingItems<PhotoEntity>,
    repository: PhotoRepository,
    onPhotoClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                count = pagingItems.itemCount,
                key = { index -> pagingItems[index]?.id ?: index }
            ) { index ->
                val photo = pagingItems[index]
                if (photo != null) {
                    PhotoGridItem(
                        photo = photo,
                        onPhotoClick = { onPhotoClick(photo.id) },
                        onFavoriteClick = {
                            kotlinx.coroutines.MainScope().launch {
                                repository.toggleFavorite(photo.id)
                                pagingItems.refresh()
                            }
                        }
                    )
                }
            }

            // Loading states
            when (pagingItems.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is LoadState.Error -> {
                    item {
                        ErrorItem(
                            message = "Error al cargar más fotos",
                            onRetry = { pagingItems.retry() }
                        )
                    }
                }
                else -> {}
            }
        }

        // Initial loading
        if (pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Initial error
        if (pagingItems.loadState.refresh is LoadState.Error && pagingItems.itemCount == 0) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Error al cargar fotos")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { pagingItems.refresh() }) {
                    Text("Reintentar")
                }
            }
        }
    }
}

@Composable
fun ErrorItem(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message)
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Buscar fotos...") },
        leadingIcon = {
            Icon(Icons.Default.Search, "Buscar")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, "Limpiar")
                }
            }
        },
        singleLine = true
    )
}