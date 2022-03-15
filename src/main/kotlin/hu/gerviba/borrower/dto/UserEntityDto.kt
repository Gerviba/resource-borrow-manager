package hu.gerviba.borrower.dto

import java.io.Serializable

data class UserEntityDto(
    val id: Int = 0,
    val name: String = "",
    val permissions: MutableList<String> = mutableListOf(),
    val groupIds: MutableList<Int> = mutableListOf(),
    val divisionIds: MutableList<Int> = mutableListOf(),
) : Serializable
