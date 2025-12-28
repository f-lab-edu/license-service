package com.flab.license.common.exception;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.license.common.response.ApiResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

class GlobalExceptionHandlerTest {

	private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new SampleController())
			.setControllerAdvice(handler)
			.setValidator(new LocalValidatorFactoryBean())
			.build();
	}

	@Test
	@DisplayName("CustomException 발생 시 ErrorCode에 정의된 상태코드와 메시지를 반환한다")
	void handleCustomException_ReturnsErrorCodeStatusAndMessage() {
		// Given
		CustomException exception = new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND);

		// When
		ResponseEntity<ApiResponse<Void>> response = handler.handleCustomException(exception);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody())
			.isNotNull()
			.satisfies(body -> {
				assertThat(body.error()).isEqualTo("RESOURCE_NOT_FOUND");
				assertThat(body.message()).isEqualTo("요청한 리소스를 찾을 수 없습니다");
			});
	}

	@Test
	@DisplayName("MethodArgumentNotValidException 발생 시 400 상태코드와 INVALID_INPUT을 반환한다")
	void handleMethodArgumentNotValidException_Returns400WithInvalidInput() {
		// Given
		MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

		// When
		ResponseEntity<ApiResponse<Void>> response = handler.handleMethodArgumentNotValidException(exception);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody())
			.isNotNull()
			.extracting(ApiResponse::error)
			.isEqualTo("INVALID_INPUT");
	}

	@Test
	@DisplayName("HttpMessageNotReadableException 발생 시 400 상태코드와 INVALID_JSON을 반환한다")
	void handleHttpMessageNotReadableException_Returns400WithInvalidJson() {
		// Given
		HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

		// When
		ResponseEntity<ApiResponse<Void>> response = handler.handleHttpMessageNotReadableException(exception);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody())
			.isNotNull()
			.extracting(ApiResponse::error)
			.isEqualTo("INVALID_JSON");
	}

	@Test
	@DisplayName("MissingServletRequestParameterException 발생 시 400 상태코드와 MISSING_PARAMETER를 반환한다")
	void handleMissingServletRequestParameterException_Returns400WithMissingParameter() {
		// Given
		MissingServletRequestParameterException exception = new MissingServletRequestParameterException("name", "String");

		// When
		ResponseEntity<ApiResponse<Void>> response = handler.handleMissingServletRequestParameterException(exception);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody())
			.isNotNull()
			.extracting(ApiResponse::error)
			.isEqualTo("MISSING_PARAMETER");
	}

	@Test
	@DisplayName("BindException 발생 시 400 상태코드와 INVALID_INPUT을 반환한다")
	void handleBindException_Returns400WithInvalidInput() {
		// Given
		BindException exception = new BindException(new Object(), "target");

		// When
		ResponseEntity<ApiResponse<Void>> response = handler.handleBindException(exception);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody())
			.isNotNull()
			.extracting(ApiResponse::error)
			.isEqualTo("INVALID_INPUT");
	}

	@Test
	@DisplayName("MethodArgumentTypeMismatchException 발생 시 400 상태코드와 TYPE_MISMATCH를 반환한다")
	void handleMethodArgumentTypeMismatchException_Returns400WithTypeMismatch() {
		// Given
		MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException("abc", Long.class, "id", null, null);

		// When
		ResponseEntity<ApiResponse<Void>> response = handler.handleMethodArgumentTypeMismatchException(exception);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody())
			.isNotNull()
			.extracting(ApiResponse::error)
			.isEqualTo("TYPE_MISMATCH");
	}

	@Test
	@DisplayName("HttpRequestMethodNotSupportedException 발생 시 405 상태코드와 METHOD_NOT_ALLOWED를 반환한다")
	void handleHttpRequestMethodNotSupportedException_Returns405WithMethodNotAllowed() {
		// Given
		HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("DELETE");

		// When
		ResponseEntity<ApiResponse<Void>> response = handler.handleHttpRequestMethodNotSupportedException(exception);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
		assertThat(response.getBody())
			.isNotNull()
			.extracting(ApiResponse::error)
			.isEqualTo("METHOD_NOT_ALLOWED");
	}

	@Test
	@DisplayName("NoHandlerFoundException 발생 시 404 상태코드와 RESOURCE_NOT_FOUND를 반환한다")
	void handleNoHandlerFoundException_Returns404WithResourceNotFound() {
		// Given
		NoHandlerFoundException exception = new NoHandlerFoundException("GET", "/unknown", null);

		// When
		ResponseEntity<ApiResponse<Void>> response = handler.handleNoHandlerFoundException(exception);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody())
			.isNotNull()
			.extracting(ApiResponse::error)
			.isEqualTo("RESOURCE_NOT_FOUND");
	}

	@Test
	@DisplayName("처리되지 않은 예외 발생 시 500 상태코드와 INTERNAL_ERROR를 반환한다")
	void handleException_Returns500WithInternalError() {
		// Given
		Exception exception = new RuntimeException("Unexpected error");

		// When
		ResponseEntity<ApiResponse<Void>> response = handler.handleException(exception);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody())
			.isNotNull()
			.extracting(ApiResponse::error)
			.isEqualTo("INTERNAL_ERROR");
	}

	@Test
	@DisplayName("MockMvc - @Valid 실패 시 INVALID_INPUT 에러로 응답한다")
	void mockMvc_ShouldReturnInvalidInputForValidationErrors() throws Exception {
		String payload = objectMapper.writeValueAsString(new SampleRequest("", 0));

		mockMvc.perform(post("/samples")
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("INVALID_INPUT"));
	}

	@Test
	@DisplayName("MockMvc - 잘못된 JSON이면 INVALID_JSON 에러로 응답한다")
	void mockMvc_ShouldReturnInvalidJsonForUnreadableBody() throws Exception {
		mockMvc.perform(post("/samples")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{invalid}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("INVALID_JSON"));
	}

	@Test
	@DisplayName("MockMvc - 요청 파라미터 누락 시 MISSING_PARAMETER 에러로 응답한다")
	void mockMvc_ShouldReturnMissingParameter() throws Exception {
		mockMvc.perform(get("/missing"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("MISSING_PARAMETER"));
	}

	@Test
	@DisplayName("MockMvc - 타입 불일치 시 TYPE_MISMATCH 에러로 응답한다")
	void mockMvc_ShouldReturnTypeMismatch() throws Exception {
		mockMvc.perform(get("/samples").param("id", "abc"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("TYPE_MISMATCH"));
	}

	@RestController
	static class SampleController {

		@PostMapping("/samples")
		public void createSample(@Valid @RequestBody SampleRequest request) {
			// no-op
		}

		@GetMapping("/missing")
		public void missingParam(@RequestParam(name = "requiredParam") String param) {
			// no-op
		}

		@GetMapping("/samples")
		public void getSample(@RequestParam Long id) {
			// no-op
		}
	}

	record SampleRequest(
		@NotBlank String name,
		@Min(1) Integer count
	) {
	}
}
