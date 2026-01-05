package com.flab.license.infra.persistence.license.jpa

import com.flab.license.infra.persistence.common.jpa.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "license")
class LicenseJpaEntity : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0


}
