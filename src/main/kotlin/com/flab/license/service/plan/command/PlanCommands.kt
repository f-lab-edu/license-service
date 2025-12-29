package com.flab.license.service.plan.command

import com.flab.license.domain.plan.PlanCode
import com.flab.license.domain.plan.PlanId

data class CreatePlanCommand(
    val planCode: PlanCode,
    val maxSeats: Int,
    val monthlyTokenLimit: Long
)

data class UpdatePlanCommand(
    val planId: PlanId,
    val maxSeats: Int,
    val monthlyTokenLimit: Long
)
