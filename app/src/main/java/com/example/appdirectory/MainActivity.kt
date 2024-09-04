package com.example.appdirectory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appdirectory.feature_directory.presentation.WordInfoItem
import com.example.appdirectory.feature_directory.presentation.WordInfoViewModel
import com.example.appdirectory.ui.theme.AppDirectoryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppDirectoryTheme {
                val viewModel: WordInfoViewModel = hiltViewModel()
                val state = viewModel.state.value
                val scaffoldState = rememberScaffoldState()
                var showHistoryDialog by remember { mutableStateOf(false) }

                LaunchedEffect(key1 = true) {
                    viewModel.eventFlow.collectLatest { event ->
                        when(event) {
                            is WordInfoViewModel.UIEvent.ShowSnackbar -> {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = event.message
                                )
                            }
                        }
                    }
                }

                Scaffold(
                    scaffoldState = scaffoldState
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colors.background)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Search TextField
                            OutlinedTextField(
                                value = viewModel.searchQuery.value,
                                onValueChange = viewModel::onSearch,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(text = "Search...")
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Display suggestions
                            if (state.suggestions.isNotEmpty()) {
                                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                    items(state.suggestions.size) { i ->
                                        val suggestion = state.suggestions[i]
                                        Text(
                                            text = suggestion,
                                            modifier = Modifier
                                                .clickable { viewModel.onSearch(suggestion) }
                                                .padding(8.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Show History Button
                            Button(onClick = { showHistoryDialog = true }, modifier = Modifier.fillMaxWidth()) {
                                Text("Show Search History")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Display word info items
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.wordInfoItems.size) { i ->
                                    val wordInfo = state.wordInfoItems[i]
                                    if (i > 0) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    WordInfoItem(wordInfo = wordInfo)
                                    if (i < state.wordInfoItems.size - 1) {
                                        Divider()
                                    }
                                }
                            }
                        }
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                        if (showHistoryDialog) {
                            AlertDialog(
                                onDismissRequest = { showHistoryDialog = false },
                                title = { Text("Search History") },
                                text = {
                                    Column {
                                        val history = viewModel.getSearchHistory()
                                        if (history.isNotEmpty()) {
                                            history.forEach { item ->
                                                Text(text = item)
                                            }
                                        } else {
                                            Text(text = "No history found.")
                                        }
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = { showHistoryDialog = false }) {
                                        Text("Close")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}