package com.flab.license.domain.license

import com.flab.license.domain.license.exception.LicenseErrorCode
import com.flab.license.domain.license.exception.LicenseException
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LicensePolicyTest {

    private val defaultPeriod = Period.of(
        LocalDate.of(2025, 1, 1),
        LocalDate.of(2025, 12, 31)
    )

    @Nested
    @DisplayName("validateUsable")
    inner class ValidateUsable {

        @Test
        @DisplayName("ACTIVE 상태이고 삭제되지 않았으며 유효 기간 내이면 검증을 통과한다")
        fun `valid license passes validation`() {
            // Given
            val dateInPeriod = LocalDate.of(2025, 6, 15)

            // When & Then
            assertThatCode {
                LicensePolicy.validateUsable(
                    status = LicenseStatus.ACTIVE,
                    deleted = false,
                    period = defaultPeriod,
                    date = dateInPeriod
                )
            }.doesNotThrowAnyException()
        }

        @Test
        @DisplayName("삭제된 라이선스는 LICENSE_ALREADY_DELETED 예외가 발생한다")
        fun `throw exception when license is deleted`() {
            assertThatThrownBy {
                LicensePolicy.validateUsable(
                    status = LicenseStatus.ACTIVE,
                    deleted = true,
                    period = defaultPeriod,
                    date = LocalDate.of(2025, 6, 15)
                )
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_ALREADY_DELETED)
        }

        @Test
        @DisplayName("ACTIVE 상태가 아니면 LICENSE_NOT_ACTIVE 예외가 발생한다")
        fun `throw exception when license is not active`() {
            assertThatThrownBy {
                LicensePolicy.validateUsable(
                    status = LicenseStatus.EXPIRED,
                    deleted = false,
                    period = defaultPeriod,
                    date = LocalDate.of(2025, 6, 15)
                )
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_NOT_ACTIVE)
        }

        @Test
        @DisplayName("유효 기간 시작일 이전이면 LICENSE_PERIOD_NOT_STARTED 예외가 발생한다")
        fun `throw exception when date is before period start`() {
            assertThatThrownBy {
                LicensePolicy.validateUsable(
                    status = LicenseStatus.ACTIVE,
                    deleted = false,
                    period = defaultPeriod,
                    date = LocalDate.of(2024, 12, 31)
                )
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_PERIOD_NOT_STARTED)
        }

        @Test
        @DisplayName("유효 기간 종료일 이후면 LICENSE_PERIOD_ENDED 예외가 발생한다")
        fun `throw exception when date is after period end`() {
            assertThatThrownBy {
                LicensePolicy.validateUsable(
                    status = LicenseStatus.ACTIVE,
                    deleted = false,
                    period = defaultPeriod,
                    date = LocalDate.of(2026, 1, 1)
                )
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_PERIOD_ENDED)
        }

        @Test
        @DisplayName("시작일에는 사용 가능하다")
        fun `license is usable on start date`() {
            assertThatCode {
                LicensePolicy.validateUsable(
                    status = LicenseStatus.ACTIVE,
                    deleted = false,
                    period = defaultPeriod,
                    date = LocalDate.of(2025, 1, 1)
                )
            }.doesNotThrowAnyException()
        }

        @Test
        @DisplayName("종료일에는 사용 가능하다")
        fun `license is usable on end date`() {
            assertThatCode {
                LicensePolicy.validateUsable(
                    status = LicenseStatus.ACTIVE,
                    deleted = false,
                    period = defaultPeriod,
                    date = LocalDate.of(2025, 12, 31)
                )
            }.doesNotThrowAnyException()
        }

        @Test
        @DisplayName("삭제 검증이 가장 먼저 수행된다")
        fun `deleted validation has highest priority`() {
            // 삭제 + 비활성 + 기간 외 → 삭제 예외가 먼저
            assertThatThrownBy {
                LicensePolicy.validateUsable(
                    status = LicenseStatus.EXPIRED,
                    deleted = true,
                    period = defaultPeriod,
                    date = LocalDate.of(2026, 1, 1)
                )
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_ALREADY_DELETED)
        }
    }

    @Nested
    @DisplayName("validateExpiration")
    inner class ValidateExpiration {

        @Test
        @DisplayName("ACTIVE 상태이고 삭제되지 않은 라이선스는 검증을 통과한다")
        fun `active and not deleted license passes validation`() {
            assertThatCode {
                LicensePolicy.validateExpiration(LicenseStatus.ACTIVE, deleted = false)
            }.doesNotThrowAnyException()
        }

        @Test
        @DisplayName("이미 만료된 라이선스는 ALREADY_EXPIRED 예외가 발생한다")
        fun `throw exception when license is already expired`() {
            assertThatThrownBy {
                LicensePolicy.validateExpiration(LicenseStatus.EXPIRED, deleted = false)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.ALREADY_EXPIRED)
        }

        @Test
        @DisplayName("삭제된 라이선스는 LICENSE_ALREADY_DELETED 예외가 발생한다")
        fun `throw exception when license is deleted`() {
            assertThatThrownBy {
                LicensePolicy.validateExpiration(LicenseStatus.ACTIVE, deleted = true)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_ALREADY_DELETED)
        }

        @Test
        @DisplayName("삭제된 상태가 만료 상태보다 먼저 검증된다")
        fun `deleted validation has priority over expired validation`() {
            // 삭제되고 만료된 라이선스 → 삭제 예외가 먼저 발생
            assertThatThrownBy {
                LicensePolicy.validateExpiration(LicenseStatus.EXPIRED, deleted = true)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_ALREADY_DELETED)
        }
    }

    @Nested
    @DisplayName("validateDeletion")
    inner class ValidateDeletion {

        @Test
        @DisplayName("삭제되지 않은 라이선스는 검증을 통과한다")
        fun `not deleted license passes validation`() {
            assertThatCode {
                LicensePolicy.validateDeletion(deleted = false)
            }.doesNotThrowAnyException()
        }

        @Test
        @DisplayName("이미 삭제된 라이선스는 LICENSE_ALREADY_DELETED 예외가 발생한다")
        fun `throw exception when license is already deleted`() {
            assertThatThrownBy {
                LicensePolicy.validateDeletion(deleted = true)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_ALREADY_DELETED)
        }
    }
}
