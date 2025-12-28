package com.flab.license.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class CustomExceptionTest {

	@Test
	@DisplayName("ErrorCode로 CustomException 생성 시 message와 errorCode가 설정된다")
	void constructor_SetsMessageAndErrorCode() {
		// Given
		ErrorCode errorCode = CommonErrorCode.RESOURCE_NOT_FOUND;

		// When
		CustomException exception = new CustomException(errorCode);

		// Then
		assertThat(exception)
			.satisfies(e -> {
				assertThat(e.getMessage()).isEqualTo("요청한 리소스를 찾을 수 없습니다");
				assertThat(e.getErrorCode()).isEqualTo(errorCode);
				assertThat(e.getErrorCode().getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
			});
	}
}
