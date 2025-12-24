package com.flab.license.common.response;

import org.springframework.lang.NonNull;

import com.flab.license.common.exception.ErrorCode;

public record ErrorResponse(
	String code,
	String message
) {
	public static ErrorResponse of(@NonNull ErrorCode errorCode) {
		return new ErrorResponse(errorCode.name(), errorCode.getMessage());
	}
}
