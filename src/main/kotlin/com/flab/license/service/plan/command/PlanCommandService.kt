package com.flab.license.service.plan.command

import com.flab.license.domain.plan.Plan
import com.flab.license.domain.plan.PlanId
import com.flab.license.domain.plan.PlanRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlanCommandService(
    private val planRepository: PlanRepository
) {

    @Transactional
    fun create(command: CreatePlanCommand): Plan {
        val plan = Plan.create(
            planCode = command.planCode,
            maxSeats = command.maxSeats,
            monthlyTokenLimit = command.monthlyTokenLimit
        )
        return planRepository.save(plan)
    }

    @Transactional
    fun update(command: UpdatePlanCommand): Plan {
        val plan = planRepository.loadById(command.planId)
        val updated = plan.update(
            maxSeats = command.maxSeats,
            monthlyTokenLimit = command.monthlyTokenLimit
        )
        return planRepository.update(updated)
    }

    @Transactional
    fun delete(planId: PlanId): Plan {
        val plan = planRepository.loadById(planId)
        val deleted = plan.delete()
        return planRepository.delete(deleted)
    }
}
