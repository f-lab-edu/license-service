package com.flab.license.common.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class CustomExceptionTest {

    @Test
    @DisplayName("ErrorCode로 CustomException 생성 시 message와 errorCode가 설정된다")
    fun `constructor sets message and errorCode`() {
        // Given
        val errorCode: ErrorCode = CommonErrorCode.RESOURCE_NOT_FOUND

        // When
        val exception = CustomException(errorCode)

        // Then
        assertThat(exception).satisfies({ e ->
            assertThat(e.message).isEqualTo("요청한 리소스를 찾을 수 없습니다")
            assertThat(e.errorCode).isEqualTo(errorCode)
            assertThat(e.errorCode.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
        })
    }
}
