package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameRoom
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.MainLobbyRoomsList
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary

@Composable
fun LobbyRoomsScreen(
    viewModel: MainViewModel,
    rooms: List<GameRoom>,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = RoyalGold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Public Game Lobby",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Browse open tables for Call Break, Marriage, Teen Patti, Kitty, Ludo & Chess",
                        color = StatusGreen,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Full Main Lobby Rooms Component
        item {
            MainLobbyRoomsList(
                rooms = rooms,
                onJoinRoom = { room -> viewModel.joinAndEnterRoom(room) },
                onCreateTable = { viewModel.navigateTo(AppScreen.TABLE_CREATION) },
                showSearchBar = true
            )
        }
    }
}
