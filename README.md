# LAB8
# Galería Pexels con Room

## Decisiones de modelado
- PhotoEntity: incluye queryKey y pageIndex para organizar cache
- RemoteKeyEntity: maneja prevKey/nextKey para paginación
- RecentQueryEntity: almacena últimas 10 búsquedas

## Estrategia de cache/paginación
- Paging 3 con RemoteMediator para cache automático
- Cache-first: lee de Room, luego red
- Favoritos se preservan entre refrescos

## Manejo de estado sin ViewModel
- Estado local con remember y mutableStateOf
- Flows de Room para reactividad
- LaunchedEffect para operaciones async

## Consideraciones offline
- RemoteMediator maneja errores de red
- Muestra cache existente cuando falla la red
- Favoritos siempre disponibles offline
