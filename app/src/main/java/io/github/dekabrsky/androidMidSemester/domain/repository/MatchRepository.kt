
package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.LocalDate

interface MatchRepository {

    suspend fun matchesOn(
        date: LocalDate
    ): List<Match>

    suspend fun match(
        id: Long
    ): Match

    suspend fun russianTeams(): List<Team>
}
