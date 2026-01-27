package com.flab.license.service.license.query

import com.flab.license.domain.license.License
import com.flab.license.domain.license.LicenseId
import com.flab.license.domain.license.LicenseRepository
import com.flab.license.domain.license.Owner
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LicenseQueryService(
    private val licenseRepository: LicenseRepository
) {

    fun getById(licenseId: LicenseId): License {
        return licenseRepository.loadById(licenseId)
    }

    fun getByOwner(owner: Owner): List<License> {
        return licenseRepository.loadByOwner(owner)
    }
}
