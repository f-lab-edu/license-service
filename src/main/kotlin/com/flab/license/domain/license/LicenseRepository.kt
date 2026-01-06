package com.flab.license.domain.license

interface LicenseRepository {
    fun save(license: License): License
    fun update(license: License): License
    fun delete(license: License): License

    fun loadById(id: LicenseId): License
    fun loadByOwner(owner: Owner): List<License>

    // 삭제된 라이선스 포함 조회 (관리자용)
    fun loadByIdIncludeDeleted(id: LicenseId): License
}
