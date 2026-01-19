package com.flab.license.service.license.command

import com.flab.license.domain.license.Owner
import com.flab.license.domain.license.Period
import com.flab.license.domain.plan.PlanId

data class CreateLicenseCommand(
    val planId: PlanId,
    val owner: Owner,
    val period: Period
)
