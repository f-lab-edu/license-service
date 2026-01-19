package com.flab.license.api.license.dto

import com.flab.license.domain.license.License
import com.flab.license.domain.license.LicenseStatus
import com.flab.license.domain.license.Owner
import java.time.LocalDate
import java.util.UUID

data class LicenseResponse(
    val id: UUID,
    val planId: UUID,
    val ownerType: Owner.Type,
    val ownerId: String,
    val status: LicenseStatus,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    companion object {
        fun from(license: License): LicenseResponse {
            return LicenseResponse(
                id = license.id.value,
                planId = license.planId.value,
                ownerType = license.owner.type,
                ownerId = license.owner.id,
                status = license.status,
                startDate = license.period.startDate,
                endDate = license.period.endDate
            )
        }
    }
}
