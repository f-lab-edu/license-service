package com.flab.license.domain.plan

import com.flab.license.domain.plan.exception.PlanErrorCode
import com.flab.license.domain.plan.exception.PlanException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class PlanIdTest {

    @Nested
    @DisplayName("generate")
    inner class Generate {

        @Test
        @DisplayName("UUID를 생성할 수 있다")
        fun `generate creates valid UUID`() {
            // When
            val planId = PlanId.generate()

            // Then
            assertThat(planId.value).isNotNull()
            assertThat(planId.value).isInstanceOf(UUID::class.java)
        }

        @Test
        @DisplayName("생성할 때마다 다른 UUID가 생성된다")
        fun `generate creates unique UUIDs`() {
            // When
            val planId1 = PlanId.generate()
            val planId2 = PlanId.generate()

            // Then
            assertThat(planId1).isNotEqualTo(planId2)
        }
    }

    @Nested
    @DisplayName("from")
    inner class From {

        @Test
        @DisplayName("유효한 UUID 문자열로 PlanId를 생성할 수 있다")
        fun `from creates PlanId from valid UUID string`() {
            // Given
            val uuidString = "550e8400-e29b-41d4-a716-446655440000"

            // When
            val planId = PlanId.from(uuidString)

            // Then
            assertThat(planId.value.toString()).isEqualTo(uuidString)
        }

        @Test
        @DisplayName("잘못된 UUID 문자열이면 예외가 발생한다")
        fun `from throws exception for invalid UUID string`() {
            // Given
            val invalidUuidString = "invalid-uuid"

            // When & Then
            assertThatThrownBy {
                PlanId.from(invalidUuidString)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_PLAN_ID)
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        fun `from throws exception for empty string`() {
            // When & Then
            assertThatThrownBy {
                PlanId.from("")
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_PLAN_ID)
        }

        @Test
        @DisplayName("UUID 형식이 아닌 문자열이면 예외가 발생한다")
        fun `from throws exception for non-UUID format`() {
            // Given
            val nonUuidString = "12345"

            // When & Then
            assertThatThrownBy {
                PlanId.from(nonUuidString)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_PLAN_ID)
        }
    }

    @Nested
    @DisplayName("equality")
    inner class Equality {

        @Test
        @DisplayName("같은 UUID를 가진 PlanId는 동등하다")
        fun `PlanIds with same UUID are equal`() {
            // Given
            val uuid = UUID.randomUUID()

            // When
            val planId1 = PlanId(uuid)
            val planId2 = PlanId(uuid)

            // Then
            assertThat(planId1).isEqualTo(planId2)
        }

        @Test
        @DisplayName("다른 UUID를 가진 PlanId는 동등하지 않다")
        fun `PlanIds with different UUIDs are not equal`() {
            // When
            val planId1 = PlanId(UUID.randomUUID())
            val planId2 = PlanId(UUID.randomUUID())

            // Then
            assertThat(planId1).isNotEqualTo(planId2)
        }
    }
}
