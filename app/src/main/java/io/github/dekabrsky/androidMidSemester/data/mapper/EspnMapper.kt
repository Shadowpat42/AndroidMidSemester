
package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnCompetitionDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.OffsetDateTime
import java.time.ZoneId

private fun EspnTeamDto.toTeam() = Team(
    id = id.toLong(),
    name = displayName,
    logoUrl = logo
)

fun EspnCompetitionDto.toMatch(
    eventId: String,
    eventDate: String,
    leagueName: String
): Match {

    val home = competitors.firstOrNull {
        it.homeAway == "home"
    } ?: throw IllegalArgumentException(
        "ESPN: missing home team for $eventId"
    )

    val away = competitors.firstOrNull {
        it.homeAway == "away"
    } ?: throw IllegalArgumentException(
        "ESPN: missing away team for $eventId"
    )

    val state = status?.type?.state

    val started = state == "in" ||
            state == "post" ||
            status?.type?.completed == true

    return Match(
        id = eventId.toLong(),

        home = home.team.toTeam(),
        away = away.team.toTeam(),

        homeScore = if (started) {
            home.score?.toIntOrNull()
        } else null,

        awayScore = if (started) {
            away.score?.toIntOrNull()
        } else null,

        status = if (status?.type?.completed == true) {
            MatchStatus.FINISHED
        } else {
            MatchStatus.SCHEDULED
        },

        kickoff = OffsetDateTime.parse(eventDate)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime(),

        league = leagueName,

        round = week?.number?.let {
            "Тур $it"
        } ?: "Не указан"
    )
}

fun EspnEventDto.toMatch(
    leagueName: String
): Match =
    competitions.firstOrNull()?.toMatch(
        id,
        date,
        leagueName
    ) ?: throw IllegalArgumentException(
        "ESPN: missing competition for $id"
    )
