
package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SStatsTeamsEnvelope(
    val status: String = "",
    val data: List<SStatsTeamDto> = emptyList(),
    @SerialName("TotalCount")
    val totalCount: Int = 0
)

@Serializable
data class SStatsTeamDto(
    val id: Long,
    val name: String,
    val logoUrl: String? = null,
    val country: SStatsCountryDto? = null
)

@Serializable
data class SStatsCountryDto(
    val code: String = "",
    val name: String = ""
)
