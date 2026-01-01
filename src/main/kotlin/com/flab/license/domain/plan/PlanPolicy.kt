package com.flab.license.domain.plan

import com.flab.license.domain.plan.exception.PlanErrorCode
import com.flab.license.domain.plan.exception.PlanException

object PlanPolicy {
    private const val MIN_MAX_SEATS = 1
    private const val MIN_MONTHLY_TOKEN_LIMIT = 1L
    private const val SINGLE_SEAT_MAX_SEATS = 1

    private val SINGLE_SEAT_PLANS = setOf(PlanCode.FREE, PlanCode.PRO)

    fun validate(planCode: PlanCode, maxSeats: Int, monthlyTokenLimit: Long) {
        validateMaxSeats(maxSeats)
        validateMonthlyTokenLimit(monthlyTokenLimit)
        validateMaxSeatsForPlan(planCode, maxSeats)
    }

    private fun validateMaxSeats(maxSeats: Int) {
        if (maxSeats < MIN_MAX_SEATS) {
            throw PlanException(PlanErrorCode.INVALID_MAX_SEATS)
        }
    }

    private fun validateMonthlyTokenLimit(monthlyTokenLimit: Long) {
        if (monthlyTokenLimit < MIN_MONTHLY_TOKEN_LIMIT) {
            throw PlanException(PlanErrorCode.INVALID_MONTHLY_TOKEN_LIMIT)
        }
    }

    private fun validateMaxSeatsForPlan(planCode: PlanCode, maxSeats: Int) {
        if (planCode in SINGLE_SEAT_PLANS && maxSeats != SINGLE_SEAT_MAX_SEATS) {
            throw PlanException(PlanErrorCode.INVALID_MAX_SEATS_FOR_PLAN)
        }
    }
}
