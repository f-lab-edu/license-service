package com.flab.license.service.plan.command

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
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class PlanCommandServiceTest {

    @Mock
    private lateinit var planRepository: PlanRepository

    private lateinit var planCommandService: PlanCommandService

    @BeforeEach
    fun setUp() {
        planCommandService = PlanCommandService(planRepository)
    }

    @Nested
    @DisplayName("create")
    inner class Create {

        @Test
        @DisplayName("플랜을 생성할 수 있다")
        fun `create plan successfully`() {
            // Given
            val command = CreatePlanCommand(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )
            given(planRepository.save(any())).willAnswer { it.arguments[0] }

            // When
            val result = planCommandService.create(command)

            // Then
            assertThat(result.planCode).isEqualTo(PlanCode.PRO)
            assertThat(result.maxSeats).isEqualTo(10)
            assertThat(result.monthlyTokenLimit).isEqualTo(500_000L)
            verify(planRepository).save(any())
        }
    }

    @Nested
    @DisplayName("update")
    inner class Update {

        @Test
        @DisplayName("플랜을 수정할 수 있다")
        fun `update plan successfully`() {
            // Given
            val existingPlan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )
            val command = UpdatePlanCommand(
                planId = existingPlan.id,
                maxSeats = 20,
                monthlyTokenLimit = 1_000_000L
            )
            given(planRepository.loadById(existingPlan.id)).willReturn(existingPlan)
            given(planRepository.update(any())).willAnswer { it.arguments[0] }

            // When
            val result = planCommandService.update(command)

            // Then
            assertThat(result.maxSeats).isEqualTo(20)
            assertThat(result.monthlyTokenLimit).isEqualTo(1_000_000L)
        }

        @Test
        @DisplayName("존재하지 않는 플랜을 수정하면 예외가 발생한다")
        fun `throw exception when plan not found`() {
            // Given
            val command = UpdatePlanCommand(
                planId = PlanId.generate(),
                maxSeats = 20,
                monthlyTokenLimit = 1_000_000L
            )
            given(planRepository.loadById(command.planId)).willThrow(PlanException(PlanErrorCode.PLAN_NOT_FOUND))

            // When & Then
            assertThatThrownBy {
                planCommandService.update(command)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.PLAN_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("delete")
    inner class Delete {

        @Test
        @DisplayName("플랜을 삭제할 수 있다")
        fun `delete plan successfully`() {
            // Given
            val existingPlan = Plan.create(
                planCode = PlanCode.PRO,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )
            given(planRepository.loadById(existingPlan.id)).willReturn(existingPlan)
            given(planRepository.delete(any())).willAnswer { it.arguments[0] }

            // When
            planCommandService.delete(existingPlan.id)

            // Then
            verify(planRepository).delete(any())
        }

        @Test
        @DisplayName("존재하지 않는 플랜을 삭제하면 예외가 발생한다")
        fun `throw exception when plan not found`() {
            // Given
            val planId = PlanId.generate()
            given(planRepository.loadById(planId)).willThrow(PlanException(PlanErrorCode.PLAN_NOT_FOUND))

            // When & Then
            assertThatThrownBy {
                planCommandService.delete(planId)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.PLAN_NOT_FOUND)
        }
    }
}
