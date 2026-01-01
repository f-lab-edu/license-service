package com.flab.license.service.plan.query

import com.flab.license.domain.plan.Plan
import com.flab.license.domain.plan.PlanCode
import com.flab.license.domain.plan.PlanId
import com.flab.license.domain.plan.PlanRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PlanQueryService(
    private val planRepository: PlanRepository
) {

    fun getById(planId: PlanId): Plan {
        return planRepository.loadById(planId)
    }

    fun getByPlanCode(planCode: PlanCode): Plan {
        return planRepository.loadByPlanCode(planCode)
    }

    fun getAll(): List<Plan> {
        return planRepository.loadAll()
    }
}
