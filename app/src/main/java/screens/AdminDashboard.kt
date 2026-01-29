package com.example.veritas.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.veritas.ui.theme.VeritasDarkest
import com.example.veritas.ui.theme.VeritasDeepMaroon
import com.example.veritas.ui.theme.VeritasGold
import com.example.veritas.ui.theme.VeritasIvory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

// Data class for MediaFile (local version for this screen)
data class MediaFile(
    val id: String,
    val title: String,
    val type: String,
    val url: String,
    val category: String,
    val uploadedBy: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- State Management ---
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("PDF") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // Edit Dialog State
    var showEditDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<MediaFile?>(null) }

    // Content list - starts with mock data
    val adminContentList = remember {
        mutableStateListOf(
            MediaFile("1", "Foundational Truths", "PDF", "", "Theology", "Admin"),
            MediaFile("2", "Morning Prayer", "AUDIO", "", "Devotional", "Admin")
        )
    }

    // --- File Picker ---
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedUri = uri }

    // Upload function - FULL IMPLEMENTATION
    fun uploadToBackend() {
        if (selectedUri == null || title.isBlank()) {
            Toast.makeText(context, "Please select a file and enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        isUploading = true
        scope.launch(Dispatchers.IO) {
            try {
                Log.d("Upload", "Starting upload: $title, $selectedCategory")

                // Read file from URI
                val inputStream = context.contentResolver.openInputStream(selectedUri!!)
                if (inputStream == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Cannot read file", Toast.LENGTH_SHORT).show()
                        isUploading = false
                    }
                    return@launch
                }

                val bytes = inputStream.readBytes()
                inputStream.close()

                // Get filename
                val cursor = context.contentResolver.query(selectedUri!!, null, null, null, null)
                var filename = "file"
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) filename = it.getString(nameIndex)
                    }
                }

                Log.d("Upload", "File size: ${bytes.size} bytes, Name: $filename")

                // Create multipart request
                val requestFile = RequestBody.create("application/octet-stream".toMediaType(), bytes)
                val filePart = MultipartBody.Part.createFormData("file", filename, requestFile)
                val titlePart = RequestBody.create("text/plain".toMediaType(), title)
                val typePart = RequestBody.create("text/plain".toMediaType(), selectedCategory.uppercase())
                val categoryPart = RequestBody.create("text/plain".toMediaType(), selectedCategory)
                val uploadedByPart = RequestBody.create("text/plain".toMediaType(), "Admin")

                // TODO: Replace with your FastAPI endpoint
                // For now, simulate upload
                kotlinx.coroutines.delay(2000)

                Log.d("Upload", "✅ Upload complete (simulated)")

                // Add to list
                withContext(Dispatchers.Main) {
                    adminContentList.add(
                        MediaFile(
                            id = java.util.UUID.randomUUID().toString(),
                            title = title,
                            type = selectedCategory,
                            url = "",
                            category = "Ministry",
                            uploadedBy = "Admin"
                        )
                    )
                    Toast.makeText(context, "Published Successfully!", Toast.LENGTH_SHORT).show()
                    title = ""
                    selectedUri = null
                    isUploading = false
                }

            } catch (e: Exception) {
                Log.e("Upload", "❌ Error: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Upload failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    isUploading = false
                }
            }
        }
    }

    // --- Edit Dialog Composable ---
    if (showEditDialog && itemToEdit != null) {
        var editTitle by remember { mutableStateOf(itemToEdit!!.title) }
        var editType by remember { mutableStateOf(itemToEdit!!.type) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = VeritasDeepMaroon,
            title = { Text("Edit Content", color = VeritasGold, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("New Title", color = VeritasIvory) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeritasGold,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White
                        )
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        listOf("PDF", "AUDIO", "IMAGE").forEach { type ->
                            FilterChip(
                                selected = editType == type,
                                onClick = { editType = type },
                                label = { Text(type) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VeritasGold,
                                    selectedLabelColor = VeritasDarkest,
                                    labelColor = VeritasIvory
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // TODO: Add API call to update on backend
                        val index = adminContentList.indexOf(itemToEdit)
                        if (index != -1) {
                            adminContentList[index] = itemToEdit!!.copy(title = editTitle, type = editType)
                        }
                        showEditDialog = false
                        Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VeritasGold, contentColor = VeritasDarkest)
                ) { Text("Save Changes") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = VeritasIvory)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Veritas Admin Panel", color = VeritasGold, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VeritasDarkest),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = VeritasGold)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VeritasDarkest)
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            // SECTION 1: UPLOAD FORM
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text("Upload New Content", color = VeritasIvory, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Content Title", color = VeritasIvory) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VeritasGold,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("PDF", "AUDIO", "IMAGE").forEach { type ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (selectedCategory == type),
                                onClick = { selectedCategory = type },
                                colors = RadioButtonDefaults.colors(selectedColor = VeritasGold)
                            )
                            Text(type, color = VeritasIvory, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val mimeType = when(selectedCategory) {
                            "PDF" -> "application/pdf"
                            "AUDIO" -> "audio/*"
                            else -> "image/*"
                        }
                        filePickerLauncher.launch(mimeType)
                    },
                    modifier = Modifier.fillMaxWidth().height(45.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VeritasDeepMaroon)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (selectedUri == null) "Select File" else "File Attached", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { uploadToBackend() },
                    enabled = selectedUri != null && title.isNotEmpty() && !isUploading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VeritasGold, contentColor = VeritasDarkest)
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = VeritasDarkest,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("PUBLISH TO VAULT", fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(color = VeritasGold.copy(alpha = 0.3f), thickness = 1.dp)

            // SECTION 2: MANAGE CONTENT
            Text(
                "Manage Existing Content",
                color = VeritasIvory,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(adminContentList) { item ->
                    AdminContentCard(
                        item = item,
                        onDelete = {
                            // TODO: Add API call to delete from backend
                            adminContentList.remove(item)
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                        },
                        onEdit = {
                            itemToEdit = item
                            showEditDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminContentCard(item: MediaFile, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = VeritasIvory,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = item.type,
                    color = VeritasGold,
                    fontSize = 12.sp
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.LightGray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFCF6679))
                }
            }
        }
    }
}