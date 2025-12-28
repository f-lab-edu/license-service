package com.flab.license.infra.persistence.license.jpa

import org.springframework.data.jpa.repository.JpaRepository

interface LicenseSpringDataJpaRepository : JpaRepository<LicenseJpaEntity, Long>
