package com.flab.license.api.plan

import com.flab.license.api.plan.dto.CreatePlanRequest
import com.flab.license.api.plan.dto.PlanResponse
import com.flab.license.api.plan.dto.UpdatePlanRequest
import com.flab.license.common.response.ApiResponse
import com.flab.license.domain.plan.PlanId
import com.flab.license.service.plan.command.PlanCommandService
import com.flab.license.service.plan.query.PlanQueryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/plans")
class PlanController(
    private val planCommandService: PlanCommandService,
    private val planQueryService: PlanQueryService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreatePlanRequest): ApiResponse<PlanResponse> {
        val plan = planCommandService.create(request.toCommand())
        return ApiResponse.success(PlanResponse.from(plan))
    }

    @GetMapping
    fun getAll(): ApiResponse<List<PlanResponse>> {
        val plans = planQueryService.getAll()
        return ApiResponse.success(plans.map { PlanResponse.from(it) })
    }

    @GetMapping("/{planId}")
    fun getById(@PathVariable planId: UUID): ApiResponse<PlanResponse> {
        val plan = planQueryService.getById(PlanId(planId))
        return ApiResponse.success(PlanResponse.from(plan))
    }

    @PutMapping("/{planId}")
    fun update(
        @PathVariable planId: UUID,
        @Valid @RequestBody request: UpdatePlanRequest
    ): ApiResponse<PlanResponse> {
        val plan = planCommandService.update(request.toCommand(PlanId(planId)))
        return ApiResponse.success(PlanResponse.from(plan))
    }

    @DeleteMapping("/{planId}")
    fun delete(@PathVariable planId: UUID): ApiResponse<Unit> {
        planCommandService.delete(PlanId(planId))
        return ApiResponse.ok()
    }
}
