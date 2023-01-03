package com.hogent.squads.model.network.rest.resources

data class ExtendSubscriptionRequest(
    val subscription:ExtendSubscriptionDTO
)

data class ExtendSubscriptionDTO(
    val customerId: Int
)