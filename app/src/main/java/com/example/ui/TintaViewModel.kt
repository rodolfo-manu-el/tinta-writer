package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.TintaRepository
import com.example.data.model.Chapter
import com.example.data.model.CharacterEntity
import com.example.data.model.CodexEntry
import com.example.data.model.MarginNote
import com.example.data.model.Novel
import com.example.data.model.Relationship
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TintaTab(val label: String, val iconName: String) {
    BIBLIOTECA("Biblioteca", "local_library"),
    OBRA("Obra", "view_timeline"),
    ESCRIBIR("Escribir", "history_edu"),
    PERSONAJES("Personajes", "group"),
    RECURSOS("Recursos", "auto_awesome")
}

class TintaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TintaRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = TintaRepository(db)
    }

    val allNovels: StateFlow<List<Novel>> = repository.allNovels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredNovel: StateFlow<Novel?> = repository.featuredNovel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allCharacters: StateFlow<List<CharacterEntity>> = repository.allCharacters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCodexEntries: StateFlow<List<CodexEntry>> = repository.allCodexEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(TintaTab.BIBLIOTECA)
    val currentTab: StateFlow<TintaTab> = _currentTab.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenreFilter = MutableStateFlow("Todos")
    val selectedGenreFilter: StateFlow<String> = _selectedGenreFilter.asStateFlow()

    private val _activeNovelId = MutableStateFlow("")
    val activeNovelId: StateFlow<String> = _activeNovelId.asStateFlow()

    private val _activeChapterId = MutableStateFlow("")
    val activeChapterId: StateFlow<String> = _activeChapterId.asStateFlow()

    // Active Chapter
    private val _chapterContent = MutableStateFlow("")
    val chapterContent: StateFlow<String> = _chapterContent.asStateFlow()

    private val _chapterWordCount = MutableStateFlow(0)
    val chapterWordCount: StateFlow<Int> = _chapterWordCount.asStateFlow()

    private val _isChapterFinished = MutableStateFlow(false)
    val isChapterFinished: StateFlow<Boolean> = _isChapterFinished.asStateFlow()

    private val _paperTexture = MutableStateFlow("plain")
    val paperTexture: StateFlow<String> = _paperTexture.asStateFlow()

    private val _selectedFont = MutableStateFlow("Newsreader")
    val selectedFont: StateFlow<String> = _selectedFont.asStateFlow()

    private val _isZenMode = MutableStateFlow(false)
    val isZenMode: StateFlow<Boolean> = _isZenMode.asStateFlow()

    private val _personajesTab = MutableStateFlow("fichas") // "fichas" or "mapa"
    val personajesTab: StateFlow<String> = _personajesTab.asStateFlow()

    private val _characterFactionFilter = MutableStateFlow<String?>(null)
    val characterFactionFilter: StateFlow<String?> = _characterFactionFilter.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _isSynopsisExpanded = MutableStateFlow(false)
    val isSynopsisExpanded: StateFlow<Boolean> = _isSynopsisExpanded.asStateFlow()

    private val _showCreateModal = MutableStateFlow(false)
    val showCreateModal: StateFlow<Boolean> = _showCreateModal.asStateFlow()

    private val _createType = MutableStateFlow("novel") // "novel", "chapter", "character", "note"
    val createType: StateFlow<String> = _createType.asStateFlow()

    private val _showBackupDialog = MutableStateFlow(false)
    val showBackupDialog: StateFlow<Boolean> = _showBackupDialog.asStateFlow()

    private val _showImportDialog = MutableStateFlow(false)
    val showImportDialog: StateFlow<Boolean> = _showImportDialog.asStateFlow()

    private val _backupJsonString = MutableStateFlow<String?>(null)
    val backupJsonString: StateFlow<String?> = _backupJsonString.asStateFlow()

    // Notes for active chapter
    val activeNotes: StateFlow<List<MarginNote>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeRelationships: StateFlow<List<Relationship>> = repository.allNovels
        .combine(_activeNovelId) { novels, novelId ->
            novelId
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
        .let {
            repository.getRelationshipsForNovel("edbti9f")
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        }

    init {
        viewModelScope.launch {
            allNovels.collect { novels ->
                if (_activeNovelId.value.isEmpty() && novels.isNotEmpty()) {
                    _activeNovelId.value = novels.first().id
                }
            }
        }
    }

    fun selectTab(tab: TintaTab) {
        _currentTab.value = tab
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
        showToast(if (_isDarkTheme.value) "Modo Noche Activado" else "Modo Pergamino Claro Activado")
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setGenreFilter(genre: String) {
        _selectedGenreFilter.value = genre
    }

    fun setActiveNovel(novelId: String) {
        _activeNovelId.value = novelId
        _currentTab.value = TintaTab.OBRA
    }

    fun openWritingStudio(chapterId: String = "") {
        _currentTab.value = TintaTab.ESCRIBIR
        viewModelScope.launch {
            val targetChapter = if (chapterId.isNotBlank()) {
                repository.getChapterById(chapterId).firstOrNull()
            } else {
                repository.getChaptersForNovel(_activeNovelId.value).firstOrNull()?.firstOrNull()
            }
            targetChapter?.let {
                _activeChapterId.value = it.id
                _chapterContent.value = it.content
                _chapterWordCount.value = it.wordCount
                _isChapterFinished.value = it.isFinished
            }
        }
    }

    fun updateChapterContent(newContent: String) {
        _chapterContent.value = newContent
        val words = newContent.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        _chapterWordCount.value = words
        viewModelScope.launch {
            repository.saveChapterContent(_activeChapterId.value, newContent, words, _activeNovelId.value)
        }
    }

    fun toggleChapterFinished() {
        val newState = !_isChapterFinished.value
        _isChapterFinished.value = newState
        viewModelScope.launch {
            repository.toggleChapterStatus(_activeChapterId.value, newState)
            showToast(if (newState) "✓ Capítulo marcado como Terminado" else "Borrador Vivo reanudado")
        }
    }

    fun setPaperTexture(texture: String) {
        _paperTexture.value = texture
    }

    fun toggleFont() {
        _selectedFont.value = if (_selectedFont.value == "Newsreader") "Jakarta Sans" else "Newsreader"
    }

    fun toggleZenMode() {
        _isZenMode.value = !_isZenMode.value
        if (_isZenMode.value) {
            showToast("Modo Inmersivo Zen Activado")
        }
    }

    fun setPersonajesTab(tab: String) {
        _personajesTab.value = tab
    }

    fun setFactionFilter(faction: String?) {
        _characterFactionFilter.value = faction
    }

    fun toggleSynopsis() {
        _isSynopsisExpanded.value = !_isSynopsisExpanded.value
    }

    fun addMarginNote(category: String, colorTag: String, content: String, scopeTag: String) {
        viewModelScope.launch {
            val note = MarginNote(
                novelId = _activeNovelId.value,
                chapterId = _activeChapterId.value,
                category = category,
                colorTag = colorTag,
                content = content,
                scopeTag = scopeTag,
                timeLabel = "Ahora mismo"
            )
            repository.addMarginNote(note)
            showToast("Nota al margen fijada")
        }
    }

    fun deleteMarginNote(noteId: Long) {
        viewModelScope.launch {
            repository.deleteMarginNote(noteId)
            showToast("Nota eliminada del margen")
        }
    }

    fun openCreateModal(type: String = "novel") {
        _createType.value = type
        _showCreateModal.value = true
    }

    fun closeCreateModal() {
        _showCreateModal.value = false
    }

    fun createNovel(title: String, genre: String, targetWords: Int, synopsis: String) {
        viewModelScope.launch {
            val id = title.lowercase(Locale.ROOT).replace("[^a-z0-9]".toRegex(), "-")
            val novel = Novel(
                id = id,
                title = title,
                subtitle = "Manuscrito original",
                genre = genre,
                tropes = "✨ Nuevo manuscrito",
                wordCount = 0,
                targetWords = targetWords,
                completedChapters = 0,
                totalChapters = 10,
                progressPercent = 0,
                coverDrawableName = "cover_silencio",
                status = "Borrador inicial",
                lastEdited = "Ahora",
                isFeatured = false,
                synopsis = synopsis
            )
            repository.insertNovel(novel)
            _showCreateModal.value = false
            showToast("Novela '$title' creada en biblioteca")
        }
    }

    fun createCharacter(name: String, role: String, species: String, bio: String, ability: String) {
        viewModelScope.launch {
            val id = name.lowercase(Locale.ROOT).replace("[^a-z0-9]".toRegex(), "-")
            val character = CharacterEntity(
                id = id,
                novelId = _activeNovelId.value,
                name = name,
                roleTag = role,
                epithet = "Ficha de personaje",
                bio = bio,
                age = "Indefinida",
                species = species,
                plotRoleOrMotivation = "En desarrollo",
                traits = "Nuevo personaje,Intriga",
                specialAbility = ability,
                crucialLink = "Pendiente de vincular",
                faction = "Gremio",
                avatarDrawableName = "avatar_kaelen"
            )
            repository.insertCharacter(character)
            _showCreateModal.value = false
            showToast("Personaje '$name' añadido al elenco")
        }
    }

    fun createBackup() {
        viewModelScope.launch {
            val novels = allNovels.value
            val characters = allCharacters.value
            val json = repository.generateBackupJson(novels, characters)
            _backupJsonString.value = json
            _showBackupDialog.value = true
            showToast("Bóveda: Copia generada exitosamente")
        }
    }

    fun closeBackupDialog() {
        _showBackupDialog.value = false
    }

    fun openImportDialog() {
        _showImportDialog.value = true
    }

    fun closeImportDialog() {
        _showImportDialog.value = false
    }

    fun importJson(jsonString: String) {
        viewModelScope.launch {
            val success = repository.importJsonData(jsonString)
            if (success) {
                showToast("¡Biblioteca importada con éxito!")
                _showImportDialog.value = false
            } else {
                showToast("El archivo no tiene el formato válido de Tinta & Trama")
            }
        }
    }

    fun loadSummerLoveStory() {
        viewModelScope.launch {
            try {
                val inputStream = getApplication<Application>().assets.open("summer_love.json")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val success = repository.importJsonData(jsonString)
                if (success) {
                    showToast("¡Historia 'Amor de verano' cargada con éxito!")
                    _showImportDialog.value = false
                } else {
                    showToast("No se pudo cargar la historia")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    fun clearAllStories() {
        viewModelScope.launch {
            repository.clearAllData()
            _activeNovelId.value = ""
            _activeChapterId.value = ""
            _chapterContent.value = ""
            _chapterWordCount.value = 0
            showToast("Biblioteca vaciada. Cero historias.")
        }
    }

    fun importBackupMock() {
        openImportDialog()
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun dismissToast() {
        _toastMessage.value = null
    }
}
