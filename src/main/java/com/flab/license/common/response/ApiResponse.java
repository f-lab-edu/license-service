package com.flab.license.common.response;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flab.license.common.exception.ErrorCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
	boolean success,
	String error,
	String message,
	T data
) {
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, null, null, data);
	}

	public static ApiResponse<Void> ok() {
		return new ApiResponse<>(true, null, null, null);
	}

	public static ApiResponse<Void> error(@NonNull ErrorCode errorCode) {
		return new ApiResponse<>(false, errorCode.name(), errorCode.getMessage(), null);
	}
}
