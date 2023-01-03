package com.hogent.squads.model.domain

import java.time.LocalDate

data class Subscription (
    val id:Int,
    val validTill:LocalDate
)