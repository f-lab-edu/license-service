package com.flab.license.infra.persistence.plan.jpa

import com.flab.license.domain.plan.Plan
import com.flab.license.domain.plan.PlanId

internal fun PlanJpaEntity.toDomain(): Plan = Plan.reconstitute(
    id = PlanId(id),
    planCode = planCode,
    maxSeats = maxSeats,
    monthlyTokenLimit = monthlyTokenLimit,
    deleted = deleted
)

internal fun Plan.toEntity(): PlanJpaEntity = PlanJpaEntity(
    id = id.value,
    planCode = planCode,
    maxSeats = maxSeats,
    monthlyTokenLimit = monthlyTokenLimit,
    deleted = deleted
)
