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
        @DisplayName("ENTERPRISE 플랜을 생성할 수 있다")
        fun `create enterprise plan successfully`() {
            // When
            val plan = Plan.create(
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L
            )

            // Then
            assertThat(plan.id).isNotNull
            assertThat(plan.planCode).isEqualTo(PlanCode.ENTERPRISE)
            assertThat(plan.maxSeats).isEqualTo(10)
            assertThat(plan.monthlyTokenLimit).isEqualTo(100_000L)
            assertThat(plan.deleted).isFalse
        }

        @Test
        @DisplayName("FREE 플랜은 maxSeats=1로 생성할 수 있다")
        fun `create free plan with maxSeats 1`() {
            // When
            val plan = Plan.create(
                planCode = PlanCode.FREE,
                maxSeats = 1,
                monthlyTokenLimit = 10_000L
            )

            // Then
            assertThat(plan.planCode).isEqualTo(PlanCode.FREE)
            assertThat(plan.maxSeats).isEqualTo(1)
        }

        @Test
        @DisplayName("PRO 플랜은 maxSeats=1로 생성할 수 있다")
        fun `create pro plan with maxSeats 1`() {
            // When
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 1,
                monthlyTokenLimit = 100_000L
            )

            // Then
            assertThat(plan.planCode).isEqualTo(PlanCode.PRO)
            assertThat(plan.maxSeats).isEqualTo(1)
        }

        @Test
        @DisplayName("maxSeats가 0이면 예외가 발생한다")
        fun `throw exception when maxSeats is zero`() {
            assertThatThrownBy {
                Plan.create(
                    planCode = PlanCode.ENTERPRISE,
                    maxSeats = 0,
                    monthlyTokenLimit = 100_000L
                )
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MAX_SEATS)
        }

        @Test
        @DisplayName("monthlyTokenLimit이 0이면 예외가 발생한다")
        fun `throw exception when monthlyTokenLimit is zero`() {
            assertThatThrownBy {
                Plan.create(
                    planCode = PlanCode.ENTERPRISE,
                    maxSeats = 10,
                    monthlyTokenLimit = 0L
                )
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MONTHLY_TOKEN_LIMIT)
        }

        @Test
        @DisplayName("FREE 플랜에서 maxSeats가 1이 아니면 예외가 발생한다")
        fun `throw exception when free plan maxSeats is not 1`() {
            assertThatThrownBy {
                Plan.create(
                    planCode = PlanCode.FREE,
                    maxSeats = 2,
                    monthlyTokenLimit = 10_000L
                )
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MAX_SEATS_FOR_PLAN)
        }

        @Test
        @DisplayName("PRO 플랜에서 maxSeats가 1이 아니면 예외가 발생한다")
        fun `throw exception when pro plan maxSeats is not 1`() {
            assertThatThrownBy {
                Plan.create(
                    planCode = PlanCode.PRO,
                    maxSeats = 5,
                    monthlyTokenLimit = 100_000L
                )
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MAX_SEATS_FOR_PLAN)
        }

    }

    @Nested
    @DisplayName("update")
    inner class Update {

        @Test
        @DisplayName("ENTERPRISE 플랜을 수정할 수 있다")
        fun `update enterprise plan successfully`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.ENTERPRISE,
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
        @DisplayName("FREE/PRO 플랜은 monthlyTokenLimit만 수정할 수 있다")
        fun `update free pro plan monthlyTokenLimit only`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 1,
                monthlyTokenLimit = 100_000L
            )

            // When
            val updated = plan.update(maxSeats = 1, monthlyTokenLimit = 500_000L)

            // Then
            assertThat(updated.monthlyTokenLimit).isEqualTo(500_000L)
        }

        @Test
        @DisplayName("수정 시 maxSeats가 0이면 예외가 발생한다")
        fun `throw exception when update with zero maxSeats`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L
            )

            // When & Then
            assertThatThrownBy {
                plan.update(maxSeats = 0, monthlyTokenLimit = 100_000L)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MAX_SEATS)
        }

        @Test
        @DisplayName("수정 시 monthlyTokenLimit이 0이면 예외가 발생한다")
        fun `throw exception when update with zero monthlyTokenLimit`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L
            )

            // When & Then
            assertThatThrownBy {
                plan.update(maxSeats = 10, monthlyTokenLimit = 0L)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MONTHLY_TOKEN_LIMIT)
        }

        @Test
        @DisplayName("FREE/PRO 플랜에서 maxSeats를 1이 아닌 값으로 수정하면 예외가 발생한다")
        fun `throw exception when update free pro plan with maxSeats not 1`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 1,
                monthlyTokenLimit = 100_000L
            )

            // When & Then
            assertThatThrownBy {
                plan.update(maxSeats = 5, monthlyTokenLimit = 100_000L)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MAX_SEATS_FOR_PLAN)
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
                planCode = PlanCode.ENTERPRISE,
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
                planCode = PlanCode.ENTERPRISE,
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
                planCode = PlanCode.ENTERPRISE,
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
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 10,
                monthlyTokenLimit = 100_000L,
                deleted = true
            )

            // Then
            assertThat(plan.deleted).isTrue
        }

        @Test
        @DisplayName("복원 시 검증을 수행하지 않는다 - 잘못된 데이터도 복원 가능")
        fun `reconstitute skips validation for invalid data`() {
            // Given - 잘못된 데이터 (maxSeats=0)
            val id = PlanId.generate()

            // When - 복원은 성공
            val plan = Plan.reconstitute(
                id = id,
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 0,
                monthlyTokenLimit = 0L,
                deleted = false
            )

            // Then
            assertThat(plan.maxSeats).isEqualTo(0)
            assertThat(plan.monthlyTokenLimit).isEqualTo(0L)
        }

        @Test
        @DisplayName("복원된 잘못된 데이터를 수정하려 하면 검증 실패")
        fun `update on reconstituted invalid data fails validation`() {
            // Given - 잘못된 데이터로 복원
            val plan = Plan.reconstitute(
                id = PlanId.generate(),
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 0,
                monthlyTokenLimit = 100_000L,
                deleted = false
            )

            // When & Then - 수정 시 검증 실패
            assertThatThrownBy {
                plan.update(maxSeats = 0, monthlyTokenLimit = 100_000L)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MAX_SEATS)
        }
    }
}
