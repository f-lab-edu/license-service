package com.flab.license.infra.persistence.license.jpa

import com.flab.license.domain.license.License
import com.flab.license.infra.persistence.common.jpa.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "license")
class LicenseJpaEntity : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    companion object {
        fun toDomain(entity: LicenseJpaEntity): License = License(entity.id.toString())
    }
}
