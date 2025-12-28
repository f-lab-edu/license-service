package com.flab.license.domain.plan

import com.flab.license.domain.plan.exception.PlanErrorCode
import com.flab.license.domain.plan.exception.PlanException
import java.util.UUID

@JvmInline
value class PlanId(val value: UUID) {
    companion object {
        fun generate(): PlanId = PlanId(UUID.randomUUID())

        fun from(value: String): PlanId = runCatching {
            PlanId(UUID.fromString(value))
        }.getOrElse {
            throw PlanException(PlanErrorCode.INVALID_PLAN_ID)
        }
    }
}
