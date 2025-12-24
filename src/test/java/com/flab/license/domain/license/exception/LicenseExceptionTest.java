package com.flab.license.domain.license.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseExceptionTest {

	@Test
	@DisplayName("LicenseException 생성 시 LicenseErrorCode의 메시지를 예외 메시지로 사용한다")
	void constructor_UsesErrorCodeMessage() {
		// Given
		LicenseErrorCode errorCode = LicenseErrorCode.LICENSE_NOT_FOUND;

		// When
		LicenseException exception = new LicenseException(errorCode);

		// Then
		assertThat(exception)
			.satisfies(e -> {
				assertThat(e.getMessage()).isEqualTo("라이선스를 찾을 수 없습니다");
				assertThat(e.getErrorCode()).isEqualTo(errorCode);
			});
	}
}
