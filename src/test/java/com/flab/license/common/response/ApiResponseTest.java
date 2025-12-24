package com.flab.license.common.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@DisplayName("success() 호출 시 success가 true이고 data가 설정된다")
	void success_ReturnsTrueWithData() {
		// When
		ApiResponse<String> response = ApiResponse.success("test data");

		// Then
		assertThat(response)
			.satisfies(r -> {
				assertThat(r.success()).isTrue();
				assertThat(r.data()).isEqualTo("test data");
				assertThat(r.message()).isNull();
			});
	}

	@Test
	@DisplayName("ok() 호출 시 success가 true이고 data가 null이다")
	void ok_ReturnsTrueWithNullData() {
		// When
		ApiResponse<Void> response = ApiResponse.ok();

		// Then
		assertThat(response)
			.satisfies(r -> {
				assertThat(r.success()).isTrue();
				assertThat(r.data()).isNull();
			});
	}

	@Test
	@DisplayName("실패 응답을 생성할 수 있다")
	void failResponse_CanBeCreated() {
		// When
		ApiResponse<Void> response = new ApiResponse<>(false, "LICENSE_NOT_FOUND", "라이선스를 찾을 수 없습니다", null);

		// Then
		assertThat(response.success()).isFalse();
		assertThat(response.code()).isEqualTo("LICENSE_NOT_FOUND");
		assertThat(response.message()).isEqualTo("라이선스를 찾을 수 없습니다");
		assertThat(response.data()).isNull();
	}

	@Test
	@DisplayName("JSON 직렬화 시 null 필드는 제외된다")
	void jsonSerialization_ExcludesNullFields() throws JsonProcessingException {
		// Given
		ApiResponse<String> response = ApiResponse.success("data");

		// When
		String json = objectMapper.writeValueAsString(response);

		// Then
		assertThat(json)
			.contains("\"success\":true")
			.contains("\"data\":\"data\"")
			.doesNotContain("\"code\"")
			.doesNotContain("\"message\"");
	}

	@Test
	@DisplayName("ok() JSON 직렬화 시 success만 포함된다")
	void okJsonSerialization_OnlyContainsSuccess() throws JsonProcessingException {
		// Given
		ApiResponse<Void> response = ApiResponse.ok();

		// When
		String json = objectMapper.writeValueAsString(response);

		// Then
		assertThat(json).isEqualTo("{\"success\":true}");
	}
}
