package com.flab.license.common.response

import com.fasterxml.jackson.databind.ObjectMapper
import com.flab.license.common.exception.CommonErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ApiResponseTest {

    private val objectMapper = ObjectMapper()

    @Test
    @DisplayName("success() 호출 시 success가 true이고 data가 설정된다")
    fun `success returns true with data`() {
        // When
        val response = ApiResponse.success("test data")

        // Then
        assertThat(response).satisfies({ r ->
            assertThat(r.success).isTrue()
            assertThat(r.data).isEqualTo("test data")
            assertThat(r.message).isNull()
        })
    }

    @Test
    @DisplayName("ok() 호출 시 success가 true이고 data가 null이다")
    fun `ok returns true with null data`() {
        // When
        val response = ApiResponse.ok()

        // Then
        assertThat(response).satisfies({ r ->
            assertThat(r.success).isTrue()
            assertThat(r.data).isNull()
        })
    }

    @Test
    @DisplayName("실패 응답을 생성할 수 있다")
    fun `fail response can be created`() {
        // When
        val response = ApiResponse<Unit>(false, "LICENSE_NOT_FOUND", "라이선스를 찾을 수 없습니다", null)

        // Then
        assertThat(response.success).isFalse()
        assertThat(response.error).isEqualTo("LICENSE_NOT_FOUND")
        assertThat(response.message).isEqualTo("라이선스를 찾을 수 없습니다")
        assertThat(response.data).isNull()
    }

    @Test
    @DisplayName("JSON 직렬화 시 null 필드는 제외된다")
    fun `json serialization excludes null fields`() {
        // Given
        val response = ApiResponse.success("data")

        // When
        val json = objectMapper.writeValueAsString(response)

        // Then
        assertThat(json)
            .contains("\"success\":true")
            .contains("\"data\":\"data\"")
            .doesNotContain("\"error\"")
            .doesNotContain("\"message\"")
    }

    @Test
    @DisplayName("ok() JSON 직렬화 시 success만 포함된다")
    fun `ok json serialization only contains success`() {
        // Given
        val response = ApiResponse.ok()

        // When
        val json = objectMapper.writeValueAsString(response)

        // Then
        assertThat(json).isEqualTo("{\"success\":true}")
    }

    @Test
    @DisplayName("error() 호출 시 ErrorCode로 에러 응답을 생성한다")
    fun `error creates error response`() {
        // When
        val response = ApiResponse.error(CommonErrorCode.INVALID_JSON)

        // Then
        assertThat(response.success).isFalse()
        assertThat(response.error).isEqualTo("INVALID_JSON")
        assertThat(response.message).isEqualTo(CommonErrorCode.INVALID_JSON.message)
        assertThat(response.data).isNull()
    }
}
