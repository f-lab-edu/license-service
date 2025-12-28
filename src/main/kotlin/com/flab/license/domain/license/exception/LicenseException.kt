package com.flab.license.domain.license.exception

import com.flab.license.common.exception.CustomException

class LicenseException(errorCode: LicenseErrorCode) : CustomException(errorCode)
