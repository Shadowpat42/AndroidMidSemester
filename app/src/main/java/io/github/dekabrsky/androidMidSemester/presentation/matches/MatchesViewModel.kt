
package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MatchesUiState {

    data object Loading : MatchesUiState

    data class Content(
        val date: LocalDate,
        val matches: List<Match>
    ) : MatchesUiState

    data class Error(
        val message: String
    ) : MatchesUiState
}

sealed interface RussianTeamsUiState {

    data object Idle : RussianTeamsUiState

    data object Loading : RussianTeamsUiState

    data class Content(
        val teams: List<Team>
    ) : RussianTeamsUiState

    data class Error(
        val message: String
    ) : RussianTeamsUiState
}

class MatchesViewModel(
    private val getMatches: GetMatchesUseCase,
    private val repository: MatchRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow<MatchesUiState>(
            MatchesUiState.Loading
        )

    val state: StateFlow<MatchesUiState> =
        _state.asStateFlow()

    private val _teamsState =
        MutableStateFlow<RussianTeamsUiState>(
            RussianTeamsUiState.Idle
        )

    val teamsState: StateFlow<RussianTeamsUiState> =
        _teamsState.asStateFlow()

    private val _selectedTeamIds =
        MutableStateFlow<Set<Long>>(emptySet())

    val selectedTeamIds: StateFlow<Set<Long>> =
        _selectedTeamIds.asStateFlow()

    private var date: LocalDate = LocalDate.now()

    private var allMatches: List<Match> = emptyList()

    private var loadJob: Job? = null

    init {
        load(date)
    }

    fun previousDay() {
        load(date.minusDays(1))
    }

    fun nextDay() {
        load(date.plusDays(1))
    }

    fun loadRussianTeams() {

        if (
            _teamsState.value is RussianTeamsUiState.Loading ||
            _teamsState.value is RussianTeamsUiState.Content
        ) {
            return
        }

        _teamsState.value = RussianTeamsUiState.Loading

        viewModelScope.launch {

            runCatching {
                repository.russianTeams()
            }.onSuccess {

                _teamsState.value =
                    RussianTeamsUiState.Content(it)

            }.onFailure {

                _teamsState.value =
                    RussianTeamsUiState.Error(
                        it.message ?: "Не удалось загрузить команды"
                    )
            }
        }
    }

    fun toggleTeam(teamId: Long) {

        val updated =
            _selectedTeamIds.value.toMutableSet()

        if (!updated.add(teamId)) {
            updated.remove(teamId)
        }

        _selectedTeamIds.value = updated

        updateVisibleMatches()
    }

    fun clearSelection() {

        _selectedTeamIds.value = emptySet()

        updateVisibleMatches()
    }

    private fun updateVisibleMatches() {

        if (_state.value is MatchesUiState.Content) {

            _state.value = MatchesUiState.Content(
                date = date,
                matches = filteredMatches()
            )
        }
    }

    private fun filteredMatches(): List<Match> {

        val selected = _selectedTeamIds.value

        return if (selected.isEmpty()) {

            allMatches

        } else {

            allMatches.filter { match ->

                match.home.id in selected ||
                        match.away.id in selected
            }
        }
    }

    private fun load(newDate: LocalDate) {

        date = newDate

        loadJob?.cancel()

        _state.value = MatchesUiState.Loading

        loadJob = viewModelScope.launch {

            runCatching {

                getMatches(newDate)

            }.onSuccess {

                allMatches = it

                _state.value = MatchesUiState.Content(
                    date = newDate,
                    matches = filteredMatches()
                )

            }.onFailure {

                _state.value = MatchesUiState.Error(
                    it.message ?: "Не удалось загрузить матчи"
                )
            }
        }
    }
}
