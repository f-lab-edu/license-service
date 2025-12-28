package com.flab.license.common.exception

import com.flab.license.common.response.ApiResponse
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ApiResponse<Unit>> {
        val errorCode = e.errorCode
        log.warn { "Custom exception: error=${errorCode.name}, message=${errorCode.message}" }

        return ResponseEntity
            .status(errorCode.httpStatus)
            .body(ApiResponse.error(errorCode))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<ApiResponse<Unit>> {
        log.warn { "Validation failed: ${e.message}" }

        return ResponseEntity
            .status(CommonErrorCode.INVALID_INPUT.httpStatus)
            .body(ApiResponse.error(CommonErrorCode.INVALID_INPUT))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        e: HttpMessageNotReadableException
    ): ResponseEntity<ApiResponse<Unit>> {
        log.warn { "Message not readable: ${e.message}" }

        return ResponseEntity
            .status(CommonErrorCode.INVALID_JSON.httpStatus)
            .body(ApiResponse.error(CommonErrorCode.INVALID_JSON))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(
        e: MissingServletRequestParameterException
    ): ResponseEntity<ApiResponse<Unit>> {
        log.warn { "Missing parameter: ${e.parameterName}" }

        return ResponseEntity
            .status(CommonErrorCode.MISSING_PARAMETER.httpStatus)
            .body(ApiResponse.error(CommonErrorCode.MISSING_PARAMETER))
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException): ResponseEntity<ApiResponse<Unit>> {
        log.warn { "Bind exception: ${e.message}" }

        return ResponseEntity
            .status(CommonErrorCode.INVALID_INPUT.httpStatus)
            .body(ApiResponse.error(CommonErrorCode.INVALID_INPUT))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(
        e: MethodArgumentTypeMismatchException
    ): ResponseEntity<ApiResponse<Unit>> {
        log.warn { "Type mismatch: parameter=${e.name}, value=${e.value}" }

        return ResponseEntity
            .status(CommonErrorCode.TYPE_MISMATCH.httpStatus)
            .body(ApiResponse.error(CommonErrorCode.TYPE_MISMATCH))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(
        e: HttpRequestMethodNotSupportedException
    ): ResponseEntity<ApiResponse<Unit>> {
        log.warn { "Method not supported: ${e.method}" }

        return ResponseEntity
            .status(CommonErrorCode.METHOD_NOT_ALLOWED.httpStatus)
            .body(ApiResponse.error(CommonErrorCode.METHOD_NOT_ALLOWED))
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(
        e: NoHandlerFoundException
    ): ResponseEntity<ApiResponse<Unit>> {
        log.warn { "No handler found: ${e.httpMethod} ${e.requestURL}" }

        return ResponseEntity
            .status(CommonErrorCode.RESOURCE_NOT_FOUND.httpStatus)
            .body(ApiResponse.error(CommonErrorCode.RESOURCE_NOT_FOUND))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.error(e) { "Unexpected error: type=${e.javaClass.simpleName}, message=${e.message}" }

        return ResponseEntity
            .status(CommonErrorCode.INTERNAL_ERROR.httpStatus)
            .body(ApiResponse.error(CommonErrorCode.INTERNAL_ERROR))
    }
}
