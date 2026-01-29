package com.example.veritas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.veritas.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    navController: NavHostController,
    categoryName: String,
    categoryType: String
) {
    var contentItems by remember { mutableStateOf<List<ContentItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Determine endpoint based on type
    val contentTypeParam = when(categoryType) {
        "AUDIO" -> "audio"
        "IMAGE" -> "image"
        else -> "pdf"
    }

    // Fetch content from FastAPI backend
    LaunchedEffect(categoryName, categoryType) {
        isLoading = true
        errorMessage = null

        try {
            withContext(Dispatchers.IO) {
                // Replace with your actual FastAPI backend URL
                val apiUrl = "http://YOUR_BACKEND_IP:8000/api/categories/$categoryName/$contentTypeParam"

                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val jsonArray = JSONArray(response)

                    val items = mutableListOf<ContentItem>()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        items.add(
                            ContentItem(
                                id = jsonObject.getInt("id"),
                                title = jsonObject.getString("title"),
                                url = jsonObject.getString("url"),
                                uploadDate = jsonObject.optString("upload_date", ""),
                                type = categoryType
                            )
                        )
                    }

                    withContext(Dispatchers.Main) {
                        contentItems = items
                        isLoading = false
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        errorMessage = "Failed to load content: Error $responseCode"
                        isLoading = false
                    }
                }

                connection.disconnect()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorMessage = "Error: ${e.message}"
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(categoryName, color = VeritasGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(categoryType, color = Color.Gray, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = VeritasGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VeritasDarkest)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(VeritasDarkest, VeritasDeepMaroon)))
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator(color = VeritasGold)
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                errorMessage ?: "Unknown error",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(containerColor = VeritasGold)
                            ) {
                                Text("Go Back", color = VeritasDarkest)
                            }
                        }
                    }
                }
                contentItems.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = when(categoryType) {
                                    "AUDIO" -> Icons.Default.MusicNote
                                    "IMAGE" -> Icons.Default.Image
                                    else -> Icons.Default.Description
                                },
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No ${categoryType.lowercase()} files available",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                            Text(
                                "in this category",
                                color = Color.Gray.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(contentItems) { content ->
                            ContentItemCard(
                                content = content,
                                onClick = {
                                    // Navigate to viewer/player based on type
                                    when(content.type) {
                                        "PDF" -> {
                                            // navController.navigate("pdf_viewer/${content.id}")
                                        }
                                        "AUDIO" -> {
                                            // navController.navigate("audio_player/${content.id}")
                                        }
                                        "IMAGE" -> {
                                            // navController.navigate("image_viewer/${content.id}")
                                        }
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContentItemCard(
    content: ContentItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(content.title, color = VeritasIvory, fontWeight = FontWeight.Bold)
            },
            supportingContent = {
                Text(
                    "Uploaded: ${content.uploadDate}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            },
            leadingContent = {
                Icon(
                    imageVector = when(content.type) {
                        "AUDIO" -> Icons.Default.PlayArrow
                        "IMAGE" -> Icons.Default.Image
                        else -> Icons.Default.Description
                    },
                    tint = VeritasGold,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            },
            trailingContent = {
                Row {
                    // Play/View button for audio/image
                    if (content.type == "AUDIO" || content.type == "IMAGE") {
                        IconButton(onClick = onClick) {
                            Icon(
                                imageVector = if (content.type == "AUDIO")
                                    Icons.Default.PlayCircle
                                else
                                    Icons.Default.Visibility,
                                tint = VeritasGold,
                                contentDescription = if (content.type == "AUDIO") "Play" else "View"
                            )
                        }
                    }
                    // Download button
                    IconButton(onClick = { /* Download logic */ }) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            tint = VeritasGold,
                            contentDescription = "Download"
                        )
                    }
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

data class ContentItem(
    val id: Int,
    val title: String,
    val url: String,
    val uploadDate: String,
    val type: String
)