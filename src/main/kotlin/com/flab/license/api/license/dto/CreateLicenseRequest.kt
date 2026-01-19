package com.flab.license.api.license.dto

import com.flab.license.common.validation.EnumValue
import com.flab.license.domain.license.Owner
import com.flab.license.domain.license.Period
import com.flab.license.domain.plan.PlanId
import com.flab.license.service.license.command.CreateLicenseCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.util.UUID

data class CreateLicenseRequest(
    @field:NotNull(message = "플랜 ID는 필수입니다")
    val planId: UUID,

    @field:EnumValue(enumClass = Owner.Type::class, ignoreCase = true, message = "유효한 소유자 타입이 아닙니다")
    val ownerType: String,

    @field:NotBlank(message = "소유자 ID는 필수입니다")
    val ownerId: String,

    @field:NotNull(message = "시작일은 필수입니다")
    val startDate: LocalDate,

    @field:NotNull(message = "종료일은 필수입니다")
    val endDate: LocalDate
) {
    fun toCommand(): CreateLicenseCommand {
        val ownerTypeEnum = Owner.Type.valueOf(ownerType.uppercase())
        val owner = when (ownerTypeEnum) {
            Owner.Type.USER -> Owner.user(ownerId)
            Owner.Type.ORGANIZATION -> Owner.organization(ownerId)
        }
        return CreateLicenseCommand(
            planId = PlanId(planId),
            owner = owner,
            period = Period.of(startDate, endDate)
        )
    }
}
