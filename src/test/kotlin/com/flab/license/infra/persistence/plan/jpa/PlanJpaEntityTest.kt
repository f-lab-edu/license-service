package com.flab.license.infra.persistence.plan.jpa

import com.flab.license.domain.plan.Plan
import com.flab.license.domain.plan.PlanCode
import com.flab.license.domain.plan.PlanId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class PlanJpaEntityTest {

    @Nested
    @DisplayName("fromDomain")
    inner class FromDomain {

        @Test
        @DisplayName("도메인 객체를 JPA 엔티티로 변환할 수 있다")
        fun `convert domain to entity`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )

            // When
            val entity = PlanJpaEntity.fromDomain(plan)

            // Then
            assertThat(entity.id).isEqualTo(plan.id.value)
            assertThat(entity.planCode).isEqualTo(plan.planCode)
            assertThat(entity.maxSeats).isEqualTo(plan.maxSeats)
            assertThat(entity.monthlyTokenLimit).isEqualTo(plan.monthlyTokenLimit)
            assertThat(entity.deleted).isEqualTo(plan.deleted)
        }

        @Test
        @DisplayName("삭제된 도메인 객체도 변환할 수 있다")
        fun `convert deleted domain to entity`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.FREE,
                maxSeats = 1,
                monthlyTokenLimit = 50_000L
            ).delete()

            // When
            val entity = PlanJpaEntity.fromDomain(plan)

            // Then
            assertThat(entity.deleted).isTrue()
        }
    }

    @Nested
    @DisplayName("toDomain")
    inner class ToDomain {

        @Test
        @DisplayName("JPA 엔티티를 도메인 객체로 변환할 수 있다")
        fun `convert entity to domain`() {
            // Given
            val id = UUID.randomUUID()
            val entity = PlanJpaEntity(
                id = id,
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 100,
                monthlyTokenLimit = 5_000_000L,
                deleted = false
            )

            // When
            val plan = entity.toDomain()

            // Then
            assertThat(plan.id.value).isEqualTo(id)
            assertThat(plan.planCode).isEqualTo(PlanCode.ENTERPRISE)
            assertThat(plan.maxSeats).isEqualTo(100)
            assertThat(plan.monthlyTokenLimit).isEqualTo(5_000_000L)
            assertThat(plan.deleted).isFalse()
        }

        @Test
        @DisplayName("삭제된 엔티티도 도메인 객체로 변환할 수 있다")
        fun `convert deleted entity to domain`() {
            // Given
            val entity = PlanJpaEntity(
                id = UUID.randomUUID(),
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L,
                deleted = true
            )

            // When
            val plan = entity.toDomain()

            // Then
            assertThat(plan.deleted).isTrue()
        }
    }

    @Nested
    @DisplayName("roundTrip")
    inner class RoundTrip {

        @Test
        @DisplayName("도메인 -> 엔티티 -> 도메인 변환 시 데이터가 유지된다")
        fun `domain to entity to domain preserves data`() {
            // Given
            val original = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )

            // When
            val entity = PlanJpaEntity.fromDomain(original)
            val restored = entity.toDomain()

            // Then
            assertThat(restored.id).isEqualTo(original.id)
            assertThat(restored.planCode).isEqualTo(original.planCode)
            assertThat(restored.maxSeats).isEqualTo(original.maxSeats)
            assertThat(restored.monthlyTokenLimit).isEqualTo(original.monthlyTokenLimit)
            assertThat(restored.deleted).isEqualTo(original.deleted)
        }
    }
}
