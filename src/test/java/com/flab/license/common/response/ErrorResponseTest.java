package com.flab.license.common.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.flab.license.common.exception.CommonErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

	@Test
	@DisplayName("ErrorCode로 에러 응답을 생성할 수 있다")
	void createErrorResponse_FromErrorCode() {
		// When
		ErrorResponse response = ErrorResponse.of(CommonErrorCode.INVALID_JSON);

		// Then
		assertThat(response.code()).isEqualTo("INVALID_JSON");
		assertThat(response.message()).isEqualTo(CommonErrorCode.INVALID_JSON.getMessage());
	}
}
