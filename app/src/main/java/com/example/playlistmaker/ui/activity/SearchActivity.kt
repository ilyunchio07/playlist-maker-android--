package com.example.playlistmaker.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.HistoryRequests
import com.example.playlistmaker.ui.TrackListItem
import com.example.playlistmaker.ui.state.SearchState
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.ui.theme.YP_LIGHT_GRAY
import com.example.playlistmaker.ui.theme.YP_TEXT_GRAY
import com.example.playlistmaker.ui.view_model.SearchViewModel

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaylistMakerTheme {
                SearchScreen(
                    onBackClick = { finish() },
                    onTrackClick = {}
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory)
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    val history by viewModel.historyList.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(screenState) {
        if (screenState is SearchState.Content) {
            focusManager.clearFocus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.search)) },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                modifier = Modifier.height(56.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 8.dp)
        ) {

            BasicTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.updateQuery(it)
                },
                textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.updateQuery(searchText) }),
                modifier = Modifier.onFocusChanged { isFocused = it.isFocused },
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(36.dp)
                            .background(YP_LIGHT_GRAY, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search),
                            tint = YP_TEXT_GRAY,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { viewModel.updateQuery(searchText) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchText.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.search),
                                    style = TextStyle(fontSize = 16.sp, color = YP_TEXT_GRAY)
                                )
                            }
                            innerTextField()
                        }
                        if (searchText.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear_search),
                                tint = YP_TEXT_GRAY,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        searchText = ""
                                        viewModel.updateQuery("")
                                        focusManager.clearFocus()
                                    }
                            )
                        }
                    }
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (isFocused && searchText.isEmpty() && history.isNotEmpty()) {
                    HistoryRequests(
                        historyList = history.map { it.word },
                        onClick = { word ->
                            searchText = word
                            viewModel.updateQuery(word) // Сразу ищем
                            focusManager.clearFocus()
                        }
                    )
                } else {
                    when (val state = screenState) {
                        is SearchState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        is SearchState.Content -> {
                            LazyColumn(
                                modifier = Modifier.padding(top = 16.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(state.tracks) { track ->
                                    TrackListItem(
                                        track = track,
                                        onClick = { onTrackClick(track) }
                                    )
                                }
                            }
                        }
                        is SearchState.Empty -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_nothing_found),
                                    contentDescription = stringResource(R.string.nothing_found),
                                    modifier = Modifier.size(120.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.nothing_found),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        is SearchState.Error -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_connection_error),
                                    contentDescription = stringResource(R.string.connection_error),
                                    modifier = Modifier.size(120.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.connection_error),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { viewModel.refreshSearch() }, // ВЫЗЫВАЕМ REFRESH
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(text = stringResource(R.string.refresh))
                                }
                            }
                        }
                        is SearchState.Initial -> { }
                    }
                }
            }
        }
    }
}