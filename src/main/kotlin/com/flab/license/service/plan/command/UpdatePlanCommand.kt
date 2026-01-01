package com.flab.license.service.plan.command

import com.flab.license.domain.plan.PlanId

data class UpdatePlanCommand(
    val planId: PlanId,
    val maxSeats: Int,
    val monthlyTokenLimit: Long
)
