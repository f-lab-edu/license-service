package com.flab.license.api.plan

import com.fasterxml.jackson.databind.ObjectMapper
import com.flab.license.api.plan.dto.CreatePlanRequest
import com.flab.license.api.plan.dto.UpdatePlanRequest
import com.flab.license.domain.plan.Plan
import com.flab.license.domain.plan.PlanCode
import com.flab.license.domain.plan.PlanRepository
import com.flab.license.infra.persistence.plan.jpa.PlanSpringDataJpaRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
class PlanControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var planRepository: PlanRepository

    @Autowired
    private lateinit var springDataJpaRepository: PlanSpringDataJpaRepository

    @BeforeEach
    fun setUp() {
        springDataJpaRepository.deleteAll()
    }

    @Nested
    @DisplayName("POST /plans")
    inner class CreatePlan {

        @Test
        @DisplayName("플랜 생성 성공")
        fun `create plan successfully`() {
            val request = CreatePlanRequest(
                planCode = "ENTERPRISE",
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )

            mockMvc.post("/plans") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.success") { value(true) }
                jsonPath("$.data.planCode") { value("ENTERPRISE") }
                jsonPath("$.data.maxSeats") { value(10) }
                jsonPath("$.data.monthlyTokenLimit") { value(500_000) }
            }
        }

        @Test
        @DisplayName("최소값(maxSeats=1, monthlyTokenLimit=1)으로 플랜 생성 성공")
        fun `create plan with minimum values`() {
            val request = CreatePlanRequest(
                planCode = "FREE",
                maxSeats = 1,
                monthlyTokenLimit = 1L
            )

            mockMvc.post("/plans") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.success") { value(true) }
                jsonPath("$.data.maxSeats") { value(1) }
                jsonPath("$.data.monthlyTokenLimit") { value(1) }
            }
        }

        @Test
        @DisplayName("필수 필드(planCode) 누락 시 400 에러와 INVALID_INPUT 반환")
        fun `return 400 when required field is missing`() {
            val request = mapOf(
                // planCode 누락
                "maxSeats" to 10,
                "monthlyTokenLimit" to 500_000
            )

            mockMvc.post("/plans") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.success") { value(false) }
                jsonPath("$.error") { value("INVALID_INPUT") }
            }
        }

        @Test
        @DisplayName("유효하지 않은 값 전달 시 400 에러와 INVALID_INPUT 반환")
        fun `return 400 when invalid value`() {
            val request = mapOf(
                "planCode" to "PRO",
                "maxSeats" to -1,
                "monthlyTokenLimit" to 500_000
            )

            mockMvc.post("/plans") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.error") { value("INVALID_INPUT") }
                jsonPath("$.success") { value(false) }
            }
        }

        @Test
        @DisplayName("잘못된 JSON 형식 요청 시 400 에러와 INVALID_JSON 반환")
        fun `return 400 when invalid json format`() {
            mockMvc.post("/plans") {
                contentType = MediaType.APPLICATION_JSON
                content = "{ invalid json }"
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.success") { value(false) }
                jsonPath("$.error") { value("INVALID_JSON") }
            }
        }

        @Test
        @DisplayName("존재하지 않는 PlanCode 값 전달 시 400 에러와 INVALID_INPUT 반환")
        fun `return 400 when invalid enum value`() {
            val request = mapOf(
                "planCode" to "INVALID_PLAN_CODE",
                "maxSeats" to 10,
                "monthlyTokenLimit" to 500_000
            )

            mockMvc.post("/plans") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.success") { value(false) }
                jsonPath("$.error") { value("INVALID_INPUT") }
            }
        }
    }

    @Nested
    @DisplayName("GET /plans")
    inner class GetAllPlans {

        @Test
        @DisplayName("전체 플랜 목록 조회 성공")
        fun `get all plans successfully`() {
            // Given
            val plan1 = Plan.create(PlanCode.FREE, 1, 50_000L)
            val plan2 = Plan.create(PlanCode.ENTERPRISE, 10, 500_000L)
            planRepository.save(plan1)
            planRepository.save(plan2)

            // When & Then
            mockMvc.get("/plans")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.success") { value(true) }
                    jsonPath("$.data.length()") { value(2) }
                }
        }

        @Test
        @DisplayName("플랜이 없으면 빈 목록 반환")
        fun `return empty list when no plans`() {
            mockMvc.get("/plans")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.success") { value(true) }
                    jsonPath("$.data.length()") { value(0) }
                }
        }

        @Test
        @DisplayName("삭제된 플랜은 목록에서 제외된다")
        fun `exclude deleted plans from list`() {
            // Given
            val plan1 = Plan.create(PlanCode.FREE, 1, 50_000L)
            val plan2 = Plan.create(PlanCode.ENTERPRISE, 10, 500_000L)
            planRepository.save(plan1)
            planRepository.save(plan2)

            // When - plan2 삭제
            mockMvc.delete("/plans/${plan2.id.value}")
                .andExpect { status { isOk() } }

            // Then - 목록에서 plan2 제외 확인
            mockMvc.get("/plans")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data.length()") { value(1) }
                    jsonPath("$.data[0].planCode") { value("FREE") }
                }
        }
    }

    @Nested
    @DisplayName("GET /plans/{planId}")
    inner class GetPlanById {

        @Test
        @DisplayName("플랜 상세 조회 성공")
        fun `get plan by id successfully`() {
            // Given
            val plan = Plan.create(PlanCode.ENTERPRISE, 10, 500_000L)
            planRepository.save(plan)

            // When & Then
            mockMvc.get("/plans/${plan.id.value}")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.success") { value(true) }
                    jsonPath("$.data.planCode") { value("ENTERPRISE") }
                    jsonPath("$.data.maxSeats") { value(10) }
                }
        }

        @Test
        @DisplayName("존재하지 않는 플랜 조회 시 404 에러")
        fun `return 404 when plan not found`() {
            val nonExistentId = UUID.randomUUID()

            mockMvc.get("/plans/$nonExistentId")
                .andExpect {
                    status { isNotFound() }
                    jsonPath("$.success") { value(false) }
                    jsonPath("$.error") { value("PLAN_NOT_FOUND") }
                }
        }

        @Test
        @DisplayName("잘못된 UUID 형식 요청 시 400 에러")
        fun `return 400 when invalid uuid format`() {
            mockMvc.get("/plans/invalid-uuid")
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.success") { value(false) }
                    jsonPath("$.error") { value("TYPE_MISMATCH") }
                }
        }
    }

    @Nested
    @DisplayName("PUT /plans/{planId}")
    inner class UpdatePlan {

        @Test
        @DisplayName("플랜 수정 성공")
        fun `update plan successfully`() {
            // Given
            val plan = Plan.create(PlanCode.ENTERPRISE, 10, 500_000L)
            planRepository.save(plan)

            val request = UpdatePlanRequest(
                maxSeats = 20,
                monthlyTokenLimit = 1_000_000L
            )

            // When & Then
            mockMvc.put("/plans/${plan.id.value}") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
                jsonPath("$.success") { value(true) }
                jsonPath("$.data.maxSeats") { value(20) }
                jsonPath("$.data.monthlyTokenLimit") { value(1_000_000) }
            }
        }

        @Test
        @DisplayName("최소값(maxSeats=1, monthlyTokenLimit=1)으로 플랜 수정 성공")
        fun `update plan with minimum values`() {
            // Given
            val plan = Plan.create(PlanCode.ENTERPRISE, 10, 500_000L)
            planRepository.save(plan)

            val request = UpdatePlanRequest(
                maxSeats = 1,
                monthlyTokenLimit = 1L
            )

            // When & Then
            mockMvc.put("/plans/${plan.id.value}") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.maxSeats") { value(1) }
                jsonPath("$.data.monthlyTokenLimit") { value(1) }
            }
        }

        @Test
        @DisplayName("존재하지 않는 플랜 수정 시 404 에러")
        fun `return 404 when plan not found`() {
            val nonExistentId = UUID.randomUUID()
            val request = UpdatePlanRequest(
                maxSeats = 20,
                monthlyTokenLimit = 1_000_000L
            )

            mockMvc.put("/plans/$nonExistentId") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isNotFound() }
                jsonPath("$.success") { value(false) }
            }
        }

        @Test
        @DisplayName("잘못된 UUID 형식 요청 시 400 에러")
        fun `return 400 when invalid uuid format`() {
            val request = UpdatePlanRequest(
                maxSeats = 20,
                monthlyTokenLimit = 1_000_000L
            )

            mockMvc.put("/plans/invalid-uuid") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.error") { value("TYPE_MISMATCH") }
            }
        }
    }

    @Nested
    @DisplayName("DELETE /plans/{planId}")
    inner class DeletePlan {

        @Test
        @DisplayName("플랜 삭제 성공")
        fun `delete plan successfully`() {
            // Given
            val plan = Plan.create(PlanCode.ENTERPRISE, 10, 500_000L)
            planRepository.save(plan)

            // When & Then
            mockMvc.delete("/plans/${plan.id.value}")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.success") { value(true) }
                }

            // Verify soft delete
            mockMvc.get("/plans/${plan.id.value}")
                .andExpect {
                    status { isNotFound() }
                }
        }

        @Test
        @DisplayName("존재하지 않는 플랜 삭제 시 404 에러")
        fun `return 404 when plan not found`() {
            val nonExistentId = UUID.randomUUID()

            mockMvc.delete("/plans/$nonExistentId")
                .andExpect {
                    status { isNotFound() }
                    jsonPath("$.success") { value(false) }
                }
        }

        @Test
        @DisplayName("잘못된 UUID 형식 요청 시 400 에러")
        fun `return 400 when invalid uuid format`() {
            mockMvc.delete("/plans/invalid-uuid")
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.error") { value("TYPE_MISMATCH") }
                }
        }
    }
}
