
package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.format.DateTimeFormatter

private val KICKOFF_FORMAT = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun MatchDetailsDialog(
    match: Match,
    onDismiss: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Детали матча",
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                TeamInfo(
                    team = match.home,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = if (
                        match.homeScore != null &&
                        match.awayScore != null
                    ) {
                        "${match.homeScore} : ${match.awayScore}"
                    } else {
                        "— : —"
                    },
                    style = MaterialTheme.typography.titleLarge
                )

                TeamInfo(
                    team = match.away,
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()

            DetailRow(
                label = "Время начала",
                value = match.kickoff.format(KICKOFF_FORMAT)
            )

            DetailRow(
                label = "Лига",
                value = match.league
            )

            DetailRow(
                label = "Тур",
                value = match.round
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Закрыть")
                }
            }
        }
    }
}

@Composable
private fun TeamInfo(
    team: Team,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        AsyncImage(
            model = team.logoUrl,
            contentDescription = "Логотип ${team.name}",
            modifier = Modifier.size(56.dp),
            placeholder = painterResource(
                R.drawable.ic_team_placeholder
            ),
            error = painterResource(
                R.drawable.ic_team_placeholder
            )
        )

        Text(
            text = team.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
