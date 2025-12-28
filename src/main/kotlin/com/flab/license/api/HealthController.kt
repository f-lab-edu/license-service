package com.flab.license.api

import com.flab.license.common.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/health")
    fun health(): ApiResponse<Unit> = ApiResponse.ok()
}
