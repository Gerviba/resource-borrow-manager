package hu.gerviba.borrower.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ProfileResponse(
    @param:JsonProperty("internal_id")
    var internalId: String = "",

    var displayName: String? = null,

    @param:JsonProperty("sn")
    var surname: String? = null,

    var givenName: String? = null,

    @param:JsonProperty("mail")
    var email: String? = null,

    @param:JsonProperty("niifPersonOrgID")
    var neptun: String? = null,

    var linkedAccounts: Map<String, String> = mapOf(),

    var lastSync: Map<String, Long> = mapOf(),

    var eduPersonEntitlement: List<PersonEntitlement> = listOf(),

    var roomNumber: String? = null,

    var mobile: String? = null,

    @param:JsonProperty("niifEduPersonAttendedCourse")
    var courses: List<String>? = null,

    var entrants: List<Entrant>? = listOf(),

    var admembership: List<String> = listOf(),

    var bmeunitscope: List<BMEUnitScope> = listOf(),
)
