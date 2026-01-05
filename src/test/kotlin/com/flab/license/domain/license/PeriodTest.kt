package com.flab.license.domain.license

import com.flab.license.domain.license.exception.LicenseErrorCode
import com.flab.license.domain.license.exception.LicenseException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PeriodTest {

    @Nested
    @DisplayName("of")
    inner class Of {

        @Test
        @DisplayName("유효한 기간을 생성할 수 있다")
        fun `create valid period`() {
            // Given
            val startDate = LocalDate.of(2025, 1, 1)
            val endDate = LocalDate.of(2025, 12, 31)

            // When
            val period = Period.of(startDate, endDate)

            // Then
            assertThat(period.startDate).isEqualTo(startDate)
            assertThat(period.endDate).isEqualTo(endDate)
        }

        @Test
        @DisplayName("시작일과 종료일이 같아도 생성할 수 있다")
        fun `create period with same start and end date`() {
            // Given
            val date = LocalDate.of(2025, 6, 15)

            // When
            val period = Period.of(date, date)

            // Then
            assertThat(period.startDate).isEqualTo(date)
            assertThat(period.endDate).isEqualTo(date)
        }

        @Test
        @DisplayName("종료일이 시작일보다 이전이면 예외가 발생한다")
        fun `throw exception when endDate is before startDate`() {
            // Given
            val startDate = LocalDate.of(2025, 12, 31)
            val endDate = LocalDate.of(2025, 1, 1)

            // When & Then
            assertThatThrownBy { Period.of(startDate, endDate) }
                .isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.INVALID_PERIOD)
        }
    }

    @Nested
    @DisplayName("reconstitute")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 기간을 검증 없이 복원할 수 있다")
        fun `reconstitute period without validation`() {
            // Given
            val startDate = LocalDate.of(2025, 1, 1)
            val endDate = LocalDate.of(2025, 12, 31)

            // When
            val period = Period.reconstitute(startDate, endDate)

            // Then
            assertThat(period.startDate).isEqualTo(startDate)
            assertThat(period.endDate).isEqualTo(endDate)
        }

        @Test
        @DisplayName("복원 시 검증을 수행하지 않는다 - 잘못된 데이터도 복원 가능")
        fun `reconstitute skips validation for invalid data`() {
            // Given - 잘못된 데이터 (endDate < startDate)
            val startDate = LocalDate.of(2025, 12, 31)
            val endDate = LocalDate.of(2025, 1, 1)

            // When - 복원은 성공
            assertThatCode {
                Period.reconstitute(startDate, endDate)
            }.doesNotThrowAnyException()
        }
    }

    @Nested
    @DisplayName("equals")
    inner class Equals {

        @Test
        @DisplayName("같은 값을 가진 Period는 동일하다")
        fun `periods with same values are equal`() {
            // Given
            val period1 = Period.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
            )
            val period2 = Period.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
            )

            // When & Then
            assertThat(period1).isEqualTo(period2)
        }

        @Test
        @DisplayName("다른 값을 가진 Period는 동일하지 않다")
        fun `periods with different values are not equal`() {
            // Given
            val period1 = Period.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
            )
            val period2 = Period.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 12, 31)
            )

            // When & Then
            assertThat(period1).isNotEqualTo(period2)
        }
    }
}
