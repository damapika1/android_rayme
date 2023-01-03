package com.hogent.squads.model.network.rest.resources

data class IncreaseTurnsRequest(
    val turnCard:IncreaseTurnsDTO
)

data class IncreaseTurnsDTO(
    val customerId:Int
)