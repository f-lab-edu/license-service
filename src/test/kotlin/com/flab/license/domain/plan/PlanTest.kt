package com.flab.license.domain.plan

import com.flab.license.domain.plan.exception.PlanErrorCode
import com.flab.license.domain.plan.exception.PlanException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PlanTest {

    @Nested
    @DisplayName("create")
    inner class Create {

        @Test
        @DisplayName("플랜을 생성할 수 있다")
        fun `create plan successfully`() {
            // When
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L
            )

            // Then
            assertThat(plan.id).isNotNull
            assertThat(plan.planCode).isEqualTo(PlanCode.PRO)
            assertThat(plan.maxSeats).isEqualTo(10)
            assertThat(plan.monthlyTokenLimit).isEqualTo(100_000L)
            assertThat(plan.deleted).isFalse
        }

        @Test
        @DisplayName("maxSeats가 음수이면 예외가 발생한다")
        fun `throw exception when maxSeats is negative`() {
            assertThatThrownBy {
                Plan.create(
                    planCode = PlanCode.PRO,
                    maxSeats = -1,
                    monthlyTokenLimit = 100_000L
                )
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MAX_SEATS)
        }

        @Test
        @DisplayName("monthlyTokenLimit이 음수이면 예외가 발생한다")
        fun `throw exception when monthlyTokenLimit is negative`() {
            assertThatThrownBy {
                Plan.create(
                    planCode = PlanCode.PRO,
                    maxSeats = 10,
                    monthlyTokenLimit = -1L
                )
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MONTHLY_TOKEN_LIMIT)
        }

        @Test
        @DisplayName("maxSeats가 0이면 생성할 수 있다")
        fun `create plan with zero maxSeats`() {
            // When
            val plan = Plan.create(
                planCode = PlanCode.FREE,
                maxSeats = 0,
                monthlyTokenLimit = 1000L
            )

            // Then
            assertThat(plan.maxSeats).isEqualTo(0)
        }

        @Test
        @DisplayName("monthlyTokenLimit이 0이면 생성할 수 있다")
        fun `create plan with zero monthlyTokenLimit`() {
            // When
            val plan = Plan.create(
                planCode = PlanCode.FREE,
                maxSeats = 1,
                monthlyTokenLimit = 0L
            )

            // Then
            assertThat(plan.monthlyTokenLimit).isEqualTo(0L)
        }

    }

    @Nested
    @DisplayName("update")
    inner class Update {

        @Test
        @DisplayName("플랜을 수정할 수 있다")
        fun `update plan successfully`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 5,
                monthlyTokenLimit = 100_000L
            )

            // When
            val updated = plan.update(maxSeats = 10, monthlyTokenLimit = 500_000L)

            // Then
            assertThat(updated.id).isEqualTo(plan.id)
            assertThat(updated.planCode).isEqualTo(plan.planCode)
            assertThat(updated.maxSeats).isEqualTo(10)
            assertThat(updated.monthlyTokenLimit).isEqualTo(500_000L)
        }

        @Test
        @DisplayName("수정 시 maxSeats가 음수이면 예외가 발생한다")
        fun `throw exception when update with negative maxSeats`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L
            )

            // When & Then
            assertThatThrownBy {
                plan.update(maxSeats = -1, monthlyTokenLimit = 100_000L)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MAX_SEATS)
        }

        @Test
        @DisplayName("수정 시 monthlyTokenLimit이 음수이면 예외가 발생한다")
        fun `throw exception when update with negative monthlyTokenLimit`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L
            )

            // When & Then
            assertThatThrownBy {
                plan.update(maxSeats = 10, monthlyTokenLimit = -1L)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MONTHLY_TOKEN_LIMIT)
        }

        @Test
        @DisplayName("수정 시 maxSeats를 0으로 변경할 수 있다")
        fun `update plan with zero maxSeats`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L
            )

            // When
            val updated = plan.update(maxSeats = 0, monthlyTokenLimit = 100_000L)

            // Then
            assertThat(updated.maxSeats).isEqualTo(0)
        }

        @Test
        @DisplayName("수정 시 monthlyTokenLimit을 0으로 변경할 수 있다")
        fun `update plan with zero monthlyTokenLimit`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L
            )

            // When
            val updated = plan.update(maxSeats = 10, monthlyTokenLimit = 0L)

            // Then
            assertThat(updated.monthlyTokenLimit).isEqualTo(0L)
        }
    }

    @Nested
    @DisplayName("delete")
    inner class Delete {

        @Test
        @DisplayName("플랜을 삭제하면 deleted가 true가 된다")
        fun `delete plan sets deleted to true`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L
            )

            // When
            val deleted = plan.delete()

            // Then
            assertThat(deleted.deleted).isTrue
            assertThat(deleted.id).isEqualTo(plan.id)
            assertThat(deleted.planCode).isEqualTo(plan.planCode)
        }

        @Test
        @DisplayName("이미 삭제된 플랜을 다시 삭제하면 예외가 발생한다")
        fun `throw exception when delete already deleted plan`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L
            )
            val deletedPlan = plan.delete()

            // When & Then
            assertThatThrownBy {
                deletedPlan.delete()
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.PLAN_ALREADY_DELETED)
        }

        @Test
        @DisplayName("삭제된 플랜을 수정하면 예외가 발생한다")
        fun `throw exception when update deleted plan`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L
            )
            val deletedPlan = plan.delete()

            // When & Then
            assertThatThrownBy {
                deletedPlan.update(maxSeats = 20, monthlyTokenLimit = 200_000L)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.PLAN_ALREADY_DELETED)
        }
    }

    @Nested
    @DisplayName("reconstitute")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 플랜을 복원할 수 있다")
        fun `reconstitute plan successfully`() {
            // Given
            val id = PlanId.generate()
            val planCode = PlanCode.ENTERPRISE
            val maxSeats = 100
            val monthlyTokenLimit = 1_000_000L
            val deleted = false

            // When
            val plan = Plan.reconstitute(
                id = id,
                planCode = planCode,
                maxSeats = maxSeats,
                monthlyTokenLimit = monthlyTokenLimit,
                deleted = deleted
            )

            // Then
            assertThat(plan.id).isEqualTo(id)
            assertThat(plan.planCode).isEqualTo(planCode)
            assertThat(plan.maxSeats).isEqualTo(maxSeats)
            assertThat(plan.monthlyTokenLimit).isEqualTo(monthlyTokenLimit)
            assertThat(plan.deleted).isEqualTo(deleted)
        }

        @Test
        @DisplayName("삭제된 플랜도 복원할 수 있다")
        fun `reconstitute deleted plan`() {
            // Given
            val id = PlanId.generate()

            // When
            val plan = Plan.reconstitute(
                id = id,
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L,
                deleted = true
            )

            // Then
            assertThat(plan.deleted).isTrue
        }

        @Test
        @DisplayName("복원 시 maxSeats가 음수이면 예외가 발생한다")
        fun `throw exception when reconstitute with negative maxSeats`() {
            assertThatThrownBy {
                Plan.reconstitute(
                    id = PlanId.generate(),
                    planCode = PlanCode.PRO,
                    maxSeats = -1,
                    monthlyTokenLimit = 100_000L,
                    deleted = false
                )
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MAX_SEATS)
        }

        @Test
        @DisplayName("복원 시 monthlyTokenLimit이 음수이면 예외가 발생한다")
        fun `throw exception when reconstitute with negative monthlyTokenLimit`() {
            assertThatThrownBy {
                Plan.reconstitute(
                    id = PlanId.generate(),
                    planCode = PlanCode.PRO,
                    maxSeats = 10,
                    monthlyTokenLimit = -1L,
                    deleted = false
                )
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MONTHLY_TOKEN_LIMIT)
        }
    }
}
