package com.hogent.squads.model.network.rest.resources

data class EditUserRequest(
    val userId:Int,
    val user:EditUserDto
)

data class EditUserDto(
    val email:String,
    val firstName:String,
    val lastName:String,
    val address:String,
    val phoneNumber:String
)