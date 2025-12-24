package com.flab.license.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
	boolean success,
	String code,
	String message,
	T data
) {
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, null, null, data);
	}

	public static ApiResponse<Void> ok() {
		return new ApiResponse<>(true, null, null, null);
	}
}
