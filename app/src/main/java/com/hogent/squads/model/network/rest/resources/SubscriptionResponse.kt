package com.hogent.squads.model.network.rest.resources

import com.hogent.squads.model.domain.Subscription
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.streams.toList

data class SubscriptionResponse (
    val subscriptions : List<SubscriptionDto>
) {
    fun toSubList() : List<Subscription> {
        return subscriptions.stream()
            .map {
                Subscription(it.id, LocalDateTime.parse(it.validTill).toLocalDate())
            }
            .toList()
    }
}

data class SubscriptionDetailResponse (
    val id: Int
        )

data class SubscriptionDto (
    val id:Int,
    val validTill:String
)