package com.flab.license.service.plan.query

import com.flab.license.domain.plan.Plan
import com.flab.license.domain.plan.PlanCode
import com.flab.license.domain.plan.PlanId
import com.flab.license.domain.plan.PlanRepository
import com.flab.license.domain.plan.exception.PlanErrorCode
import com.flab.license.domain.plan.exception.PlanException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given

@ExtendWith(MockitoExtension::class)
class PlanQueryServiceTest {

    @Mock
    private lateinit var planRepository: PlanRepository

    private lateinit var planQueryService: PlanQueryService

    @BeforeEach
    fun setUp() {
        planQueryService = PlanQueryService(planRepository)
    }

    @Nested
    @DisplayName("getById")
    inner class GetById {

        @Test
        @DisplayName("ID로 플랜을 조회할 수 있다")
        fun `get plan by id successfully`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )
            given(planRepository.loadById(plan.id)).willReturn(plan)

            // When
            val result = planQueryService.getById(plan.id)

            // Then
            assertThat(result.id).isEqualTo(plan.id)
            assertThat(result.planCode).isEqualTo(PlanCode.PRO)
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
        fun `throw exception when plan not found`() {
            // Given
            val planId = PlanId.generate()
            given(planRepository.loadById(planId)).willThrow(PlanException(PlanErrorCode.PLAN_NOT_FOUND))

            // When & Then
            assertThatThrownBy {
                planQueryService.getById(planId)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.PLAN_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("getByPlanCode")
    inner class GetByPlanCode {

        @Test
        @DisplayName("플랜 코드로 플랜을 조회할 수 있다")
        fun `get plan by plan code successfully`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.FREE,
                maxSeats = 1,
                monthlyTokenLimit = 50_000L
            )
            given(planRepository.loadByPlanCode(PlanCode.FREE)).willReturn(plan)

            // When
            val result = planQueryService.getByPlanCode(PlanCode.FREE)

            // Then
            assertThat(result.planCode).isEqualTo(PlanCode.FREE)
        }

        @Test
        @DisplayName("존재하지 않는 플랜 코드로 조회하면 예외가 발생한다")
        fun `throw exception when plan code not found`() {
            // Given
            given(planRepository.loadByPlanCode(PlanCode.ENTERPRISE)).willThrow(PlanException(PlanErrorCode.PLAN_NOT_FOUND))

            // When & Then
            assertThatThrownBy {
                planQueryService.getByPlanCode(PlanCode.ENTERPRISE)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.PLAN_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("getAll")
    inner class GetAll {

        @Test
        @DisplayName("전체 플랜 목록을 조회할 수 있다")
        fun `get all plans successfully`() {
            // Given
            val plan1 = Plan.create(
                planCode = PlanCode.FREE,
                maxSeats = 1,
                monthlyTokenLimit = 50_000L
            )
            val plan2 = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )
            given(planRepository.loadAll()).willReturn(listOf(plan1, plan2))

            // When
            val result = planQueryService.getAll()

            // Then
            assertThat(result).hasSize(2)
            assertThat(result.map { it.planCode }).containsExactlyInAnyOrder(PlanCode.FREE, PlanCode.PRO)
        }

        @Test
        @DisplayName("플랜이 없으면 빈 목록을 반환한다")
        fun `return empty list when no plans exist`() {
            // Given
            given(planRepository.loadAll()).willReturn(emptyList())

            // When
            val result = planQueryService.getAll()

            // Then
            assertThat(result).isEmpty()
        }
    }
}
