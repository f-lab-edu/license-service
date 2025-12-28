package com.flab.license.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flab.license.common.response.ApiResponse;

@RestController
public class HealthController {

	@GetMapping("/health")
	public ApiResponse<Void> health() {
		return ApiResponse.ok();
	}
}
