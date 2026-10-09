package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.VoiceMessageManager
import com.example.model.Card
import com.example.model.ChatMessage
import com.example.model.ChatScope
import com.example.model.GameRoom
import com.example.model.GameType
import com.example.model.PlayerStatus
import com.example.model.RoomType
import com.example.model.RulePreset
import com.example.model.TableVoiceChannelState
import com.example.model.TransactionType
import com.example.model.UserProfile
import com.example.repository.ChatRepository
import com.example.repository.GameRepository
import com.example.repository.WalletRepository
import com.example.sound.SoundAndHapticManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    GAME_CATALOG,
    TABLE_CREATION,
    LOBBY_ROOMS,
    TABLE_GAME,
    WALLET,
    FRIENDS,
    HOST_AUDIT,
    PROFILE_SETTINGS
}

enum class AppLanguage(val title: String, val code: String) {
    ENGLISH("English", "en"),
    NEPALI("नेपाली (Nepali)", "ne"),
    HINDI("हिन्दी (Hindi)", "hi")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val soundManager = SoundAndHapticManager(application)
    val voiceManager = VoiceMessageManager(application)

    private val gameRepo = GameRepository()
    private val walletRepo = WalletRepository()
    private val chatRepo = ChatRepository()

    // Current User Profile
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "USER_ROYAL_01",
            displayName = "Rakesh_Royal",
            phoneNumber = "+977-9841234567",
            avatarEmoji = "👑",
            status = PlayerStatus.ONLINE,
            practiceCoins = 10500L,
            gamesPlayed = 48,
            gamesWon = 33,
            winRate = 68.75f,
            badges = listOf("Royal Pioneer", "Hukum Ace", "Table King"),
            isVerified = true
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Screen stack for BackHandler
    private val screenStack = mutableListOf<AppScreen>()

    // Selected game in catalog / creation
    private val _selectedGameType = MutableStateFlow(GameType.CALL_BREAK)
    val selectedGameType: StateFlow<GameType> = _selectedGameType.asStateFlow()

    // Selected game mode for catalog
    private val _selectedRoomType = MutableStateFlow(RoomType.SOLO)
    val selectedRoomType: StateFlow<RoomType> = _selectedRoomType.asStateFlow()

    // Language
    private val _selectedLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    // Voice channel in-game table state
    private val _tableVoiceState = MutableStateFlow(TableVoiceChannelState())
    val tableVoiceState: StateFlow<TableVoiceChannelState> = _tableVoiceState.asStateFlow()

    // In-game chat bottom sheet visibility
    private val _isTableChatOpen = MutableStateFlow(false)
    val isTableChatOpen: StateFlow<Boolean> = _isTableChatOpen.asStateFlow()

    // Data streams from repositories
    val activeRooms = gameRepo.rooms
    val currentActiveRoom = gameRepo.currentActiveRoom
    val auditLogs = gameRepo.auditLogs
    val walletState = walletRepo.walletState
    val lobbyMessages = chatRepo.lobbyMessages
    val tableMessages = chatRepo.tableMessages
    val ludoState = gameRepo.ludoState
    val chessState = gameRepo.chessState
    val snakeLadderPositions = gameRepo.snakeLadderPositions

    val voiceRecordingState = voiceManager.recordingState
    val voicePlaybackState = voiceManager.playbackState

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
            return true
        }
        if (_currentScreen.value != AppScreen.HOME) {
            _currentScreen.value = AppScreen.HOME
            return true
        }
        return false
    }

    fun selectGame(game: GameType) {
        _selectedGameType.value = game
    }

    fun selectRoomType(type: RoomType) {
        _selectedRoomType.value = type
    }

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
    }

    fun toggleTableMic() {
        soundManager.triggerHaptic(20)
        _tableVoiceState.value = _tableVoiceState.value.copy(
            isMicMuted = !_tableVoiceState.value.isMicMuted
        )
    }

    fun toggleTableSpeaker() {
        soundManager.triggerHaptic(20)
        _tableVoiceState.value = _tableVoiceState.value.copy(
            isSpeakerMuted = !_tableVoiceState.value.isSpeakerMuted
        )
    }

    fun setTableChatOpen(isOpen: Boolean) {
        _isTableChatOpen.value = isOpen
    }

    // Voice Message Recording & Sending
    fun startRecordingVoice() {
        soundManager.triggerHaptic(30)
        voiceManager.startRecording()
    }

    fun stopAndSendVoice(scope: ChatScope, roomId: String? = null) {
        soundManager.triggerHaptic(40)
        val msg = voiceManager.stopAndCreateVoiceMessage(_currentUser.value, scope, roomId)
        if (msg != null) {
            if (scope == ChatScope.GLOBAL_LOBBY) {
                chatRepo.sendLobbyVoiceMessage(msg)
            } else if (roomId != null) {
                chatRepo.sendTableVoiceMessage(roomId, msg)
            }
        }
    }

    fun cancelVoiceRecording() {
        soundManager.triggerHaptic(20)
        voiceManager.cancelRecording()
    }

    fun playVoiceMessage(message: ChatMessage) {
        soundManager.triggerHaptic(15)
        voiceManager.playVoiceMessage(message)
    }

    // Text & Emoji Chat
    fun sendLobbyMessage(text: String) {
        if (text.isNotBlank()) {
            soundManager.playChipSound()
            chatRepo.sendLobbyTextMessage(_currentUser.value, text.trim())
        }
    }

    fun sendTableMessage(roomId: String, text: String) {
        if (text.isNotBlank()) {
            soundManager.playChipSound()
            chatRepo.sendTableTextMessage(roomId, _currentUser.value, text.trim())
        }
    }

    fun sendTableEmoji(roomId: String, emoji: String) {
        soundManager.triggerHaptic(25)
        chatRepo.sendTableEmojiReaction(roomId, _currentUser.value, emoji)
    }

    // Room Actions
    fun createAndEnterRoom(
        name: String,
        gameType: GameType,
        roomType: RoomType,
        entryCoins: Long,
        rulePreset: RulePreset = RulePreset()
    ) {
        soundManager.playCardDealSound()
        // Deduct table entry coins from practice ledger
        walletRepo.recordTransaction(
            type = TransactionType.TABLE_BUY_IN,
            amount = entryCoins,
            idempotencyKey = "BUYIN_" + System.currentTimeMillis(),
            source = _currentUser.value.id,
            destination = "TABLE_POT",
            notes = "Entry buy-in for ${gameType.title} table"
        )
        val room = gameRepo.createRoom(name, gameType, roomType, _currentUser.value, entryCoins, rulePreset)
        _currentUser.value = _currentUser.value.copy(
            practiceCoins = walletRepo.walletState.value.balance
        )
        navigateTo(AppScreen.TABLE_GAME)
    }

    fun joinAndEnterRoom(room: GameRoom) {
        soundManager.playCardFlipSound()
        walletRepo.recordTransaction(
            type = TransactionType.TABLE_BUY_IN,
            amount = room.entryCoins,
            idempotencyKey = "JOIN_" + room.id + "_" + System.currentTimeMillis(),
            source = _currentUser.value.id,
            destination = "TABLE_POT",
            notes = "Join buy-in for ${room.gameType.title}"
        )
        gameRepo.joinRoom(room, _currentUser.value)
        _currentUser.value = _currentUser.value.copy(
            practiceCoins = walletRepo.walletState.value.balance
        )
        navigateTo(AppScreen.TABLE_GAME)
    }

    fun startCurrentMatch() {
        val room = currentActiveRoom.value ?: return
        soundManager.playCardDealSound()
        gameRepo.startMatch(room.id, _currentUser.value.id)
    }

    fun playCard(card: Card) {
        soundManager.playCardFlipSound()
        gameRepo.playCallBreakCard(0, card)
    }

    fun teenPattiAction(action: String, amount: Long = 10L) {
        soundManager.playChipSound()
        gameRepo.makeTeenPattiAction(0, action, amount)
    }

    fun rollLudo() {
        soundManager.playDiceRollSound()
        gameRepo.rollLudoDice()
    }

    fun moveLudo(tokenIndex: Int) {
        soundManager.playChipSound()
        gameRepo.moveLudoToken(tokenIndex)
    }

    fun rollSnakeLadder() {
        soundManager.playDiceRollSound()
        val (dice, msg) = gameRepo.rollSnakeLadderDice(0)
        if (msg.contains("Champion")) {
            soundManager.playVictoryFanfare()
        }
    }

    fun selectChess(r: Int, c: Int) {
        soundManager.playChipSound()
        gameRepo.selectChessSquare(r, c)
    }

    fun claimDailyBonus(): Boolean {
        soundManager.playVictoryFanfare()
        val claimed = walletRepo.claimDailyBonus()
        if (claimed) {
            _currentUser.value = _currentUser.value.copy(
                practiceCoins = walletRepo.walletState.value.balance
            )
        }
        return claimed
    }

    fun updateProfile(name: String, emoji: String) {
        soundManager.triggerHaptic(20)
        _currentUser.value = _currentUser.value.copy(
            displayName = name,
            avatarEmoji = emoji
        )
    }
}
