package com.flab.license.infra.persistence.license.jpa

import com.flab.license.domain.license.LicenseRepository
import org.springframework.stereotype.Repository

@Repository
class LicenseRepositoryJpaAdapter(
    private val springDataJpaRepository: LicenseSpringDataJpaRepository
) : LicenseRepository
