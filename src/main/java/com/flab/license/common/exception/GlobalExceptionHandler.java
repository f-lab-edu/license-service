package com.flab.license.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.validation.BindException;

import com.flab.license.common.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
		ErrorCode errorCode = e.getErrorCode();
		log.warn("Custom exception: error={}, message={}", errorCode.name(), errorCode.getMessage());

		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ApiResponse.error(errorCode));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.warn("Validation failed: {}", e.getMessage());

		return ResponseEntity
			.status(CommonErrorCode.INVALID_INPUT.getHttpStatus())
			.body(ApiResponse.error(CommonErrorCode.INVALID_INPUT));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		log.warn("Message not readable: {}", e.getMessage());

		return ResponseEntity
			.status(CommonErrorCode.INVALID_JSON.getHttpStatus())
			.body(ApiResponse.error(CommonErrorCode.INVALID_JSON));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		log.warn("Missing parameter: {}", e.getParameterName());

		return ResponseEntity
			.status(CommonErrorCode.MISSING_PARAMETER.getHttpStatus())
			.body(ApiResponse.error(CommonErrorCode.MISSING_PARAMETER));
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
		log.warn("Bind exception: {}", e.getMessage());

		return ResponseEntity
			.status(CommonErrorCode.INVALID_INPUT.getHttpStatus())
			.body(ApiResponse.error(CommonErrorCode.INVALID_INPUT));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		log.warn("Type mismatch: parameter={}, value={}", e.getName(), e.getValue());

		return ResponseEntity
			.status(CommonErrorCode.TYPE_MISMATCH.getHttpStatus())
			.body(ApiResponse.error(CommonErrorCode.TYPE_MISMATCH));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		log.warn("Method not supported: {}", e.getMethod());

		return ResponseEntity
			.status(CommonErrorCode.METHOD_NOT_ALLOWED.getHttpStatus())
			.body(ApiResponse.error(CommonErrorCode.METHOD_NOT_ALLOWED));
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
		log.warn("No handler found: {} {}", e.getHttpMethod(), e.getRequestURL());

		return ResponseEntity
			.status(CommonErrorCode.RESOURCE_NOT_FOUND.getHttpStatus())
			.body(ApiResponse.error(CommonErrorCode.RESOURCE_NOT_FOUND));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		log.error("Unexpected error: type={}, message={}", e.getClass().getSimpleName(), e.getMessage(), e);

		return ResponseEntity
			.status(CommonErrorCode.INTERNAL_ERROR.getHttpStatus())
			.body(ApiResponse.error(CommonErrorCode.INTERNAL_ERROR));
	}
}
