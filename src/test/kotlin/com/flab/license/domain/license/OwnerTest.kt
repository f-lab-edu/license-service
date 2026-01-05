package com.flab.license.domain.license

import com.flab.license.domain.license.exception.LicenseErrorCode
import com.flab.license.domain.license.exception.LicenseException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class OwnerTest {

    @Nested
    @DisplayName("user")
    inner class User {

        @Test
        @DisplayName("USER 타입 Owner를 생성할 수 있다")
        fun `create USER owner successfully`() {
            // When
            val owner = Owner.user("user-123")

            // Then
            assertThat(owner.type).isEqualTo(Owner.Type.USER)
            assertThat(owner.id).isEqualTo("user-123")
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "  "])
        @DisplayName("id가 빈 값이면 예외가 발생한다")
        fun `throw exception when id is blank`(blankId: String) {
            // When & Then
            assertThatThrownBy {
                Owner.user(blankId)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.INVALID_OWNER_ID)
        }
    }

    @Nested
    @DisplayName("organization")
    inner class Organization {

        @Test
        @DisplayName("ORGANIZATION 타입 Owner를 생성할 수 있다")
        fun `create ORGANIZATION owner successfully`() {
            // When
            val owner = Owner.organization("org-456")

            // Then
            assertThat(owner.type).isEqualTo(Owner.Type.ORGANIZATION)
            assertThat(owner.id).isEqualTo("org-456")
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "  "])
        @DisplayName("id가 빈 값이면 예외가 발생한다")
        fun `throw exception when id is blank`(blankId: String) {
            // When & Then
            assertThatThrownBy {
                Owner.organization(blankId)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.INVALID_OWNER_ID)
        }
    }

    @Nested
    @DisplayName("reconstitute")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 Owner를 복원할 수 있다")
        fun `reconstitute owner successfully`() {
            // When
            val owner = Owner.reconstitute(Owner.Type.USER, "user-123")

            // Then
            assertThat(owner.type).isEqualTo(Owner.Type.USER)
            assertThat(owner.id).isEqualTo("user-123")
        }

        @Test
        @DisplayName("reconstitute는 검증을 건너뛴다 - 빈 id도 복원 가능")
        fun `reconstitute skips validation for blank id`() {
            // When & Then
            assertThatCode {
                Owner.reconstitute(Owner.Type.USER, "")
            }.doesNotThrowAnyException()
        }
    }
}
