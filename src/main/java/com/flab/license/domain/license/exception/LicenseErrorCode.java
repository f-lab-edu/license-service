package com.flab.license.domain.license.exception;

import org.springframework.http.HttpStatus;

import com.flab.license.common.exception.ErrorCode;

public enum LicenseErrorCode implements ErrorCode {

	LICENSE_NOT_FOUND(HttpStatus.NOT_FOUND, "라이선스를 찾을 수 없습니다");

	private final HttpStatus httpStatus;
	private final String message;

	LicenseErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
