package com.flab.license.api.license.dto

import com.flab.license.common.validation.EnumValue
import com.flab.license.domain.license.Owner
import jakarta.validation.constraints.NotBlank

data class GetLicensesByOwnerQuery(
    @field:EnumValue(enumClass = Owner.Type::class, ignoreCase = true, message = "유효한 소유자 타입이 아닙니다")
    val ownerType: String,

    @field:NotBlank(message = "소유자 ID는 필수입니다")
    val ownerId: String
) {
    fun toOwner(): Owner {
        val type = Owner.Type.valueOf(ownerType.uppercase())
        return when (type) {
            Owner.Type.USER -> Owner.user(ownerId)
            Owner.Type.ORGANIZATION -> Owner.organization(ownerId)
        }
    }
}
