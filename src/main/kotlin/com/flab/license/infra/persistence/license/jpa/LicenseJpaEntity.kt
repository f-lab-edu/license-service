package com.flab.license.infra.persistence.license.jpa

import com.flab.license.domain.license.LicenseStatus
import com.flab.license.domain.license.Owner
import com.flab.license.infra.persistence.common.jpa.BaseTimeEntity
import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "license")
class LicenseJpaEntity(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    val planId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val ownerType: Owner.Type,

    @Column(nullable = false)
    val ownerId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: LicenseStatus,

    @Column(nullable = false)
    val startDate: LocalDate,

    @Column(nullable = false)
    val endDate: LocalDate,

    @Column(nullable = false)
    val deleted: Boolean = false
) : BaseTimeEntity()
