package com.vbwd.plugin.tarot.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.vbwd.core.networking.ApiError
import com.vbwd.plugin.tarot.domain.DailyLimits
import com.vbwd.plugin.tarot.domain.TaroSession
import com.vbwd.plugin.tarot.domain.TarotService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val PADDING = 16.dp

/** Tarot reading screen logic. Port of the iOS `TarotViewModel`. */
class TarotViewModel(private val service: TarotService) {
    data class UiState(
        val isLoading: Boolean = false,
        val limits: DailyLimits? = null,
        val session: TaroSession? = null,
        val interpretation: String? = null,
        val errorMessage: String? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    suspend fun load() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        try {
            _uiState.value = _uiState.value.copy(isLoading = false, limits = service.fetchDailyLimits())
        } catch (error: ApiError) {
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
        }
    }

    suspend fun createSession() {
        try {
            _uiState.value = _uiState.value.copy(session = service.createSession(), interpretation = null)
        } catch (error: ApiError) {
            _uiState.value = _uiState.value.copy(errorMessage = error.message)
        }
    }

    suspend fun submitSituation(text: String) {
        val sessionId = _uiState.value.session?.sessionId ?: return
        try {
            _uiState.value =
                _uiState.value.copy(
                    interpretation = service.submitSituation(sessionId, text, "en"),
                )
        } catch (error: ApiError) {
            _uiState.value = _uiState.value.copy(errorMessage = error.message)
        }
    }
}

@Composable
fun TarotScreen(viewModel: TarotViewModel) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var situation by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(PADDING)
                .testTag("tarot_screen"),
        verticalArrangement = Arrangement.spacedBy(PADDING),
    ) {
        Text("Tarot Card Reading", style = MaterialTheme.typography.headlineSmall)
        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        state.limits?.let { Text("Sessions remaining: ${it.dailyRemaining} / ${it.dailyTotal}") }

        Button(
            onClick = { scope.launch { viewModel.createSession() } },
            enabled = state.limits?.canCreate != false,
            modifier = Modifier.fillMaxWidth().testTag("tarot_start"),
        ) {
            Text("Start Reading")
        }

        state.session?.let { session ->
            session.cards.forEach { card ->
                Text("${card.position}: ${card.arcana?.name ?: card.arcanaId} (${card.orientation})")
            }
            OutlinedTextField(
                value = situation,
                onValueChange = { situation = it },
                label = { Text("Your situation") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { scope.launch { viewModel.submitSituation(situation) } },
                modifier = Modifier.fillMaxWidth().testTag("tarot_submit"),
            ) {
                Text("Get interpretation")
            }
        }

        state.interpretation?.let { Text(it, modifier = Modifier.testTag("tarot_interpretation")) }
    }
}
