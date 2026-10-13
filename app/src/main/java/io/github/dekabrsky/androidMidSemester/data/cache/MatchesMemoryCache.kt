
package io.github.dekabrsky.androidMidSemester.data.cache

import android.util.Log
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import kotlinx.coroutines.sync.Mutex
import java.time.LocalDate

class MatchesMemoryCache {

    private val entries =
        mutableMapOf<LocalDate, List<Match>>()

    private val mutex = Mutex()

    suspend fun getOrLoad(
        date: LocalDate,
        loader: suspend () -> List<Match>
    ): List<Match> {
        mutex.lock()

        try {
            entries[date]?.let { matches ->
                Log.d(
                    "MatchesCache",
                    "CACHE HIT: $date (без запроса)"
                )
                return matches
            }

            Log.d(
                "MatchesCache",
                "CACHE MISS: $date -> запрос к API"
            )

            return loader().also { matches ->
                entries[date] = matches
            }
        } finally {
            mutex.unlock()
        }
    }
}
