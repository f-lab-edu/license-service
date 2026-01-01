package com.flab.license.infra.persistence.plan.jpa

import com.flab.license.common.exception.CommonErrorCode
import com.flab.license.common.exception.CustomException
import com.flab.license.domain.plan.Plan
import com.flab.license.domain.plan.PlanCode
import com.flab.license.domain.plan.PlanId
import com.flab.license.domain.plan.PlanRepository
import com.flab.license.domain.plan.exception.PlanErrorCode
import com.flab.license.domain.plan.exception.PlanException
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import com.flab.license.common.config.JpaConfig

@DataJpaTest
@Import(PlanRepositoryJpaAdapter::class, JpaConfig::class)
class PlanRepositoryJpaAdapterTest {

    @Autowired
    private lateinit var planRepository: PlanRepository

    @Autowired
    private lateinit var springDataJpaRepository: PlanSpringDataJpaRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    @BeforeEach
    fun setUp() {
        springDataJpaRepository.deleteAll()
        entityManager.flush()
        entityManager.clear()
    }

    @Nested
    @DisplayName("save")
    inner class Save {

        @Test
        @DisplayName("플랜을 저장할 수 있다")
        fun `save plan successfully`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )

            // When
            val saved = planRepository.save(plan)

            // Then
            assertThat(saved.id).isEqualTo(plan.id)
            assertThat(saved.planCode).isEqualTo(PlanCode.ENTERPRISE)
            assertThat(saved.maxSeats).isEqualTo(10)
            assertThat(saved.monthlyTokenLimit).isEqualTo(500_000L)
            assertThat(saved.deleted).isFalse()
        }
    }

    @Nested
    @DisplayName("update")
    inner class Update {

        @Test
        @DisplayName("플랜을 수정할 수 있다")
        fun `update plan successfully`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )
            planRepository.save(plan)

            val updated = plan.update(maxSeats = 20, monthlyTokenLimit = 1_000_000L)

            // When
            val result = planRepository.update(updated)

            // Then
            assertThat(result.maxSeats).isEqualTo(20)
            assertThat(result.monthlyTokenLimit).isEqualTo(1_000_000L)
        }
    }

    @Nested
    @DisplayName("delete")
    inner class Delete {

        @Test
        @DisplayName("플랜을 삭제할 수 있다")
        fun `delete plan successfully`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )
            planRepository.save(plan)

            val deleted = plan.delete()

            // When
            val result = planRepository.delete(deleted)

            // Then
            assertThat(result.deleted).isTrue()
        }
    }

    @Nested
    @DisplayName("loadById")
    inner class LoadById {

        @Test
        @DisplayName("ID로 플랜을 로드할 수 있다")
        fun `load plan by id successfully`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )
            planRepository.save(plan)

            // When
            val result = planRepository.loadById(plan.id)

            // Then
            assertThat(result.id).isEqualTo(plan.id)
        }

        @Test
        @DisplayName("존재하지 않는 ID로 로드하면 예외가 발생한다")
        fun `throw exception when plan not found`() {
            // Given
            val planId = PlanId.generate()

            // When & Then
            assertThatThrownBy {
                planRepository.loadById(planId)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.PLAN_NOT_FOUND)
        }

        @Test
        @DisplayName("삭제된 플랜은 조회되지 않는다")
        fun `throw exception when plan is deleted`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )
            planRepository.save(plan)
            planRepository.delete(plan.delete())

            // When & Then
            assertThatThrownBy {
                planRepository.loadById(plan.id)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.PLAN_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("loadByPlanCode")
    inner class LoadByPlanCode {

        @Test
        @DisplayName("플랜 코드로 플랜을 로드할 수 있다")
        fun `load plan by plan code successfully`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 100,
                monthlyTokenLimit = 5_000_000L
            )
            planRepository.save(plan)

            // When
            val result = planRepository.loadByPlanCode(PlanCode.ENTERPRISE)

            // Then
            assertThat(result.planCode).isEqualTo(PlanCode.ENTERPRISE)
        }

        @Test
        @DisplayName("존재하지 않는 플랜 코드로 로드하면 예외가 발생한다")
        fun `throw exception when plan code not found`() {
            // When & Then
            assertThatThrownBy {
                planRepository.loadByPlanCode(PlanCode.FREE)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.PLAN_NOT_FOUND)
        }

        @Test
        @DisplayName("삭제된 플랜은 조회되지 않는다")
        fun `throw exception when plan is deleted`() {
            // Given
            val plan = Plan.create(
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 100,
                monthlyTokenLimit = 5_000_000L
            )
            planRepository.save(plan)
            planRepository.delete(plan.delete())

            // When & Then
            assertThatThrownBy {
                planRepository.loadByPlanCode(PlanCode.ENTERPRISE)
            }.isInstanceOf(PlanException::class.java)
                .extracting("errorCode")
                .isEqualTo(PlanErrorCode.PLAN_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("loadAll")
    inner class LoadAll {

        @Test
        @DisplayName("전체 플랜 목록을 조회할 수 있다")
        fun `load all plans successfully`() {
            // Given
            val plan1 = Plan.create(
                planCode = PlanCode.FREE,
                maxSeats = 1,
                monthlyTokenLimit = 50_000L
            )
            val plan2 = Plan.create(
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )
            planRepository.save(plan1)
            planRepository.save(plan2)

            // When
            val result = planRepository.loadAll()

            // Then
            assertThat(result).hasSize(2)
            assertThat(result.map { it.planCode }).containsExactlyInAnyOrder(PlanCode.FREE, PlanCode.ENTERPRISE)
        }

        @Test
        @DisplayName("삭제된 플랜은 목록에서 제외된다")
        fun `exclude deleted plans from list`() {
            // Given
            val plan1 = Plan.create(
                planCode = PlanCode.FREE,
                maxSeats = 1,
                monthlyTokenLimit = 50_000L
            )
            val plan2 = Plan.create(
                planCode = PlanCode.ENTERPRISE,
                maxSeats = 10,
                monthlyTokenLimit = 500_000L
            )
            planRepository.save(plan1)
            planRepository.save(plan2)
            planRepository.delete(plan2.delete())

            // When
            val result = planRepository.loadAll()

            // Then
            assertThat(result).hasSize(1)
            assertThat(result[0].planCode).isEqualTo(PlanCode.FREE)
        }

        @Test
        @DisplayName("플랜이 없으면 빈 목록을 반환한다")
        fun `return empty list when no plans exist`() {
            // When
            val result = planRepository.loadAll()

            // Then
            assertThat(result).isEmpty()
        }
    }

    @Nested
    @DisplayName("loadByIdIncludeDeleted")
    inner class LoadByIdIncludeDeleted {

        @Test
        @DisplayName("아직 구현되지 않은 기능이므로 예외가 발생한다")
        fun `throw not implemented exception`() {
            // Given
            val planId = PlanId.generate()

            // When & Then
            assertThatThrownBy {
                planRepository.loadByIdIncludeDeleted(planId)
            }.isInstanceOf(CustomException::class.java)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.NOT_IMPLEMENTED)
        }
    }
}
