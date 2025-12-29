package com.flab.license.domain.plan.exception

import com.flab.license.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class PlanErrorCode(
    override val httpStatus: HttpStatus,
    override val message: String
) : ErrorCode {

    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "플랜을 찾을 수 없습니다"),
    INVALID_PLAN_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 플랜 ID입니다"),
    INVALID_MAX_SEATS(HttpStatus.BAD_REQUEST, "최대 좌석 수는 0 이상이어야 합니다"),
    INVALID_MONTHLY_TOKEN_LIMIT(HttpStatus.BAD_REQUEST, "월별 토큰 한도는 0 이상이어야 합니다"),
    PLAN_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 플랜입니다")
}
