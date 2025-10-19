package com.example.ap7mt.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ap7mt.ui.theme.GameDatabaseTheme
import com.example.ap7mt.ui.viewmodel.MapViewModel
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

data class ElectronicStore(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val type: String = "Elektro obchod"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mapView = remember { MapView(context) }
    val stores by viewModel.stores.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val selectedStore by viewModel.selectedStore.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.fetchUserLocation(context)
        } else {
            viewModel.loadStores()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    LaunchedEffect(userLocation) {
        scope.launch { viewModel.centerOnUserLocation(mapView) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                Configuration.getInstance().userAgentValue = ctx.packageName
                mapView.apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(17.0)

                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                    locationOverlay.enableMyLocation()
                    overlays.add(locationOverlay)

                    val startPoint = GeoPoint(49.2308443, 17.657083)
                    controller.setCenter(startPoint)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
            update = { map ->
                map.overlays.clear()

                val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
                locationOverlay.enableMyLocation()
                map.overlays.add(locationOverlay)

                val center = userLocation?.let { GeoPoint(it.latitude, it.longitude) }
                    ?: GeoPoint(49.2308443, 17.657083)
                map.controller.setCenter(center)

                stores.forEach { store: ElectronicStore ->
                    val marker = Marker(map).apply {
                        position = GeoPoint(store.latitude, store.longitude)
                        title = store.name
                        snippet = "${store.address}\n${store.type}"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = context.getDrawable(android.R.drawable.ic_menu_compass)?.apply {
                            setTint(android.graphics.Color.BLUE)
                        }
                        setOnMarkerClickListener { marker, mapView ->
                            viewModel.selectStore(store)
                            mapView.controller.animateTo(GeoPoint(store.latitude, store.longitude))
                            mapView.controller.setZoom(18.0)
                            true
                        }
                    }
                    map.overlays.add(marker)
                }

                map.invalidate()
            }
        )

        if (isLoading) {
            androidx.compose.material3.Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Načítání obchodů...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                }
            }
        }

        if (error != null) {
            androidx.compose.material3.Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Chyba",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = error!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    OutlinedButton(onClick = { viewModel.loadStores() }) {
                        Text("Zkusit znovu")
                    }
                }
                }
            }
        }

        TopAppBar(
            title = {
                Text(
                    "GameDatabase",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                )
            },
            actions = {
                IconButton(
                    onClick = {
                        val fineGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        val coarseGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (fineGranted || coarseGranted) {
                            viewModel.fetchUserLocation(context)
                        } else {
                            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                        }
                    }
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Moje poloha")
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        )

        if (selectedStore != null) {
            StoreDetailCard(
                store = selectedStore!!,
                onDismiss = { viewModel.selectStore(null) },
                modifier = Modifier.align(Alignment.BottomCenter),
                context = context
            )
        }
    }
}

@Composable
fun StoreList(
    stores: List<ElectronicStore>,
    userLocation: Location?,
    onStoreClick: (ElectronicStore) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(stores.sortedBy { store ->
            userLocation?.let { location ->
                val storeLocation = Location("").apply {
                    latitude = store.latitude
                    longitude = store.longitude
                }
                location.distanceTo(storeLocation)
            } ?: 0f
        }) { store ->
            StoreListItem(
                store = store,
                userLocation = userLocation,
                onClick = { onStoreClick(store) }
            )
        }
    }
}

@Composable
fun StoreListItem(
    store: ElectronicStore,
    userLocation: Location?,
    onClick: () -> Unit
) {
    val distance = userLocation?.let { location ->
        val storeLocation = Location("").apply {
            latitude = store.latitude
            longitude = store.longitude
        }
        val distanceInMeters = location.distanceTo(storeLocation)
        if (distanceInMeters < 1000) {
            "%.0f m".format(distanceInMeters)
        } else {
            "%.1f km".format(distanceInMeters / 1000)
        }
    } ?: "Neznámo"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = store.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${store.type} • ${distance}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StoreDetailCard(
    store: ElectronicStore,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    context: Context
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = onDismiss) {
                    Text("×", style = MaterialTheme.typography.headlineMedium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = store.address,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = store.type,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    val uri = Uri.parse("geo:${store.latitude},${store.longitude}?q=${store.latitude},${store.longitude}(${store.name})")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps")
                    try {
                        context.startActivity(intent)
                    } catch (e: android.content.ActivityNotFoundException) {
                        val fallbackIntent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(fallbackIntent)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Navigovat")
            }
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Chyba",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            OutlinedButton(onClick = onRetry) {
                Text("Zkusit znovu")
            }
        }
    }
}
