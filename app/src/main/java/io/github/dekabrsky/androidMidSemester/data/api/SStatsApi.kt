
package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SStatsApi : EspnApi {

    @GET("Games/list?offset=0&limit=10")
    suspend fun gamesOn(
        @Query("Date") date: String
    ): ListEnvelope<GameDto>

    @GET("Games/{id}")
    suspend fun game(
        @Path("id") id: Long
    ): SingleEnvelope<GameDto>
}
