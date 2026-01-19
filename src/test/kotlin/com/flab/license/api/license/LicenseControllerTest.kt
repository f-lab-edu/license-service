package com.flab.license.api.license

import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.flab.license.api.AbstractRestDocsTest
import com.flab.license.api.CommonResponseFields.success
import com.flab.license.api.license.dto.CreateLicenseRequest
import com.flab.license.domain.license.License
import com.flab.license.domain.license.LicenseRepository
import com.flab.license.domain.license.Owner
import com.flab.license.domain.license.Period
import com.flab.license.domain.plan.Plan
import com.flab.license.domain.plan.PlanCode
import com.flab.license.domain.plan.PlanRepository
import com.flab.license.infra.persistence.license.jpa.LicenseSpringDataJpaRepository
import com.flab.license.infra.persistence.plan.jpa.PlanSpringDataJpaRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName as queryParamWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.util.UUID

@DisplayName("License API")
class LicenseControllerTest : AbstractRestDocsTest() {

    @Autowired
    private lateinit var licenseRepository: LicenseRepository

    @Autowired
    private lateinit var planRepository: PlanRepository

    @Autowired
    private lateinit var licenseSpringDataJpaRepository: LicenseSpringDataJpaRepository

    @Autowired
    private lateinit var planSpringDataJpaRepository: PlanSpringDataJpaRepository

    @AfterEach
    fun tearDown() {
        licenseSpringDataJpaRepository.deleteAll()
        planSpringDataJpaRepository.deleteAll()
    }

    @Test
    @DisplayName("POST /licenses - 라이선스 생성 성공")
    fun createLicense_success() {
        // Given
        val plan = Plan.create(PlanCode.ENTERPRISE, 100, 1_000_000)
        planRepository.save(plan)

        val request = CreateLicenseRequest(
            planId = plan.id.value,
            ownerType = "USER",
            ownerId = "user-123",
            startDate = LocalDate.of(2025, 1, 1),
            endDate = LocalDate.of(2025, 12, 31)
        )

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .post("/licenses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.planId").value(plan.id.value.toString()))
            .andExpect(jsonPath("$.data.ownerType").value("USER"))
            .andExpect(jsonPath("$.data.ownerId").value("user-123"))
            .andExpect(jsonPath("$.data.status").value("ACTIVE"))
            .andExpect(jsonPath("$.data.startDate").value("2025-01-01"))
            .andExpect(jsonPath("$.data.endDate").value("2025-12-31"))
            .andDo(
                document(
                    "license-create",
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .tag("License")
                            .summary("라이선스 생성")
                            .description("새로운 라이선스를 생성합니다.")
                            .requestFields(
                                fieldWithPath("planId")
                                    .type(JsonFieldType.STRING)
                                    .description("플랜 ID (UUID)"),
                                fieldWithPath("ownerType")
                                    .type(JsonFieldType.STRING)
                                    .description("소유자 타입 (USER, ORGANIZATION)"),
                                fieldWithPath("ownerId")
                                    .type(JsonFieldType.STRING)
                                    .description("소유자 ID"),
                                fieldWithPath("startDate")
                                    .type(JsonFieldType.STRING)
                                    .description("시작일 (YYYY-MM-DD)"),
                                fieldWithPath("endDate")
                                    .type(JsonFieldType.STRING)
                                    .description("종료일 (YYYY-MM-DD)")
                            )
                            .responseFields(
                                success() + listOf(
                                    fieldWithPath("data.id")
                                        .type(JsonFieldType.STRING)
                                        .description("라이선스 ID (UUID)"),
                                    fieldWithPath("data.planId")
                                        .type(JsonFieldType.STRING)
                                        .description("플랜 ID (UUID)"),
                                    fieldWithPath("data.ownerType")
                                        .type(JsonFieldType.STRING)
                                        .description("소유자 타입 (USER, ORGANIZATION)"),
                                    fieldWithPath("data.ownerId")
                                        .type(JsonFieldType.STRING)
                                        .description("소유자 ID"),
                                    fieldWithPath("data.status")
                                        .type(JsonFieldType.STRING)
                                        .description("라이선스 상태 (ACTIVE, EXPIRED)"),
                                    fieldWithPath("data.startDate")
                                        .type(JsonFieldType.STRING)
                                        .description("시작일 (YYYY-MM-DD)"),
                                    fieldWithPath("data.endDate")
                                        .type(JsonFieldType.STRING)
                                        .description("종료일 (YYYY-MM-DD)")
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("POST /licenses - 유효하지 않은 ownerType으로 생성 실패")
    fun createLicense_invalidOwnerType_returnsError() {
        // Given
        val plan = Plan.create(PlanCode.ENTERPRISE, 100, 1_000_000)
        planRepository.save(plan)

        val request = mapOf(
            "planId" to plan.id.value.toString(),
            "ownerType" to "INVALID",
            "ownerId" to "user-123",
            "startDate" to "2025-01-01",
            "endDate" to "2025-12-31"
        )

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .post("/licenses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    @DisplayName("POST /licenses - 필수 필드 누락 시 생성 실패")
    fun createLicense_missingRequiredField_returnsError() {
        // Given
        val request = mapOf(
            "ownerType" to "USER",
            "ownerId" to "user-123",
            "startDate" to "2025-01-01",
            "endDate" to "2025-12-31"
            // planId 누락
        )

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .post("/licenses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    @DisplayName("GET /licenses/{licenseId} - 라이선스 단건 조회 성공")
    fun getLicenseById_success() {
        // Given
        val plan = Plan.create(PlanCode.PRO, 1, 100_000)
        planRepository.save(plan)

        val license = License.create(
            planId = plan.id,
            owner = Owner.user("user-456"),
            period = Period.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
            )
        )
        licenseRepository.save(license)

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .get("/licenses/{licenseId}", license.id.value)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(license.id.value.toString()))
            .andExpect(jsonPath("$.data.planId").value(plan.id.value.toString()))
            .andExpect(jsonPath("$.data.ownerType").value("USER"))
            .andExpect(jsonPath("$.data.ownerId").value("user-456"))
            .andExpect(jsonPath("$.data.status").value("ACTIVE"))
            .andDo(
                document(
                    "license-get-by-id",
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .tag("License")
                            .summary("라이선스 단건 조회")
                            .description("ID로 라이선스를 조회합니다.")
                            .pathParameters(
                                parameterWithName("licenseId")
                                    .description("라이선스 ID (UUID)")
                            )
                            .responseFields(
                                success() + listOf(
                                    fieldWithPath("data.id")
                                        .type(JsonFieldType.STRING)
                                        .description("라이선스 ID (UUID)"),
                                    fieldWithPath("data.planId")
                                        .type(JsonFieldType.STRING)
                                        .description("플랜 ID (UUID)"),
                                    fieldWithPath("data.ownerType")
                                        .type(JsonFieldType.STRING)
                                        .description("소유자 타입 (USER, ORGANIZATION)"),
                                    fieldWithPath("data.ownerId")
                                        .type(JsonFieldType.STRING)
                                        .description("소유자 ID"),
                                    fieldWithPath("data.status")
                                        .type(JsonFieldType.STRING)
                                        .description("라이선스 상태 (ACTIVE, EXPIRED)"),
                                    fieldWithPath("data.startDate")
                                        .type(JsonFieldType.STRING)
                                        .description("시작일 (YYYY-MM-DD)"),
                                    fieldWithPath("data.endDate")
                                        .type(JsonFieldType.STRING)
                                        .description("종료일 (YYYY-MM-DD)")
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("GET /licenses/{licenseId} - 존재하지 않는 라이선스 조회 실패")
    fun getLicenseById_notFound_returnsError() {
        // Given
        val nonExistentId = UUID.randomUUID()

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .get("/licenses/{licenseId}", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    @DisplayName("GET /licenses?ownerType=&ownerId= - 소유자별 라이선스 목록 조회 성공")
    fun getLicensesByOwner_success() {
        // Given
        val plan = Plan.create(PlanCode.ENTERPRISE, 100, 1_000_000)
        planRepository.save(plan)

        val license1 = License.create(
            planId = plan.id,
            owner = Owner.user("user-789"),
            period = Period.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 6, 30)
            )
        )
        val license2 = License.create(
            planId = plan.id,
            owner = Owner.user("user-789"),
            period = Period.of(
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 12, 31)
            )
        )
        licenseRepository.save(license1)
        licenseRepository.save(license2)

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .get("/licenses")
                    .param("ownerType", "USER")
                    .param("ownerId", "user-789")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].ownerId").value("user-789"))
            .andExpect(jsonPath("$.data[1].ownerId").value("user-789"))
            .andDo(
                document(
                    "license-get-by-owner",
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .tag("License")
                            .summary("소유자별 라이선스 목록 조회")
                            .description("소유자 타입과 ID로 라이선스 목록을 조회합니다.")
                            .queryParameters(
                                queryParamWithName("ownerType")
                                    .description("소유자 타입 (USER, ORGANIZATION)"),
                                queryParamWithName("ownerId")
                                    .description("소유자 ID")
                            )
                            .responseFields(
                                success() + listOf(
                                    fieldWithPath("data[]")
                                        .type(JsonFieldType.ARRAY)
                                        .description("라이선스 목록"),
                                    fieldWithPath("data[].id")
                                        .type(JsonFieldType.STRING)
                                        .description("라이선스 ID (UUID)"),
                                    fieldWithPath("data[].planId")
                                        .type(JsonFieldType.STRING)
                                        .description("플랜 ID (UUID)"),
                                    fieldWithPath("data[].ownerType")
                                        .type(JsonFieldType.STRING)
                                        .description("소유자 타입 (USER, ORGANIZATION)"),
                                    fieldWithPath("data[].ownerId")
                                        .type(JsonFieldType.STRING)
                                        .description("소유자 ID"),
                                    fieldWithPath("data[].status")
                                        .type(JsonFieldType.STRING)
                                        .description("라이선스 상태 (ACTIVE, EXPIRED)"),
                                    fieldWithPath("data[].startDate")
                                        .type(JsonFieldType.STRING)
                                        .description("시작일 (YYYY-MM-DD)"),
                                    fieldWithPath("data[].endDate")
                                        .type(JsonFieldType.STRING)
                                        .description("종료일 (YYYY-MM-DD)")
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("GET /licenses?ownerType=&ownerId= - 유효하지 않은 ownerType으로 조회 실패")
    fun getLicensesByOwner_invalidOwnerType_returnsError() {
        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .get("/licenses")
                    .param("ownerType", "INVALID")
                    .param("ownerId", "user-789")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    @DisplayName("POST /licenses/{licenseId}/expire - 라이선스 만료 처리 성공")
    fun expireLicense_success() {
        // Given
        val plan = Plan.create(PlanCode.PRO, 1, 100_000)
        planRepository.save(plan)

        val license = License.create(
            planId = plan.id,
            owner = Owner.organization("org-100"),
            period = Period.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
            )
        )
        licenseRepository.save(license)

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .post("/licenses/{licenseId}/expire", license.id.value)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(license.id.value.toString()))
            .andExpect(jsonPath("$.data.status").value("EXPIRED"))
            .andDo(
                document(
                    "license-expire",
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .tag("License")
                            .summary("라이선스 만료 처리")
                            .description("라이선스를 만료 상태로 변경합니다.")
                            .pathParameters(
                                parameterWithName("licenseId")
                                    .description("라이선스 ID (UUID)")
                            )
                            .responseFields(
                                success() + listOf(
                                    fieldWithPath("data.id")
                                        .type(JsonFieldType.STRING)
                                        .description("라이선스 ID (UUID)"),
                                    fieldWithPath("data.planId")
                                        .type(JsonFieldType.STRING)
                                        .description("플랜 ID (UUID)"),
                                    fieldWithPath("data.ownerType")
                                        .type(JsonFieldType.STRING)
                                        .description("소유자 타입 (USER, ORGANIZATION)"),
                                    fieldWithPath("data.ownerId")
                                        .type(JsonFieldType.STRING)
                                        .description("소유자 ID"),
                                    fieldWithPath("data.status")
                                        .type(JsonFieldType.STRING)
                                        .description("라이선스 상태 (EXPIRED)"),
                                    fieldWithPath("data.startDate")
                                        .type(JsonFieldType.STRING)
                                        .description("시작일 (YYYY-MM-DD)"),
                                    fieldWithPath("data.endDate")
                                        .type(JsonFieldType.STRING)
                                        .description("종료일 (YYYY-MM-DD)")
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("POST /licenses/{licenseId}/expire - 이미 만료된 라이선스 만료 처리 실패")
    fun expireLicense_alreadyExpired_returnsError() {
        // Given
        val plan = Plan.create(PlanCode.PRO, 1, 100_000)
        planRepository.save(plan)

        val license = License.create(
            planId = plan.id,
            owner = Owner.user("user-999"),
            period = Period.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
            )
        )
        val expiredLicense = license.expire()
        licenseRepository.save(expiredLicense)

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .post("/licenses/{licenseId}/expire", expiredLicense.id.value)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    @DisplayName("DELETE /licenses/{licenseId} - 라이선스 삭제 성공")
    fun deleteLicense_success() {
        // Given
        val plan = Plan.create(PlanCode.ENTERPRISE, 100, 1_000_000)
        planRepository.save(plan)

        val license = License.create(
            planId = plan.id,
            owner = Owner.user("user-888"),
            period = Period.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
            )
        )
        licenseRepository.save(license)

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .delete("/licenses/{licenseId}", license.id.value)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andDo(
                document(
                    "license-delete",
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .tag("License")
                            .summary("라이선스 삭제")
                            .description("라이선스를 삭제합니다.")
                            .pathParameters(
                                parameterWithName("licenseId")
                                    .description("라이선스 ID (UUID)")
                            )
                            .responseFields(
                                success()
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("DELETE /licenses/{licenseId} - 존재하지 않는 라이선스 삭제 실패")
    fun deleteLicense_notFound_returnsError() {
        // Given
        val nonExistentId = UUID.randomUUID()

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .delete("/licenses/{licenseId}", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").exists())
    }
}
