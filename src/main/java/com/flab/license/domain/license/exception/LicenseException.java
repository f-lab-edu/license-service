package com.flab.license.domain.license.exception;

import com.flab.license.common.exception.CustomException;

public class LicenseException extends CustomException {

	public LicenseException(LicenseErrorCode errorCode) {
		super(errorCode);
	}
}
