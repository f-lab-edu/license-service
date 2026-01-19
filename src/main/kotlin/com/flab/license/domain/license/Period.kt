package com.flab.license.domain.license

import com.flab.license.domain.license.exception.LicenseErrorCode
import com.flab.license.domain.license.exception.LicenseException
import java.time.LocalDate

@ConsistentCopyVisibility
data class Period private constructor(
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    companion object {
        fun of(startDate: LocalDate, endDate: LocalDate): Period {
            if (endDate.isBefore(startDate)) {
                throw LicenseException(LicenseErrorCode.INVALID_PERIOD)
            }
            return Period(startDate, endDate)
        }

        fun reconstitute(startDate: LocalDate, endDate: LocalDate) =
            Period(startDate, endDate)
    }
}
