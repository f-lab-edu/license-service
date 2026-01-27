package com.flab.license.service.license.command

import com.flab.license.domain.license.License
import com.flab.license.domain.license.LicenseId
import com.flab.license.domain.license.LicenseRepository
import com.flab.license.service.plan.query.PlanQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LicenseCommandService(
    private val licenseRepository: LicenseRepository,
    private val planQueryService: PlanQueryService
) {

    @Transactional
    fun create(command: CreateLicenseCommand): License {
        planQueryService.getById(command.planId)

        val license = License.create(
            planId = command.planId,
            owner = command.owner,
            period = command.period
        )
        return licenseRepository.save(license)
    }

    @Transactional
    fun expire(licenseId: LicenseId): License {
        val license = licenseRepository.loadById(licenseId)
        val expired = license.expire()
        return licenseRepository.update(expired)
    }

    @Transactional
    fun delete(licenseId: LicenseId) {
        val license = licenseRepository.loadById(licenseId)
        val deleted = license.delete()
        licenseRepository.delete(deleted)
    }
}
