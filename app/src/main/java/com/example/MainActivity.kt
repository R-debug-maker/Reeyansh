package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ChatScope
import com.example.ui.components.ChatBottomSheet
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.GameCatalogScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HostAuditScreen
import com.example.ui.screens.LobbyRoomsScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.TableCreationScreen
import com.example.ui.screens.TableGameScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RoyalTaasApp()
            }
        }
    }
}

@Composable
fun RoyalTaasApp(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val activeRooms by viewModel.activeRooms.collectAsState()
    val currentRoom by viewModel.currentActiveRoom.collectAsState()
    val selectedGame by viewModel.selectedGameType.collectAsState()
    val walletState by viewModel.walletState.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val currentLang by viewModel.selectedLanguage.collectAsState()

    val lobbyMessages by viewModel.lobbyMessages.collectAsState()
    val tableMessagesMap by viewModel.tableMessages.collectAsState()
    val tableVoiceState by viewModel.tableVoiceState.collectAsState()
    val isTableChatOpen by viewModel.isTableChatOpen.collectAsState()

    val recState by viewModel.voiceRecordingState.collectAsState()
    val playbackState by viewModel.voicePlaybackState.collectAsState()

    val ludoState by viewModel.ludoState.collectAsState()
    val chessState by viewModel.chessState.collectAsState()
    val snakePositions by viewModel.snakeLadderPositions.collectAsState()

    var isLobbyChatOpen by remember { mutableStateOf(false) }

    // Audio recording runtime permission launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startRecordingVoice()
        }
    }

    fun requestAudioAndRecord() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            viewModel.startRecordingVoice()
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Android Back button handling
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SurfaceDark,
        bottomBar = {
            // Show bottom navigation on primary tabs
            if (currentScreen != AppScreen.TABLE_GAME) {
                NavigationBar(
                    containerColor = SurfaceCard,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    val navItems = listOf(
                        Triple(AppScreen.HOME, "Home", Icons.Default.Home),
                        Triple(AppScreen.GAME_CATALOG, "Games", Icons.Default.Casino),
                        Triple(AppScreen.LOBBY_ROOMS, "Rooms", Icons.Default.MeetingRoom),
                        Triple(AppScreen.FRIENDS, "Friends", Icons.Default.Group),
                        Triple(AppScreen.WALLET, "Wallet", Icons.Default.Wallet)
                    )

                    navItems.forEach { (screen, label, icon) ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(screen) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SurfaceDark,
                                selectedTextColor = RoyalGoldLight,
                                indicatorColor = RoyalGold,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        user = currentUser,
                        activeRooms = activeRooms,
                        lobbyMessages = lobbyMessages,
                        onOpenLobbyChat = { isLobbyChatOpen = true }
                    )
                }

                AppScreen.GAME_CATALOG -> {
                    GameCatalogScreen(
                        viewModel = viewModel,
                        selectedGame = selectedGame,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                AppScreen.TABLE_CREATION -> {
                    TableCreationScreen(
                        viewModel = viewModel,
                        initialGameType = selectedGame,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                AppScreen.LOBBY_ROOMS -> {
                    LobbyRoomsScreen(
                        viewModel = viewModel,
                        rooms = activeRooms,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                AppScreen.TABLE_GAME -> {
                    TableGameScreen(
                        viewModel = viewModel,
                        room = currentRoom,
                        currentUser = currentUser,
                        ludoState = ludoState,
                        chessState = chessState,
                        snakePositions = snakePositions,
                        onBack = { viewModel.navigateBack() },
                        onOpenChat = { viewModel.setTableChatOpen(true) }
                    )
                }

                AppScreen.WALLET -> {
                    WalletScreen(
                        viewModel = viewModel,
                        wallet = walletState,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                AppScreen.FRIENDS -> {
                    FriendsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                AppScreen.HOST_AUDIT -> {
                    HostAuditScreen(
                        viewModel = viewModel,
                        auditLogs = auditLogs,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                AppScreen.PROFILE_SETTINGS -> {
                    ProfileSettingsScreen(
                        viewModel = viewModel,
                        user = currentUser,
                        currentLanguage = currentLang,
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }

            // Global Lobby Chat BottomSheet
            if (isLobbyChatOpen) {
                ChatBottomSheet(
                    title = "Lobby Chat Lounge",
                    scope = ChatScope.GLOBAL_LOBBY,
                    roomId = null,
                    currentUser = currentUser,
                    messages = lobbyMessages,
                    voiceChannelState = tableVoiceState,
                    recordingState = recState,
                    playbackState = playbackState,
                    onDismiss = { isLobbyChatOpen = false },
                    onSendMessage = { text -> viewModel.sendLobbyMessage(text) },
                    onSendEmoji = { emoji -> viewModel.sendLobbyMessage(emoji) },
                    onStartVoiceRecord = { requestAudioAndRecord() },
                    onStopAndSendVoice = { viewModel.stopAndSendVoice(ChatScope.GLOBAL_LOBBY) },
                    onCancelVoiceRecord = { viewModel.cancelVoiceRecording() },
                    onPlayVoiceMessage = { msg -> viewModel.playVoiceMessage(msg) },
                    onToggleMic = { viewModel.toggleTableMic() },
                    onToggleSpeaker = { viewModel.toggleTableSpeaker() }
                )
            }

            // In-Game Table Chat BottomSheet (while playing games at the table!)
            if (isTableChatOpen && currentRoom != null) {
                val tableMsgs = tableMessagesMap[currentRoom!!.id] ?: emptyList()
                ChatBottomSheet(
                    title = "Table Chat (${currentRoom!!.name})",
                    scope = ChatScope.GAME_TABLE,
                    roomId = currentRoom!!.id,
                    currentUser = currentUser,
                    messages = tableMsgs,
                    voiceChannelState = tableVoiceState,
                    recordingState = recState,
                    playbackState = playbackState,
                    onDismiss = { viewModel.setTableChatOpen(false) },
                    onSendMessage = { text -> viewModel.sendTableMessage(currentRoom!!.id, text) },
                    onSendEmoji = { emoji -> viewModel.sendTableEmoji(currentRoom!!.id, emoji) },
                    onStartVoiceRecord = { requestAudioAndRecord() },
                    onStopAndSendVoice = { viewModel.stopAndSendVoice(ChatScope.GAME_TABLE, currentRoom!!.id) },
                    onCancelVoiceRecord = { viewModel.cancelVoiceRecording() },
                    onPlayVoiceMessage = { msg -> viewModel.playVoiceMessage(msg) },
                    onToggleMic = { viewModel.toggleTableMic() },
                    onToggleSpeaker = { viewModel.toggleTableSpeaker() }
                )
            }
        }
    }
}
