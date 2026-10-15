package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChapterVersion
import com.example.data.model.MarginNote
import com.example.ui.TintaTab
import com.example.ui.TintaViewModel
import com.example.ui.theme.NewsreaderFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily

@Composable
fun EscribirScreen(
    viewModel: TintaViewModel,
    modifier: Modifier = Modifier
) {
    val selectedNovelId by viewModel.selectedNovelId.collectAsStateWithLifecycle()
    val currentNovel by viewModel.currentNovel.collectAsStateWithLifecycle()
    val activeChapters by viewModel.activeChapters.collectAsStateWithLifecycle()
    val activeChapterId by viewModel.activeChapterId.collectAsStateWithLifecycle()
    val chapterContent by viewModel.chapterContent.collectAsStateWithLifecycle()
    val wordCount by viewModel.chapterWordCount.collectAsStateWithLifecycle()
    val isFinished by viewModel.isChapterFinished.collectAsStateWithLifecycle()
    val paperTexture by viewModel.paperTexture.collectAsStateWithLifecycle()
    val selectedFont by viewModel.selectedFont.collectAsStateWithLifecycle()
    val isZenMode by viewModel.isZenMode.collectAsStateWithLifecycle()
    val marginNotes by viewModel.activeNotes.collectAsStateWithLifecycle()
    val chapterVersions by viewModel.chapterVersions.collectAsStateWithLifecycle()

    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val timerSeconds by viewModel.timerSeconds.collectAsStateWithLifecycle()
    val wordsWrittenInSession by viewModel.wordsWrittenInSession.collectAsStateWithLifecycle()

    var tfv by remember(chapterContent) {
        mutableStateOf(TextFieldValue(text = chapterContent, selection = TextRange(chapterContent.length)))
    }

    var isChapterDropdownExpanded by remember { mutableStateOf(false) }
    var showVersionsDialog by remember { mutableStateOf(false) }

    val readingTimeMin = (wordCount / 250).coerceAtLeast(1)
    val currentChapter = activeChapters.find { it.id == activeChapterId }
    val currentChapterIndex = activeChapters.indexOfFirst { it.id == activeChapterId }
    val hasPreviousChapter = currentChapterIndex > 0
    val hasNextChapter = currentChapterIndex >= 0 && currentChapterIndex < activeChapters.size - 1

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (selectedNovelId.isNullOrBlank() || currentNovel == null) {
            // Empty State when no novel is selected
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("escribir_empty_state_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Draw,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "No hay ninguna historia seleccionada. Selecciona una historia en la Biblioteca para escribir en el editor.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.selectTab(TintaTab.BIBLIOTECA) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("empty_state_go_to_biblioteca_button")
                    ) {
                        Text("Ir a la Biblioteca")
                    }
                }
            }
        } else if (activeChapters.isEmpty()) {
            // Empty chapters state for active novel
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("no_chapters_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "La historia '${currentNovel?.title}' aún no tiene ningún capítulo.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.createChapter() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("create_first_chapter_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Crear Primer Capítulo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Chapter Switcher & Telemetry Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = {
                                    if (hasPreviousChapter) {
                                        viewModel.selectChapter(activeChapters[currentChapterIndex - 1].id)
                                    }
                                },
                                enabled = hasPreviousChapter,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (hasPreviousChapter) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLow)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                    contentDescription = "Capítulo anterior",
                                    tint = if (hasPreviousChapter) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Interactive Dropdown Chapter Selector Trigger
                            Box(modifier = Modifier.weight(1f)) {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { isChapterDropdownExpanded = true }
                                        .padding(vertical = 4.dp, horizontal = 6.dp)
                                        .testTag("chapter_selector_dropdown_trigger")
                                ) {
                                    Text(
                                        text = currentNovel?.title?.uppercase() ?: "MANUSCRITO EN CURSO",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = currentChapter?.let { "Cap. ${it.number}: ${it.title}" } ?: "Seleccionar Capítulo",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Desplegar capítulos",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Chapter Selector Dropdown Menu
                                DropdownMenu(
                                    expanded = isChapterDropdownExpanded,
                                    onDismissRequest = { isChapterDropdownExpanded = false }
                                ) {
                                    activeChapters.forEach { chapter ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Cap. ${chapter.number}: ${chapter.title}",
                                                        fontWeight = if (chapter.id == activeChapterId) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (chapter.id == activeChapterId) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    if (chapter.isFinished) {
                                                        Text(
                                                            text = " ✓",
                                                            color = MaterialTheme.colorScheme.secondary,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                viewModel.selectChapter(chapter.id)
                                                isChapterDropdownExpanded = false
                                            },
                                            modifier = Modifier.testTag("chapter_item_${chapter.id}")
                                        )
                                    }

                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = "Nuevo Capítulo",
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        },
                                        onClick = {
                                            isChapterDropdownExpanded = false
                                            viewModel.createChapter()
                                        },
                                        modifier = Modifier.testTag("create_new_chapter_option")
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                if (hasNextChapter) {
                                    viewModel.selectChapter(activeChapters[currentChapterIndex + 1].id)
                                }
                            },
                            enabled = hasNextChapter,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (hasNextChapter) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Capítulo siguiente",
                                tint = if (hasNextChapter) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Sub-bar status and zen toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isFinished) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { viewModel.toggleChapterFinished() }
                                    .testTag("status_toggle_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (isFinished) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isFinished) "✓ Terminado" else "Borrador Vivo",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isFinished) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = "Guardado local automático",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { viewModel.toggleZenMode() }
                                .testTag("zen_mode_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Inmersivo",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Floating Writer Toolbar
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Row 1: Paper textures & font family
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            TextureChip(
                                icon = "📄",
                                label = "Limpio",
                                isSelected = paperTexture == "plain",
                                onClick = { viewModel.setPaperTexture("plain") }
                            )
                            TextureChip(
                                icon = "📜",
                                label = "Pergamino",
                                isSelected = paperTexture == "parchment",
                                onClick = { viewModel.setPaperTexture("parchment") }
                            )
                            TextureChip(
                                icon = "📓",
                                label = "Rayado",
                                isSelected = paperTexture == "lined",
                                onClick = { viewModel.setPaperTexture("lined") }
                            )
                            TextureChip(
                                icon = "🔲",
                                label = "Cuadrícula",
                                isSelected = paperTexture == "grid",
                                onClick = { viewModel.setPaperTexture("grid") }
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { viewModel.toggleFont() }
                                .testTag("font_family_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatSize,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = selectedFont,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Row 2: Formatting shortcuts & History/Version control
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FormatButton(text = "B", isBold = true, onClick = {
                                viewModel.applyMarkdownFormat("bold", tfv.selection.start, tfv.selection.end)
                            })
                            FormatButton(text = "I", isItalic = true, onClick = {
                                viewModel.applyMarkdownFormat("italic", tfv.selection.start, tfv.selection.end)
                            })
                            FormatButton(text = "H", isBold = true, onClick = {
                                viewModel.applyMarkdownFormat("header", tfv.selection.start, tfv.selection.end)
                            })
                            FormatIconButton(icon = Icons.Default.FormatQuote, onClick = {
                                viewModel.applyMarkdownFormat("quote", tfv.selection.start, tfv.selection.end)
                            })
                            FormatIconButton(icon = Icons.Default.FormatListBulleted, onClick = {
                                viewModel.applyMarkdownFormat("list", tfv.selection.start, tfv.selection.end)
                            })

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.showToast("Chispa creativa: metáfora o sugerencia de ritmo aplicada") },
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AutoFixHigh,
                                        contentDescription = "Chispa",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Save version snapshot button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                modifier = Modifier
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.saveCurrentVersion("Snapshot manual") }
                                    .testTag("save_version_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = "Guardar versión",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Capturar",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // View history button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                modifier = Modifier
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showVersionsDialog = true }
                                    .testTag("history_versions_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "Historial",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Versiones (${chapterVersions.size})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Pomodoro Writing Timer & Telemetry Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().testTag("pomodoro_timer_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (isTimerRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        val minutes = timerSeconds / 60
                        val seconds = timerSeconds % 60
                        val timerFormatted = String.format("%02d:%02d", minutes, seconds)
                        Column {
                            Text(
                                text = "Sesión Pomodoro: $timerFormatted",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "+$wordsWrittenInSession palabras escritas en la sesión",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { viewModel.toggleTimer() },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isTimerRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer)
                                .testTag("toggle_pomodoro_button")
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isTimerRunning) "Pausar" else "Iniciar",
                                tint = if (isTimerRunning) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.resetTimer() },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                .testTag("reset_pomodoro_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reiniciar Pomodoro",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Manuscript Canvas (Editorial Live Writing Canvas)
            val canvasBg = when (paperTexture) {
                "parchment" -> Color(0xFFFBF0D9)
                "lined" -> Color(0xFFF1F5F9)
                "grid" -> Color(0xFFE2E8F0)
                else -> MaterialTheme.colorScheme.surfaceContainerLow
            }

            val lineOverlayColor = when (paperTexture) {
                "parchment" -> Color(0xFFD9C3A3)
                "lined" -> Color(0xFF94A3B8)
                "grid" -> Color(0xFFCBD5E1)
                else -> Color.Transparent
            }

            val textOnCanvasColor = when (paperTexture) {
                "parchment" -> Color(0xFF2C1D11)
                "lined" -> Color(0xFF0F172A)
                "grid" -> Color(0xFF0F172A)
                else -> MaterialTheme.colorScheme.onSurface
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = canvasBg,
                tonalElevation = 2.dp,
                border = if (isZenMode) BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else BorderStroke(1.dp, lineOverlayColor.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manuscript_canvas")
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Pattern overlay for Lined / Grid
                    if (paperTexture == "lined" || paperTexture == "grid") {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val strokeWidth = 1f
                            val step = 28.dp.toPx()
                            if (paperTexture == "lined" || paperTexture == "grid") {
                                var y = step
                                while (y < size.height) {
                                    drawLine(
                                        color = lineOverlayColor.copy(alpha = 0.35f),
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = strokeWidth
                                    )
                                    y += step
                                }
                            }
                            if (paperTexture == "grid") {
                                var x = step
                                while (x < size.width) {
                                    drawLine(
                                        color = lineOverlayColor.copy(alpha = 0.35f),
                                        start = Offset(x, 0f),
                                        end = Offset(x, size.height),
                                        strokeWidth = strokeWidth
                                    )
                                    x += step
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Header with icon & title
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✍️",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = currentChapter?.title?.uppercase() ?: "BORRADOR MAESTRO",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.4.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Interactive Text Editor (Persists directly to Chapter entity in Room)
                        val editorFontFamily = if (selectedFont == "Newsreader") NewsreaderFontFamily else PlusJakartaSansFontFamily
                        BasicTextField(
                            value = tfv,
                            onValueChange = { newTfv ->
                                tfv = newTfv
                                if (newTfv.text != chapterContent) {
                                    viewModel.updateChapterContent(newTfv.text)
                                }
                            },
                            textStyle = TextStyle(
                                fontFamily = editorFontFamily,
                                fontSize = 17.sp,
                                lineHeight = 28.sp,
                                color = textOnCanvasColor
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(340.dp)
                                .testTag("editable_manuscript_text")
                        )

                        // Footer Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Draw,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Tinta activa",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${chapterContent.length} caracteres",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Section Margin Notes
        SectionMarginNotes(
            notes = marginNotes,
            onAddPostIt = { viewModel.openCreateModal("note") },
            onDeleteNote = { noteId -> viewModel.deleteMarginNote(noteId) }
        )

        // Version History Dialog
        if (showVersionsDialog) {
            VersionHistoryDialog(
                versions = chapterVersions,
                onDismiss = { showVersionsDialog = false },
                onRestore = { version ->
                    viewModel.restoreVersion(version)
                    showVersionsDialog = false
                }
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun VersionHistoryDialog(
    versions: List<ChapterVersion>,
    onDismiss: () -> Unit,
    onRestore: (ChapterVersion) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(text = "Historial de Versiones")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (versions.isEmpty()) {
                    Text(
                        text = "Aún no hay versiones o borradores guardados para este capítulo. Presiona 'Capturar' en la barra para guardar una versión.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    versions.forEach { version ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⏱️ ${version.timeFormatted}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${version.wordCount} palabras",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = version.content.take(100) + if (version.content.length > 100) "..." else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2
                                )
                                OutlinedButton(
                                    onClick = { onRestore(version) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Restaurar este borrador")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun TextureChip(
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (isSelected) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = icon, fontSize = 11.sp)
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun FormatButton(
    text: String,
    isBold: Boolean = false,
    isItalic: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun FormatIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun SectionMarginNotes(
    notes: List<MarginNote>,
    onAddPostIt: () -> Unit,
    onDeleteNote: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Notas al Margen",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Text(
                        text = "${notes.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Button(
                onClick = onAddPostIt,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("add_postit_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nuevo Post-it", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }

        notes.forEachIndexed { index, note ->
            val rotation = when (index % 3) {
                0 -> -0.8f
                1 -> 0.8f
                else -> -0.4f
            }
            val cardBg = when (note.colorTag) {
                "amber" -> MaterialTheme.colorScheme.secondaryContainer
                "lavender" -> MaterialTheme.colorScheme.surfaceContainerHighest
                else -> MaterialTheme.colorScheme.surfaceContainerHigh
            }
            val titleColor = when (note.colorTag) {
                "amber" -> MaterialTheme.colorScheme.secondary
                "lavender" -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.secondary
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .rotate(rotation)
                    .testTag("postit_card_${note.id}")
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Tactile Pin Head
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )

                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = note.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = titleColor,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            IconButton(
                                onClick = { onDeleteNote(note.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar nota",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = note.scopeTag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = note.timeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
