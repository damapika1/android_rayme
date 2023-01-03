package com.hogent.squads.model.domain

import java.time.LocalDate

data class Turncard(
    val id:Int,
    val validTill: LocalDate,
    val numberOfTurns: Int
)