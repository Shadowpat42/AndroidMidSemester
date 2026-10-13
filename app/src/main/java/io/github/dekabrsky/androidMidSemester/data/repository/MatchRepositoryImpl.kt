
package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
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
            api.gamesOn(date.toString())
                .data
                .map { it.toMatch() }
        }

    override suspend fun match(
        id: Long
    ): Match =
        api.game(id).data?.toMatch()
            ?: throw NoSuchElementException(
                "Матч $id не найден"
            )
}
