package com.flab.license.domain.license

import com.flab.license.domain.license.exception.LicenseErrorCode
import com.flab.license.domain.license.exception.LicenseException
import java.time.LocalDate

object LicensePolicy {

    fun validateUsable(status: LicenseStatus, deleted: Boolean, period: Period, date: LocalDate) {
        validateNotDeleted(deleted)
        validateActive(status)
        validatePeriod(period, date)
    }

    fun validateExpiration(status: LicenseStatus, deleted: Boolean) {
        validateNotDeleted(deleted)
        if (status.isExpired()) {
            throw LicenseException(LicenseErrorCode.ALREADY_EXPIRED)
        }
    }

    fun validateDeletion(deleted: Boolean) {
        validateNotDeleted(deleted)
    }

    private fun validateNotDeleted(deleted: Boolean) {
        if (deleted) {
            throw LicenseException(LicenseErrorCode.LICENSE_ALREADY_DELETED)
        }
    }

    private fun validateActive(status: LicenseStatus) {
        if (status.isNotActive()) {
            throw LicenseException(LicenseErrorCode.LICENSE_NOT_ACTIVE)
        }
    }

    private fun validatePeriod(period: Period, date: LocalDate) {
        if (date.isBefore(period.startDate)) {
            throw LicenseException(LicenseErrorCode.LICENSE_PERIOD_NOT_STARTED)
        }
        if (date.isAfter(period.endDate)) {
            throw LicenseException(LicenseErrorCode.LICENSE_PERIOD_ENDED)
        }
    }
}
