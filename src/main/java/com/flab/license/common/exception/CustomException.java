package com.flab.license.common.exception;

import org.springframework.lang.NonNull;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
	private final transient ErrorCode errorCode;

	public CustomException(@NonNull ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
