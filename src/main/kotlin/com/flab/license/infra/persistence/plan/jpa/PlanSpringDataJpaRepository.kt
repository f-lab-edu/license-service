package com.flab.license.infra.persistence.plan.jpa

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlanSpringDataJpaRepository : JpaRepository<PlanJpaEntity, UUID>
