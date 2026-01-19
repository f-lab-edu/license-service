package com.flab.license.infra.persistence.license.jpa

import com.flab.license.common.config.JpaConfig
import com.flab.license.common.exception.CommonErrorCode
import com.flab.license.common.exception.CustomException
import com.flab.license.domain.license.*
import com.flab.license.domain.license.exception.LicenseErrorCode
import com.flab.license.domain.license.exception.LicenseException
import com.flab.license.domain.plan.PlanId
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
import java.time.LocalDate

@DataJpaTest
@Import(LicenseRepositoryJpaAdapter::class, JpaConfig::class)
class LicenseRepositoryJpaAdapterTest {

    @Autowired
    private lateinit var licenseRepository: LicenseRepository

    @Autowired
    private lateinit var springDataJpaRepository: LicenseSpringDataJpaRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    private val defaultPlanId = PlanId.generate()
    private val defaultOwner = Owner.user("user-123")
    private val defaultPeriod = Period.of(
        LocalDate.of(2025, 1, 1),
        LocalDate.of(2025, 12, 31)
    )

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
        @DisplayName("라이선스를 저장할 수 있다")
        fun `save license successfully`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )

            // When
            val saved = licenseRepository.save(license)

            // Then
            assertThat(saved.id).isEqualTo(license.id)
            assertThat(saved.planId).isEqualTo(defaultPlanId)
            assertThat(saved.owner).isEqualTo(defaultOwner)
            assertThat(saved.period).isEqualTo(defaultPeriod)
            assertThat(saved.deleted).isFalse()
        }
    }

    @Nested
    @DisplayName("update")
    inner class Update {

        @Test
        @DisplayName("라이선스를 만료시킬 수 있다")
        fun `update license to expired`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            licenseRepository.save(license)

            val expired = license.expire()

            // When
            val result = licenseRepository.update(expired)

            // Then
            assertThat(result.status.isExpired()).isTrue()
        }
    }

    @Nested
    @DisplayName("delete")
    inner class Delete {

        @Test
        @DisplayName("라이선스를 삭제할 수 있다")
        fun `delete license successfully`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            licenseRepository.save(license)

            val deleted = license.delete()

            // When
            val result = licenseRepository.delete(deleted)

            // Then
            assertThat(result.deleted).isTrue()
        }
    }

    @Nested
    @DisplayName("loadById")
    inner class LoadById {

        @Test
        @DisplayName("ID로 라이선스를 로드할 수 있다")
        fun `load license by id successfully`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            licenseRepository.save(license)

            // When
            val result = licenseRepository.loadById(license.id)

            // Then
            assertThat(result.id).isEqualTo(license.id)
        }

        @Test
        @DisplayName("존재하지 않는 ID로 로드하면 예외가 발생한다")
        fun `throw exception when license not found`() {
            // Given
            val licenseId = LicenseId.generate()

            // When & Then
            assertThatThrownBy {
                licenseRepository.loadById(licenseId)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_NOT_FOUND)
        }

        @Test
        @DisplayName("삭제된 라이선스는 조회되지 않는다")
        fun `throw exception when license is deleted`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            licenseRepository.save(license)
            licenseRepository.delete(license.delete())

            // When & Then
            assertThatThrownBy {
                licenseRepository.loadById(license.id)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("loadByOwner")
    inner class LoadByOwner {

        @Test
        @DisplayName("소유자로 라이선스 목록을 조회할 수 있다")
        fun `load licenses by owner successfully`() {
            // Given
            val owner = Owner.user("user-456")
            val license1 = License.create(
                planId = defaultPlanId,
                owner = owner,
                period = defaultPeriod
            )
            val license2 = License.create(
                planId = PlanId.generate(),
                owner = owner,
                period = defaultPeriod
            )
            licenseRepository.save(license1)
            licenseRepository.save(license2)

            // When
            val result = licenseRepository.loadByOwner(owner)

            // Then
            assertThat(result).hasSize(2)
            assertThat(result.map { it.id }).containsExactlyInAnyOrder(license1.id, license2.id)
        }

        @Test
        @DisplayName("다른 소유자의 라이선스는 조회되지 않는다")
        fun `not load other owner licenses`() {
            // Given
            val owner1 = Owner.user("user-111")
            val owner2 = Owner.user("user-222")
            val license1 = License.create(
                planId = defaultPlanId,
                owner = owner1,
                period = defaultPeriod
            )
            val license2 = License.create(
                planId = defaultPlanId,
                owner = owner2,
                period = defaultPeriod
            )
            licenseRepository.save(license1)
            licenseRepository.save(license2)

            // When
            val result = licenseRepository.loadByOwner(owner1)

            // Then
            assertThat(result).hasSize(1)
            assertThat(result[0].owner).isEqualTo(owner1)
        }

        @Test
        @DisplayName("삭제된 라이선스는 목록에서 제외된다")
        fun `exclude deleted licenses from list`() {
            // Given
            val owner = Owner.organization("org-123")
            val license1 = License.create(
                planId = defaultPlanId,
                owner = owner,
                period = defaultPeriod
            )
            val license2 = License.create(
                planId = PlanId.generate(),
                owner = owner,
                period = defaultPeriod
            )
            licenseRepository.save(license1)
            licenseRepository.save(license2)
            licenseRepository.delete(license2.delete())

            // When
            val result = licenseRepository.loadByOwner(owner)

            // Then
            assertThat(result).hasSize(1)
            assertThat(result[0].id).isEqualTo(license1.id)
        }

        @Test
        @DisplayName("라이선스가 없으면 빈 목록을 반환한다")
        fun `return empty list when no licenses exist`() {
            // Given
            val owner = Owner.user("user-999")

            // When
            val result = licenseRepository.loadByOwner(owner)

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
            val licenseId = LicenseId.generate()

            // When & Then
            assertThatThrownBy {
                licenseRepository.loadByIdIncludeDeleted(licenseId)
            }.isInstanceOf(CustomException::class.java)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.NOT_IMPLEMENTED)
        }
    }
}
