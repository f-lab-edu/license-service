package com.flab.license.api.plan.dto

import com.flab.license.common.validation.EnumValue
import com.flab.license.domain.plan.PlanCode
import com.flab.license.service.plan.command.CreatePlanCommand
import jakarta.validation.constraints.Min

data class CreatePlanRequest(
    @field:EnumValue(enumClass = PlanCode::class, ignoreCase = true, message = "유효한 플랜 코드가 아닙니다")
    val planCode: String,

    @field:Min(value = 1, message = "최대 좌석 수는 1 이상이어야 합니다")
    val maxSeats: Int,

    @field:Min(value = 1, message = "월별 토큰 한도는 1 이상이어야 합니다")
    val monthlyTokenLimit: Long
) {
    fun toCommand(): CreatePlanCommand =
        CreatePlanCommand(
            planCode = PlanCode.valueOf(planCode.uppercase()),
            maxSeats = maxSeats,
            monthlyTokenLimit = monthlyTokenLimit
        )
}
