package com.flab.license.infra.persistence.license.jpa

import com.flab.license.common.exception.CommonErrorCode
import com.flab.license.common.exception.CustomException
import com.flab.license.domain.license.License
import com.flab.license.domain.license.LicenseId
import com.flab.license.domain.license.LicenseRepository
import com.flab.license.domain.license.Owner
import com.flab.license.domain.license.exception.LicenseErrorCode
import com.flab.license.domain.license.exception.LicenseException
import org.springframework.stereotype.Repository

@Repository
class LicenseRepositoryJpaAdapter(
    private val springDataJpaRepository: LicenseSpringDataJpaRepository
) : LicenseRepository {

    override fun save(license: License): License {
        val entity = license.toEntity()
        val saved = springDataJpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun update(license: License): License = save(license)

    override fun delete(license: License): License = save(license)

    override fun loadById(id: LicenseId): License {
        return findById(id) ?: throw LicenseException(LicenseErrorCode.LICENSE_NOT_FOUND)
    }

    override fun loadByOwner(owner: Owner): List<License> {
        return springDataJpaRepository.findByOwnerTypeAndOwnerIdAndDeletedFalse(owner.type, owner.id)
            .map { it.toDomain() }
    }

    private fun findById(id: LicenseId): License? {
        return springDataJpaRepository.findByIdAndDeletedFalse(id.value)?.toDomain()
    }

    override fun loadByIdIncludeDeleted(id: LicenseId): License {
        throw CustomException(CommonErrorCode.NOT_IMPLEMENTED)
    }
}
