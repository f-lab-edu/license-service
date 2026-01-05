package com.flab.license.domain.license.exception

import com.flab.license.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class LicenseErrorCode(
    override val httpStatus: HttpStatus,
    override val message: String
) : ErrorCode {
    LICENSE_NOT_FOUND(HttpStatus.NOT_FOUND, "라이선스를 찾을 수 없습니다"),
    INVALID_PERIOD(HttpStatus.BAD_REQUEST, "종료일은 시작일 이후여야 합니다"),
    LICENSE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 라이선스입니다"),
    ALREADY_EXPIRED(HttpStatus.BAD_REQUEST, "이미 만료된 라이선스입니다"),
    INVALID_OWNER_ID(HttpStatus.BAD_REQUEST, "소유자 ID는 빈 값일 수 없습니다"),
    LICENSE_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "활성 상태의 라이선스가 아닙니다"),
    LICENSE_PERIOD_NOT_STARTED(HttpStatus.BAD_REQUEST, "라이선스 유효 기간이 시작되지 않았습니다"),
    LICENSE_PERIOD_ENDED(HttpStatus.BAD_REQUEST, "라이선스 유효 기간이 종료되었습니다")
}
