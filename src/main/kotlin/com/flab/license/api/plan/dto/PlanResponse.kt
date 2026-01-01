package com.flab.license.api.plan.dto

import com.flab.license.domain.plan.Plan
import com.flab.license.domain.plan.PlanCode
import java.util.UUID

data class PlanResponse(
    val id: UUID,
    val planCode: PlanCode,
    val maxSeats: Int,
    val monthlyTokenLimit: Long
) {
    companion object {
        fun from(plan: Plan): PlanResponse {
            return PlanResponse(
                id = plan.id.value,
                planCode = plan.planCode,
                maxSeats = plan.maxSeats,
                monthlyTokenLimit = plan.monthlyTokenLimit
            )
        }
    }
}
