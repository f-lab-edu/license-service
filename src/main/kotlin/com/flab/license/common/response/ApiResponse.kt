package com.flab.license.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.flab.license.common.exception.ErrorCode

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val error: String? = null,
    val message: String? = null,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(success = true, data = data)

        fun ok(): ApiResponse<Unit> = ApiResponse(success = true)

        fun error(errorCode: ErrorCode): ApiResponse<Unit> = ApiResponse(
            success = false,
            error = errorCode.name,
            message = errorCode.message
        )
    }
}
