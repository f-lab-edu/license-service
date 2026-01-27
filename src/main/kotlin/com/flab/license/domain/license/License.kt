package com.flab.license.domain.license

import com.flab.license.domain.plan.PlanId
import java.time.LocalDate

class License private constructor(
    val id: LicenseId,
    val planId: PlanId,
    val owner: Owner,
    val status: LicenseStatus,
    val period: Period,
    val deleted: Boolean = false
) {

    fun expire(): License {
        LicensePolicy.validateExpiration(status, deleted)
        return License(
            id = this.id,
            planId = this.planId,
            owner = this.owner,
            status = LicenseStatus.EXPIRED,
            period = this.period,
            deleted = this.deleted
        )
    }

    fun delete(): License {
        LicensePolicy.validateDeletion(deleted)
        return License(
            id = this.id,
            planId = this.planId,
            owner = this.owner,
            status = this.status,
            period = this.period,
            deleted = true
        )
    }

    fun validateUsable(date: LocalDate = LocalDate.now()) {
        LicensePolicy.validateUsable(status, deleted, period, date)
    }

    companion object {
        fun create(
            planId: PlanId,
            owner: Owner,
            period: Period
        ): License {
            return License(
                id = LicenseId.generate(),
                planId = planId,
                owner = owner,
                status = LicenseStatus.ACTIVE,
                period = period
            )
        }

        fun reconstitute(
            id: LicenseId,
            planId: PlanId,
            owner: Owner,
            status: LicenseStatus,
            period: Period,
            deleted: Boolean
        ) = License(
            id = id,
            planId = planId,
            owner = owner,
            status = status,
            period = period,
            deleted = deleted
        )
    }
}
