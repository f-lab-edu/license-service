package com.flab.license.api.plan

import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.flab.license.api.AbstractRestDocsTest
import com.flab.license.api.CommonResponseFields.success
import com.flab.license.api.plan.dto.CreatePlanRequest
import com.flab.license.api.plan.dto.UpdatePlanRequest
import com.flab.license.domain.plan.Plan
import com.flab.license.domain.plan.PlanCode
import com.flab.license.domain.plan.PlanRepository
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@DisplayName("Plan API")
class PlanControllerTest : AbstractRestDocsTest() {

    @Autowired
    private lateinit var planRepository: PlanRepository

    @Autowired
    private lateinit var springDataJpaRepository: PlanSpringDataJpaRepository

    @AfterEach
    fun tearDown() {
        springDataJpaRepository.deleteAll()
    }

    @Test
    @DisplayName("POST /plans - 플랜 생성 성공")
    fun createPlan_success() {
        // Given
        val request = CreatePlanRequest(
            planCode = "ENTERPRISE",
            maxSeats = 100,
            monthlyTokenLimit = 1_000_000
        )

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .post("/plans")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.planCode").value("ENTERPRISE"))
            .andExpect(jsonPath("$.data.maxSeats").value(100))
            .andExpect(jsonPath("$.data.monthlyTokenLimit").value(1_000_000))
            .andDo(
                document(
                    "plan-create",
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .tag("Plan")
                            .summary("플랜 생성")
                            .description("새로운 플랜을 생성합니다.")
                            .requestFields(
                                fieldWithPath("planCode")
                                    .type(JsonFieldType.STRING)
                                    .description("플랜 코드 (FREE, PRO, ENTERPRISE). FREE/PRO는 maxSeats=1 필수"),
                                fieldWithPath("maxSeats")
                                    .type(JsonFieldType.NUMBER)
                                    .description("최대 좌석 수. FREE/PRO: 1 고정, ENTERPRISE: 1 이상"),
                                fieldWithPath("monthlyTokenLimit")
                                    .type(JsonFieldType.NUMBER)
                                    .description("월별 토큰 한도 (1 이상)")
                            )
                            .responseFields(
                                success() + listOf(
                                    fieldWithPath("data.id")
                                        .type(JsonFieldType.STRING)
                                        .description("플랜 ID (UUID)"),
                                    fieldWithPath("data.planCode")
                                        .type(JsonFieldType.STRING)
                                        .description("플랜 코드"),
                                    fieldWithPath("data.maxSeats")
                                        .type(JsonFieldType.NUMBER)
                                        .description("최대 좌석 수"),
                                    fieldWithPath("data.monthlyTokenLimit")
                                        .type(JsonFieldType.NUMBER)
                                        .description("월별 토큰 한도")
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("POST /plans - 유효하지 않은 플랜 코드로 생성 실패")
    fun createPlan_invalidPlanCode_returnsError() {
        // Given
        val request = mapOf(
            "planCode" to "INVALID",
            "maxSeats" to 100,
            "monthlyTokenLimit" to 1_000_000
        )

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .post("/plans")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    @DisplayName("GET /plans - 전체 플랜 목록 조회")
    fun getAllPlans_success() {
        // Given
        val plan1 = Plan.create(PlanCode.FREE, 1, 10_000)
        val plan2 = Plan.create(PlanCode.PRO, 1, 100_000)
        planRepository.save(plan1)
        planRepository.save(plan2)

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .get("/plans")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(2))
            .andDo(
                document(
                    "plan-get-all",
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .tag("Plan")
                            .summary("전체 플랜 목록 조회")
                            .description("모든 플랜 목록을 조회합니다.")
                            .responseFields(
                                success() + listOf(
                                    fieldWithPath("data[]")
                                        .type(JsonFieldType.ARRAY)
                                        .description("플랜 목록"),
                                    fieldWithPath("data[].id")
                                        .type(JsonFieldType.STRING)
                                        .description("플랜 ID (UUID)"),
                                    fieldWithPath("data[].planCode")
                                        .type(JsonFieldType.STRING)
                                        .description("플랜 코드"),
                                    fieldWithPath("data[].maxSeats")
                                        .type(JsonFieldType.NUMBER)
                                        .description("최대 좌석 수"),
                                    fieldWithPath("data[].monthlyTokenLimit")
                                        .type(JsonFieldType.NUMBER)
                                        .description("월별 토큰 한도")
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("GET /plans/{planId} - 플랜 단건 조회")
    fun getPlanById_success() {
        // Given
        val plan = Plan.create(PlanCode.ENTERPRISE, 100, 1_000_000)
        planRepository.save(plan)

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .get("/plans/{planId}", plan.id.value)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.planCode").value("ENTERPRISE"))
            .andDo(
                document(
                    "plan-get-by-id",
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .tag("Plan")
                            .summary("플랜 단건 조회")
                            .description("ID로 플랜을 조회합니다.")
                            .pathParameters(
                                parameterWithName("planId")
                                    .description("플랜 ID (UUID)")
                            )
                            .responseFields(
                                success() + listOf(
                                    fieldWithPath("data.id")
                                        .type(JsonFieldType.STRING)
                                        .description("플랜 ID (UUID)"),
                                    fieldWithPath("data.planCode")
                                        .type(JsonFieldType.STRING)
                                        .description("플랜 코드"),
                                    fieldWithPath("data.maxSeats")
                                        .type(JsonFieldType.NUMBER)
                                        .description("최대 좌석 수"),
                                    fieldWithPath("data.monthlyTokenLimit")
                                        .type(JsonFieldType.NUMBER)
                                        .description("월별 토큰 한도")
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("GET /plans/{planId} - 존재하지 않는 플랜 조회")
    fun getPlanById_notFound_returnsError() {
        // Given
        val nonExistentId = UUID.randomUUID()

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .get("/plans/{planId}", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    @DisplayName("PUT /plans/{planId} - 플랜 수정")
    fun updatePlan_success() {
        // Given
        val plan = Plan.create(PlanCode.ENTERPRISE, 100, 1_000_000)
        planRepository.save(plan)
        val request = UpdatePlanRequest(
            maxSeats = 200,
            monthlyTokenLimit = 2_000_000
        )

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .put("/plans/{planId}", plan.id.value)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.maxSeats").value(200))
            .andExpect(jsonPath("$.data.monthlyTokenLimit").value(2_000_000))
            .andDo(
                document(
                    "plan-update",
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .tag("Plan")
                            .summary("플랜 수정")
                            .description("플랜 정보를 수정합니다.")
                            .pathParameters(
                                parameterWithName("planId")
                                    .description("플랜 ID (UUID)")
                            )
                            .requestFields(
                                fieldWithPath("maxSeats")
                                    .type(JsonFieldType.NUMBER)
                                    .description("최대 좌석 수 (1 이상)"),
                                fieldWithPath("monthlyTokenLimit")
                                    .type(JsonFieldType.NUMBER)
                                    .description("월별 토큰 한도 (1 이상)")
                            )
                            .responseFields(
                                success() + listOf(
                                    fieldWithPath("data.id")
                                        .type(JsonFieldType.STRING)
                                        .description("플랜 ID (UUID)"),
                                    fieldWithPath("data.planCode")
                                        .type(JsonFieldType.STRING)
                                        .description("플랜 코드"),
                                    fieldWithPath("data.maxSeats")
                                        .type(JsonFieldType.NUMBER)
                                        .description("최대 좌석 수"),
                                    fieldWithPath("data.monthlyTokenLimit")
                                        .type(JsonFieldType.NUMBER)
                                        .description("월별 토큰 한도")
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("DELETE /plans/{planId} - 플랜 삭제")
    fun deletePlan_success() {
        // Given
        val plan = Plan.create(PlanCode.ENTERPRISE, 100, 1_000_000)
        planRepository.save(plan)

        // When & Then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .delete("/plans/{planId}", plan.id.value)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andDo(
                document(
                    "plan-delete",
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .tag("Plan")
                            .summary("플랜 삭제")
                            .description("플랜을 삭제합니다.")
                            .pathParameters(
                                parameterWithName("planId")
                                    .description("플랜 ID (UUID)")
                            )
                            .responseFields(
                                success()
                            )
                            .build()
                    )
                )
            )
    }
}
