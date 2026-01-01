package com.flab.license.infra.persistence.plan.jpa

import com.flab.license.common.exception.CommonErrorCode
import com.flab.license.common.exception.CustomException
import com.flab.license.domain.plan.Plan
import com.flab.license.domain.plan.PlanCode
import com.flab.license.domain.plan.PlanId
import com.flab.license.domain.plan.PlanRepository
import com.flab.license.domain.plan.exception.PlanErrorCode
import com.flab.license.domain.plan.exception.PlanException
import org.springframework.stereotype.Repository

@Repository
class PlanRepositoryJpaAdapter(
    private val springDataJpaRepository: PlanSpringDataJpaRepository
) : PlanRepository {

    override fun save(plan: Plan): Plan {
        val entity = plan.toEntity()
        val saved = springDataJpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun update(plan: Plan): Plan = save(plan)

    override fun delete(plan: Plan): Plan = save(plan)

    override fun loadById(id: PlanId): Plan {
        return findById(id) ?: throw PlanException(PlanErrorCode.PLAN_NOT_FOUND)
    }

    override fun loadByPlanCode(planCode: PlanCode): Plan {
        return findByPlanCode(planCode) ?: throw PlanException(PlanErrorCode.PLAN_NOT_FOUND)
    }

    override fun loadAll(): List<Plan> {
        return springDataJpaRepository.findAllByDeletedFalse().map { it.toDomain() }
    }

    private fun findById(id: PlanId): Plan? {
        return springDataJpaRepository.findByIdAndDeletedFalse(id.value)?.toDomain()
    }

    private fun findByPlanCode(planCode: PlanCode): Plan? {
        return springDataJpaRepository.findByPlanCodeAndDeletedFalse(planCode)?.toDomain()
    }

    override fun loadByIdIncludeDeleted(id: PlanId): Plan {
        throw CustomException(CommonErrorCode.NOT_IMPLEMENTED)
    }
}
