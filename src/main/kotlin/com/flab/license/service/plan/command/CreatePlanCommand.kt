package com.flab.license.service.plan.command

import com.flab.license.domain.plan.PlanCode

data class CreatePlanCommand(
    val planCode: PlanCode,
    val maxSeats: Int,
    val monthlyTokenLimit: Long
)
