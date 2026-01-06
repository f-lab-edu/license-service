package com.flab.license.infra.persistence.license.jpa

import com.flab.license.domain.license.Owner
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface LicenseSpringDataJpaRepository : JpaRepository<LicenseJpaEntity, UUID> {
    fun findByIdAndDeletedFalse(id: UUID): LicenseJpaEntity?
    fun findByOwnerTypeAndOwnerIdAndDeletedFalse(ownerType: Owner.Type, ownerId: String): List<LicenseJpaEntity>
}
