package com.flab.license.infra.persistence.plan.jpa

import com.flab.license.domain.plan.PlanCode
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlanSpringDataJpaRepository : JpaRepository<PlanJpaEntity, UUID> {
    fun findByIdAndDeletedFalse(id: UUID): PlanJpaEntity?
    fun findByPlanCodeAndDeletedFalse(planCode: PlanCode): PlanJpaEntity?
    fun findAllByDeletedFalse(): List<PlanJpaEntity>
}
