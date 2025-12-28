package com.flab.license.common.exception

open class CustomException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)
