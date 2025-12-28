package com.flab.license.common.exception

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.validation.BindException
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.*
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()
    private val objectMapper = ObjectMapper()
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(SampleController())
            .setControllerAdvice(handler)
            .setValidator(LocalValidatorFactoryBean())
            .build()
    }

    @Test
    @DisplayName("CustomException 발생 시 ErrorCode에 정의된 상태코드와 메시지를 반환한다")
    fun `handleCustomException returns error code status and message`() {
        // Given
        val exception = CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)

        // When
        val response = handler.handleCustomException(exception)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        val body = checkNotNull(response.body)
        assertThat(body.error).isEqualTo("RESOURCE_NOT_FOUND")
        assertThat(body.message).isEqualTo("요청한 리소스를 찾을 수 없습니다")
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 발생 시 400 상태코드와 INVALID_INPUT을 반환한다")
    fun `handleMethodArgumentNotValidException returns 400 with invalid input`() {
        // Given
        val exception = mock<MethodArgumentNotValidException>()

        // When
        val response = handler.handleMethodArgumentNotValidException(exception)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        val body = checkNotNull(response.body)
        assertThat(body.error).isEqualTo("INVALID_INPUT")
    }

    @Test
    @DisplayName("HttpMessageNotReadableException 발생 시 400 상태코드와 INVALID_JSON을 반환한다")
    fun `handleHttpMessageNotReadableException returns 400 with invalid json`() {
        // Given
        val exception = mock<HttpMessageNotReadableException>()

        // When
        val response = handler.handleHttpMessageNotReadableException(exception)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        val body = checkNotNull(response.body)
        assertThat(body.error).isEqualTo("INVALID_JSON")
    }

    @Test
    @DisplayName("MissingServletRequestParameterException 발생 시 400 상태코드와 MISSING_PARAMETER를 반환한다")
    fun `handleMissingServletRequestParameterException returns 400 with missing parameter`() {
        // Given
        val exception = MissingServletRequestParameterException("name", "String")

        // When
        val response = handler.handleMissingServletRequestParameterException(exception)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        val body = checkNotNull(response.body)
        assertThat(body.error).isEqualTo("MISSING_PARAMETER")
    }

    @Test
    @DisplayName("BindException 발생 시 400 상태코드와 INVALID_INPUT을 반환한다")
    fun `handleBindException returns 400 with invalid input`() {
        // Given
        val exception = BindException(Any(), "target")

        // When
        val response = handler.handleBindException(exception)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        val body = checkNotNull(response.body)
        assertThat(body.error).isEqualTo("INVALID_INPUT")
    }

    @Test
    @DisplayName("MethodArgumentTypeMismatchException 발생 시 400 상태코드와 TYPE_MISMATCH를 반환한다")
    fun `handleMethodArgumentTypeMismatchException returns 400 with type mismatch`() {
        // Given
        val exception = mock<MethodArgumentTypeMismatchException> {
            on { name }.thenReturn("id")
            on { value }.thenReturn("abc")
        }

        // When
        val response = handler.handleMethodArgumentTypeMismatchException(exception)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        val body = checkNotNull(response.body)
        assertThat(body.error).isEqualTo("TYPE_MISMATCH")
    }

    @Test
    @DisplayName("HttpRequestMethodNotSupportedException 발생 시 405 상태코드와 METHOD_NOT_ALLOWED를 반환한다")
    fun `handleHttpRequestMethodNotSupportedException returns 405 with method not allowed`() {
        // Given
        val exception = HttpRequestMethodNotSupportedException("DELETE")

        // When
        val response = handler.handleHttpRequestMethodNotSupportedException(exception)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
        val body = checkNotNull(response.body)
        assertThat(body.error).isEqualTo("METHOD_NOT_ALLOWED")
    }

    @Test
    @DisplayName("NoHandlerFoundException 발생 시 404 상태코드와 RESOURCE_NOT_FOUND를 반환한다")
    fun `handleNoHandlerFoundException returns 404 with resource not found`() {
        // Given
        val exception = NoHandlerFoundException("GET", "/unknown", HttpHeaders())

        // When
        val response = handler.handleNoHandlerFoundException(exception)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        val body = checkNotNull(response.body)
        assertThat(body.error).isEqualTo("RESOURCE_NOT_FOUND")
    }

    @Test
    @DisplayName("처리되지 않은 예외 발생 시 500 상태코드와 INTERNAL_ERROR를 반환한다")
    fun `handleException returns 500 with internal error`() {
        // Given
        val exception = RuntimeException("Unexpected error")

        // When
        val response = handler.handleException(exception)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        val body = checkNotNull(response.body)
        assertThat(body.error).isEqualTo("INTERNAL_ERROR")
    }

    @Test
    @DisplayName("MockMvc - @Valid 실패 시 INVALID_INPUT 에러로 응답한다")
    fun `mockMvc should return invalid input for validation errors`() {
        val payload = objectMapper.writeValueAsString(SampleRequest("", 0))

        mockMvc.post("/samples") {
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error") { value("INVALID_INPUT") }
        }
    }

    @Test
    @DisplayName("MockMvc - 잘못된 JSON이면 INVALID_JSON 에러로 응답한다")
    fun `mockMvc should return invalid json for unreadable body`() {
        mockMvc.post("/samples") {
            contentType = MediaType.APPLICATION_JSON
            content = "{invalid}"
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error") { value("INVALID_JSON") }
        }
    }

    @Test
    @DisplayName("MockMvc - 요청 파라미터 누락 시 MISSING_PARAMETER 에러로 응답한다")
    fun `mockMvc should return missing parameter`() {
        mockMvc.get("/missing")
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.error") { value("MISSING_PARAMETER") }
            }
    }

    @Test
    @DisplayName("MockMvc - 타입 불일치 시 TYPE_MISMATCH 에러로 응답한다")
    fun `mockMvc should return type mismatch`() {
        mockMvc.get("/samples") {
            param("id", "abc")
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error") { value("TYPE_MISMATCH") }
        }
    }

    @RestController
    class SampleController {

        @PostMapping("/samples")
        fun createSample(@Valid @RequestBody request: SampleRequest) {
            // no-op
        }

        @GetMapping("/missing")
        fun missingParam(@RequestParam(name = "requiredParam") param: String) {
            // no-op
        }

        @GetMapping("/samples")
        fun getSample(@RequestParam id: Long) {
            // no-op
        }
    }

    data class SampleRequest(
        @field:NotBlank val name: String,
        @field:Min(1) val count: Int
    )
}
