package com.flab.license.domain.plan

import com.flab.license.domain.plan.exception.PlanErrorCode
import com.flab.license.domain.plan.exception.PlanException

class Plan private constructor(
    val id: PlanId,
    val planCode: PlanCode,
    val maxSeats: Int,
    val monthlyTokenLimit: Long,
    val deleted: Boolean = false
) {
    init {
        if (maxSeats < 0) {
            throw PlanException(PlanErrorCode.INVALID_MAX_SEATS)
        }
        if (monthlyTokenLimit < 0) {
            throw PlanException(PlanErrorCode.INVALID_MONTHLY_TOKEN_LIMIT)
        }
    }

    fun update(maxSeats: Int, monthlyTokenLimit: Long): Plan {
        if (deleted) {
            throw PlanException(PlanErrorCode.PLAN_ALREADY_DELETED)
        }
        return Plan(
            id = this.id,
            planCode = this.planCode,
            maxSeats = maxSeats,
            monthlyTokenLimit = monthlyTokenLimit,
            deleted = this.deleted
        )
    }

    fun delete(): Plan {
        if (deleted) {
            throw PlanException(PlanErrorCode.PLAN_ALREADY_DELETED)
        }
        return Plan(
            id = this.id,
            planCode = this.planCode,
            maxSeats = this.maxSeats,
            monthlyTokenLimit = this.monthlyTokenLimit,
            deleted = true
        )
    }

    companion object {
        fun create(
            planCode: PlanCode,
            maxSeats: Int,
            monthlyTokenLimit: Long
        ): Plan {
            return Plan(
                id = PlanId.generate(),
                planCode = planCode,
                maxSeats = maxSeats,
                monthlyTokenLimit = monthlyTokenLimit
            )
        }

        fun reconstitute(
            id: PlanId,
            planCode: PlanCode,
            maxSeats: Int,
            monthlyTokenLimit: Long,
            deleted: Boolean
        ): Plan {
            return Plan(
                id = id,
                planCode = planCode,
                maxSeats = maxSeats,
                monthlyTokenLimit = monthlyTokenLimit,
                deleted = deleted
            )
        }
    }
}
