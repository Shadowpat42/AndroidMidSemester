
package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val api: SStatsApi,
    private val cache: MatchesMemoryCache
) : MatchRepository {

    override suspend fun matchesOn(
        date: LocalDate
    ): List<Match> =
        cache.getOrLoad(date) {

            Log.d(
                "MatchesApi",
                "GET https://api.sstats.net/Games/list?Date=$date"
            )

            api.gamesOn(date.toString())
                .data
                .map { it.toMatch() }
        }

    override suspend fun match(
        id: Long
    ): Match {

        Log.d(
            "MatchesApi",
            "GET https://api.sstats.net/Games/$id"
        )

        return api.game(id).data?.toMatch()
            ?: throw NoSuchElementException(
                "Матч $id не найден"
            )
    }

    override suspend fun russianTeams(): List<Team> {

        val result = linkedMapOf<Long, Team>()
        val pageSize = 100
        var offset = 0
        var pages = 0

        val seenPageIds = mutableSetOf<Long>()

        while (pages < 30) {

            Log.d(
                "TeamsApi",
                "GET https://api.sstats.net/Teams/list?Country=Russia&offset=$offset&limit=$pageSize"
            )

            val response = api.teamsOn(
                country = "Russia",
                offset = offset,
                limit = pageSize
            )

            if (response.status != "OK") {
                throw IllegalStateException(
                    "SStats Teams API: ${response.status}"
                )
            }

            val newIds = response.data.count {
                seenPageIds.add(it.id)
            }

            response.data
                .filter {
                    it.country?.name.equals(
                        "Russia",
                        ignoreCase = true
                    )
                }
                .forEach { dto ->
                    result[dto.id] = Team(
                        id = dto.id,
                        name = dto.name,
                        logoUrl = dto.logoUrl
                    )
                }

            pages++
            offset += response.data.size

            if (
                response.data.isEmpty() ||
                response.data.size < pageSize ||
                offset >= response.totalCount ||
                newIds == 0
            ) {
                break
            }
        }

        Log.d(
            "TeamsApi",
            "Россия: загружено ${result.size} команд"
        )

        return result.values.sortedBy {
            it.name.lowercase()
        }
    }
}
