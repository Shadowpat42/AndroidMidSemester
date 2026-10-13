
package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.ErrorState
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.LoadingState
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TITLE_DATE_FORMAT =
    DateTimeFormatter.ofPattern(
        "d MMMM yyyy",
        Locale.forLanguageTag("ru")
    )

@Composable
fun MatchesScreen(
    onMatchClick: (Match) -> Unit,
    viewModel: MatchesViewModel = koinViewModel()
) {

    var showTeams by rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(showTeams) {
        showTeams = false
    }

    if (showTeams) {

        RussianTeamsScreen(
            viewModel = viewModel,
            onBack = {
                showTeams = false
            }
        )

        return
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    val selected by viewModel.selectedTeamIds
        .collectAsStateWithLifecycle()

    Scaffold(
        topBar = {

            MatchesTopBar(
                state = state,
                selectedCount = selected.size,
                onPreviousDay = viewModel::previousDay,
                onNextDay = viewModel::nextDay,
                onOpenTeams = {
                    showTeams = true
                    viewModel.loadRussianTeams()
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (val current = state) {

                MatchesUiState.Loading -> {
                    LoadingState()
                }

                is MatchesUiState.Error -> {
                    ErrorState(
                        message = current.message
                    )
                }

                is MatchesUiState.Content -> {

                    if (current.matches.isEmpty()) {

                        Text(
                            text = if (selected.isEmpty()) {
                                "На эту дату матчей нет"
                            } else {
                                "Нет матчей выбранных команд"
                            },
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )

                    } else {

                        LazyColumn {

                            items(
                                current.matches,
                                key = { it.id }
                            ) { match ->

                                MatchListItem(
                                    match = match,
                                    onClick = onMatchClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchesTopBar(
    state: MatchesUiState,
    selectedCount: Int,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onOpenTeams: () -> Unit
) {

    val date =
        (state as? MatchesUiState.Content)?.date

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                horizontal = 4.dp,
                vertical = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onPreviousDay
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_arrow_left
                ),
                contentDescription = "Предыдущий день"
            )
        }

        Text(
            text = date?.format(TITLE_DATE_FORMAT) ?: "",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        IconButton(
            onClick = onNextDay
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_arrow_right
                ),
                contentDescription = "Следующий день"
            )
        }

        TextButton(
            onClick = onOpenTeams
        ) {
            Text(
                if (selectedCount == 0) {
                    "Команды"
                } else {
                    "Команды ($selectedCount)"
                }
            )
        }
    }
}

@Composable
private fun RussianTeamsScreen(
    viewModel: MatchesViewModel,
    onBack: () -> Unit
) {

    val state by viewModel.teamsState
        .collectAsStateWithLifecycle()

    val selected by viewModel.selectedTeamIds
        .collectAsStateWithLifecycle()

    var query by rememberSaveable {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {

            Column(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.statusBars
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    TextButton(
                        onClick = onBack
                    ) {
                        Text("Назад")
                    }

                    Text(
                        text = "Команды России",
                        style = MaterialTheme.typography.titleMedium
                    )

                    TextButton(
                        onClick = onBack
                    ) {
                        Text("Готово")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Выбрано: ${selected.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    TextButton(
                        onClick = viewModel::clearSelection
                    ) {
                        Text("Сбросить")
                    }
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    label = {
                        Text("Поиск команды")
                    },
                    singleLine = true
                )

                Text(
                    text = "Без выбранных команд показываются все матчи",
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 4.dp
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (val current = state) {

                RussianTeamsUiState.Idle,
                RussianTeamsUiState.Loading -> {
                    LoadingState()
                }

                is RussianTeamsUiState.Error -> {

                    Column(
                        modifier = Modifier.align(
                            Alignment.Center
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Ошибка: ${current.message}"
                        )

                        TextButton(
                            onClick = viewModel::loadRussianTeams
                        ) {
                            Text("Повторить")
                        }
                    }
                }

                is RussianTeamsUiState.Content -> {

                    val teams = current.teams.filter {

                        it.name.contains(
                            query,
                            ignoreCase = true
                        )
                    }

                    if (teams.isEmpty()) {

                        Text(
                            text = "Команды не найдены",
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )

                    } else {

                        LazyColumn {

                            items(
                                teams,
                                key = { it.id }
                            ) { team ->

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.toggleTeam(
                                                team.id
                                            )
                                        }
                                        .padding(
                                            horizontal = 12.dp,
                                            vertical = 8.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(
                                        12.dp
                                    )
                                ) {

                                    Checkbox(
                                        checked = team.id in selected,
                                        onCheckedChange = {
                                            viewModel.toggleTeam(
                                                team.id
                                            )
                                        }
                                    )

                                    AsyncImage(
                                        model = team.logoUrl,
                                        contentDescription = null,
                                        modifier = Modifier.size(
                                            36.dp
                                        ),
                                        placeholder = painterResource(
                                            R.drawable.ic_team_placeholder
                                        ),
                                        error = painterResource(
                                            R.drawable.ic_team_placeholder
                                        )
                                    )

                                    Text(
                                        text = team.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
