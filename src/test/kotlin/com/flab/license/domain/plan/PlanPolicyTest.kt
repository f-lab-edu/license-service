package com.flab.license.domain.plan

import com.flab.license.domain.plan.exception.PlanErrorCode
import com.flab.license.domain.plan.exception.PlanException
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class PlanPolicyTest {

    @Nested
    @DisplayName("validate")
    inner class Validate {

        @Test
        @DisplayName("ENTERPRISE 플랜은 maxSeats가 1 이상이면 통과")
        fun `enterprise plan with valid maxSeats passes`() {
            assertThatCode {
                PlanPolicy.validate(PlanCode.ENTERPRISE, 10, 100_000L)
            }.doesNotThrowAnyException()
        }

        @ParameterizedTest
        @EnumSource(value = PlanCode::class, names = ["FREE", "PRO"])
        @DisplayName("FREE/PRO 플랜은 maxSeats가 1이면 통과")
        fun `free and pro plan with maxSeats 1 passes`(planCode: PlanCode) {
            assertThatCode {
                PlanPolicy.validate(planCode, 1, 100_000L)
            }.doesNotThrowAnyException()
        }

        @Test
        @DisplayName("maxSeats가 0이면 INVALID_MAX_SEATS 예외")
        fun `throw exception when maxSeats is zero`() {
            assertThatThrownBy {
                PlanPolicy.validate(PlanCode.ENTERPRISE, 0, 100_000L)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MAX_SEATS)
        }

        @Test
        @DisplayName("monthlyTokenLimit이 0이면 INVALID_MONTHLY_TOKEN_LIMIT 예외")
        fun `throw exception when monthlyTokenLimit is zero`() {
            assertThatThrownBy {
                PlanPolicy.validate(PlanCode.ENTERPRISE, 10, 0L)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MONTHLY_TOKEN_LIMIT)
        }

        @ParameterizedTest
        @EnumSource(value = PlanCode::class, names = ["FREE", "PRO"])
        @DisplayName("FREE/PRO 플랜에서 maxSeats가 1이 아니면 INVALID_MAX_SEATS_FOR_PLAN 예외")
        fun `throw exception when free or pro plan has maxSeats not 1`(planCode: PlanCode) {
            assertThatThrownBy {
                PlanPolicy.validate(planCode, 5, 100_000L)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.INVALID_MAX_SEATS_FOR_PLAN)
        }
    }
}
