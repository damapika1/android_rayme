package com.hogent.squads.model.network.rest.resources

import com.hogent.squads.model.domain.Turncard
import java.time.LocalDateTime
import kotlin.streams.toList

data class TurncardResponse(
    val turnCards:List<TurncardDto>
) {
    fun toTurncardList() : List<Turncard> {
        return turnCards.stream()
            .map {
                Turncard(it.id, LocalDateTime.parse(it.validTill).toLocalDate(), it.numberOfTurns)
            }
            .toList()
    }
}

data class TurncardDetailResponse(
    val turnCard:TurncardDto
)

data class TurncardDto (
    val id:Int,
    val validTill:String,
    val numberOfTurns:Int
        )