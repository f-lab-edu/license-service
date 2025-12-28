package com.flab.license.infra.persistence.plan.jpa

import com.flab.license.domain.plan.PlanRepository
import org.springframework.stereotype.Repository

@Repository
class PlanRepositoryJpaAdapter(
    private val springDataJpaRepository: PlanSpringDataJpaRepository
) : PlanRepository
