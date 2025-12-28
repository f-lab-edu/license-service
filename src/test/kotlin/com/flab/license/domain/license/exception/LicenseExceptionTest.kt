package com.flab.license.domain.license.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class LicenseExceptionTest {

    @Test
    @DisplayName("LicenseException 생성 시 LicenseErrorCode의 메시지를 예외 메시지로 사용한다")
    fun `constructor uses error code message`() {
        // Given
        val errorCode = LicenseErrorCode.LICENSE_NOT_FOUND

        // When
        val exception = LicenseException(errorCode)

        // Then
        assertThat(exception).satisfies({ e ->
            assertThat(e.message).isEqualTo("라이선스를 찾을 수 없습니다")
            assertThat(e.errorCode).isEqualTo(errorCode)
        })
    }
}
