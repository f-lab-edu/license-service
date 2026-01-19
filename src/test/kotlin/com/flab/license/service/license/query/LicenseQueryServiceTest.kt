package com.flab.license.service.license.query

import com.flab.license.domain.license.License
import com.flab.license.domain.license.LicenseId
import com.flab.license.domain.license.LicenseRepository
import com.flab.license.domain.license.LicenseStatus
import com.flab.license.domain.license.Owner
import com.flab.license.domain.license.Period
import com.flab.license.domain.license.exception.LicenseErrorCode
import com.flab.license.domain.license.exception.LicenseException
import com.flab.license.domain.plan.PlanId
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
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class LicenseQueryServiceTest {

    @Mock
    private lateinit var licenseRepository: LicenseRepository

    private lateinit var sut: LicenseQueryService

    private val defaultPlanId = PlanId.generate()
    private val defaultOwner = Owner.user("user-123")
    private val defaultPeriod = Period.of(
        LocalDate.of(2025, 1, 1),
        LocalDate.of(2025, 12, 31)
    )

    @BeforeEach
    fun setUp() {
        sut = LicenseQueryService(licenseRepository)
    }

    @Nested
    @DisplayName("getById")
    inner class GetById {

        @Test
        @DisplayName("ID로 라이선스를 조회할 수 있다")
        fun `get license by id successfully`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            given(licenseRepository.loadById(license.id)).willReturn(license)

            // When
            val result = sut.getById(license.id)

            // Then
            assertThat(result.id).isEqualTo(license.id)
            assertThat(result.status).isEqualTo(LicenseStatus.ACTIVE)
            assertThat(result.owner).isEqualTo(defaultOwner)
            assertThat(result.deleted).isFalse()
        }

        @Test
        @DisplayName("조회한 라이선스의 플랜 ID가 일치한다")
        fun `get license has correct plan id`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            given(licenseRepository.loadById(license.id)).willReturn(license)

            // When
            val result = sut.getById(license.id)

            // Then
            assertThat(result.planId).isEqualTo(defaultPlanId)
            assertThat(result.period.startDate).isEqualTo(LocalDate.of(2025, 1, 1))
            assertThat(result.period.endDate).isEqualTo(LocalDate.of(2025, 12, 31))
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
        fun `throw exception when license not found`() {
            // Given
            val licenseId = LicenseId.generate()
            given(licenseRepository.loadById(licenseId))
                .willThrow(LicenseException(LicenseErrorCode.LICENSE_NOT_FOUND))

            // When & Then
            assertThatThrownBy {
                sut.getById(licenseId)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_NOT_FOUND)
        }

        @Test
        @DisplayName("삭제된 라이선스를 조회하면 예외가 발생한다")
        fun `throw exception when trying to get deleted license`() {
            // Given
            val licenseId = LicenseId.generate()
            given(licenseRepository.loadById(licenseId))
                .willThrow(LicenseException(LicenseErrorCode.LICENSE_NOT_FOUND))

            // When & Then
            assertThatThrownBy {
                sut.getById(licenseId)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("getByOwner")
    inner class GetByOwner {

        @Test
        @DisplayName("Owner로 라이선스 목록을 조회할 수 있다")
        fun `get licenses by owner successfully`() {
            // Given
            val license1 = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            val license2 = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = Period.of(
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 12, 31)
                )
            )
            given(licenseRepository.loadByOwner(defaultOwner))
                .willReturn(listOf(license1, license2))

            // When
            val result = sut.getByOwner(defaultOwner)

            // Then
            assertThat(result).hasSize(2)
            assertThat(result.map { it.id }).containsExactlyInAnyOrder(license1.id, license2.id)
            assertThat(result.all { it.owner == defaultOwner }).isTrue()
            assertThat(result.all { it.deleted.not() }).isTrue()
        }

        @Test
        @DisplayName("Organization Owner로 라이선스를 조회할 수 있다")
        fun `get licenses by organization owner successfully`() {
            // Given
            val orgOwner = Owner.organization("org-456")
            val license = License.create(
                planId = defaultPlanId,
                owner = orgOwner,
                period = defaultPeriod
            )
            given(licenseRepository.loadByOwner(orgOwner))
                .willReturn(listOf(license))

            // When
            val result = sut.getByOwner(orgOwner)

            // Then
            assertThat(result).hasSize(1)
            assertThat(result[0].owner.type).isEqualTo(Owner.Type.ORGANIZATION)
            assertThat(result[0].owner.id).isEqualTo("org-456")
        }

        @Test
        @DisplayName("Owner에 해당하는 라이선스가 없으면 빈 목록을 반환한다")
        fun `return empty list when no licenses exist for owner`() {
            // Given
            val owner = Owner.user("user-999")
            given(licenseRepository.loadByOwner(owner)).willReturn(emptyList())

            // When
            val result = sut.getByOwner(owner)

            // Then
            assertThat(result).isEmpty()
        }

        @Test
        @DisplayName("조회된 라이선스는 모두 ACTIVE 상태이다")
        fun `all returned licenses are active`() {
            // Given
            val license1 = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            val license2 = License.create(
                planId = PlanId.generate(),
                owner = defaultOwner,
                period = defaultPeriod
            )
            given(licenseRepository.loadByOwner(defaultOwner))
                .willReturn(listOf(license1, license2))

            // When
            val result = sut.getByOwner(defaultOwner)

            // Then
            assertThat(result.all { it.status == LicenseStatus.ACTIVE }).isTrue()
            assertThat(result.all { it.deleted.not() }).isTrue()
        }

        @Test
        @DisplayName("여러 Owner 타입의 라이선스를 각각 조회할 수 있다")
        fun `can get licenses for different owner types`() {
            // Given
            val userOwner = Owner.user("user-100")
            val orgOwner = Owner.organization("org-200")

            val userLicense = License.create(
                planId = defaultPlanId,
                owner = userOwner,
                period = defaultPeriod
            )
            val orgLicense = License.create(
                planId = defaultPlanId,
                owner = orgOwner,
                period = defaultPeriod
            )

            given(licenseRepository.loadByOwner(userOwner)).willReturn(listOf(userLicense))
            given(licenseRepository.loadByOwner(orgOwner)).willReturn(listOf(orgLicense))

            // When
            val userResult = sut.getByOwner(userOwner)
            val orgResult = sut.getByOwner(orgOwner)

            // Then
            assertThat(userResult).hasSize(1)
            assertThat(userResult[0].owner.type).isEqualTo(Owner.Type.USER)
            assertThat(orgResult).hasSize(1)
            assertThat(orgResult[0].owner.type).isEqualTo(Owner.Type.ORGANIZATION)
        }
    }
}
