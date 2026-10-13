
package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MatchRepositoryImpl(
    private val api: SStatsApi,
    private val cache: MatchesMemoryCache
) : MatchRepository {

    override suspend fun matchesOn(
        date: LocalDate
    ): List<Match> =
        cache.getOrLoad(date) {

            val queryDate = date.format(
                DateTimeFormatter.BASIC_ISO_DATE
            )

            Log.d(
                "MatchesApi",
                "GET https://site.api.espn.com/apis/site/v2/sports/soccer/rus.1/scoreboard?dates=$queryDate"
            )

            val response = api.scoreboard(queryDate)

            val leagueName =
                response.leagues.firstOrNull()?.name
                    ?: "Russian Premier League"

            response.events.map {
                it.toMatch(leagueName)
            }
        }

    override suspend fun match(
        id: Long
    ): Match {

        Log.d(
            "MatchesApi",
            "GET https://site.api.espn.com/apis/site/v2/sports/soccer/rus.1/summary?event=$id"
        )

        val header = api.summary(id).header
            ?: throw NoSuchElementException(
                "ESPN: no summary for match $id"
            )

        val competition = header.competitions.firstOrNull()
            ?: throw NoSuchElementException(
                "ESPN: no competition for match $id"
            )

        val kickoff = competition.date
            ?: throw NoSuchElementException(
                "ESPN: no date for match $id"
            )

        return competition.toMatch(
            eventId = id.toString(),
            eventDate = kickoff,
            leagueName = header.league?.name
                ?: "Russian Premier League"
        )
    }
}
