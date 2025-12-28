package com.flab.license.domain.license.exception

import com.flab.license.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class LicenseErrorCode(
    override val httpStatus: HttpStatus,
    override val message: String
) : ErrorCode {

    LICENSE_NOT_FOUND(HttpStatus.NOT_FOUND, "라이선스를 찾을 수 없습니다")
}
