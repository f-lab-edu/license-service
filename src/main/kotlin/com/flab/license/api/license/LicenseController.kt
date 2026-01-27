package com.flab.license.api.license

import com.flab.license.api.license.dto.CreateLicenseRequest
import com.flab.license.api.license.dto.GetLicensesByOwnerQuery
import com.flab.license.api.license.dto.LicenseResponse
import com.flab.license.common.response.ApiResponse
import com.flab.license.domain.license.LicenseId
import com.flab.license.service.license.command.LicenseCommandService
import com.flab.license.service.license.query.LicenseQueryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/licenses")
class LicenseController(
    private val licenseCommandService: LicenseCommandService,
    private val licenseQueryService: LicenseQueryService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateLicenseRequest): ApiResponse<LicenseResponse> {
        val license = licenseCommandService.create(request.toCommand())
        return ApiResponse.success(LicenseResponse.from(license))
    }

    @GetMapping("/{licenseId}")
    fun getById(@PathVariable licenseId: UUID): ApiResponse<LicenseResponse> {
        val license = licenseQueryService.getById(LicenseId(licenseId))
        return ApiResponse.success(LicenseResponse.from(license))
    }

    @GetMapping
    fun getByOwner(
        @Valid query: GetLicensesByOwnerQuery
    ): ApiResponse<List<LicenseResponse>> {
        val licenses = licenseQueryService.getByOwner(query.toOwner())
        return ApiResponse.success(licenses.map { LicenseResponse.from(it) })
    }

    @PostMapping("/{licenseId}/expire")
    fun expire(@PathVariable licenseId: UUID): ApiResponse<LicenseResponse> {
        val license = licenseCommandService.expire(LicenseId(licenseId))
        return ApiResponse.success(LicenseResponse.from(license))
    }

    @DeleteMapping("/{licenseId}")
    fun delete(@PathVariable licenseId: UUID): ApiResponse<Unit> {
        licenseCommandService.delete(LicenseId(licenseId))
        return ApiResponse.ok()
    }
}
