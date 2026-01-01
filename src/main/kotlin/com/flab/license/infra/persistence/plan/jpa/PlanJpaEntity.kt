package com.flab.license.infra.persistence.plan.jpa

import com.flab.license.domain.plan.PlanCode
import com.flab.license.infra.persistence.common.jpa.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "plan")
class PlanJpaEntity(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val planCode: PlanCode,

    @Column(nullable = false)
    val maxSeats: Int,

    @Column(nullable = false)
    val monthlyTokenLimit: Long,

    @Column(nullable = false)
    val deleted: Boolean = false
) : BaseTimeEntity()
