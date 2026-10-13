
package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SStatsTeamsEnvelope
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SStatsApi : EspnApi {

    @GET("Games/list")
    suspend fun gamesOn(
        @Query("Date") date: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 100
    ): ListEnvelope<GameDto>

    @GET("Games/{id}")
    suspend fun game(
        @Path("id") id: Long
    ): SingleEnvelope<GameDto>

    @GET("Teams/list")
    suspend fun teamsOn(
        @Query("Country") country: String = "Russia",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 100
    ): SStatsTeamsEnvelope
}
