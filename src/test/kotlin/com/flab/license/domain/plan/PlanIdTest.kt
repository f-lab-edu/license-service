package com.flab.license.domain.plan

import org.assertj.core.api.Assertions.assertThat
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
