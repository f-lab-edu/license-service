package com.flab.license.api.plan.dto

import com.flab.license.domain.plan.PlanId
import com.flab.license.service.plan.command.UpdatePlanCommand
import jakarta.validation.constraints.Min

data class UpdatePlanRequest(
    @field:Min(value = 1, message = "최대 좌석 수는 1 이상이어야 합니다")
    val maxSeats: Int,

    @field:Min(value = 1, message = "월별 토큰 한도는 1 이상이어야 합니다")
    val monthlyTokenLimit: Long
) {
    fun toCommand(planId: PlanId): UpdatePlanCommand =
        UpdatePlanCommand(
            planId = planId,
            maxSeats = maxSeats,
            monthlyTokenLimit = monthlyTokenLimit
        )
}
