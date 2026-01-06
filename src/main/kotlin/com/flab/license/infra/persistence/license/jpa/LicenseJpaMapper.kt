package com.flab.license.infra.persistence.license.jpa

import com.flab.license.domain.license.License
import com.flab.license.domain.license.LicenseId
import com.flab.license.domain.license.Owner
import com.flab.license.domain.license.Period
import com.flab.license.domain.plan.PlanId

internal fun LicenseJpaEntity.toDomain(): License = License.reconstitute(
    id = LicenseId(id),
    planId = PlanId(planId),
    owner = Owner.reconstitute(ownerType, ownerId),
    status = status,
    period = Period.reconstitute(startDate, endDate),
    deleted = deleted
)

internal fun License.toEntity(): LicenseJpaEntity = LicenseJpaEntity(
    id = id.value,
    planId = planId.value,
    ownerType = owner.type,
    ownerId = owner.id,
    status = status,
    startDate = period.startDate,
    endDate = period.endDate,
    deleted = deleted
)
