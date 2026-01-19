package com.flab.license.service.license.command

import com.flab.license.domain.license.*
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
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class LicenseCommandServiceTest {

    @Mock
    private lateinit var licenseRepository: LicenseRepository

    private lateinit var licenseCommandService: LicenseCommandService

    private val defaultPlanId = PlanId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
    private val defaultOwner = Owner.user("user-123")
    private val defaultPeriod = Period.of(
        LocalDate.of(2025, 1, 1),
        LocalDate.of(2025, 12, 31)
    )

    @BeforeEach
    fun setUp() {
        licenseCommandService = LicenseCommandService(licenseRepository)
    }

    @Nested
    @DisplayName("create")
    inner class Create {

        @Test
        @DisplayName("라이선스를 생성할 수 있다")
        fun `create license successfully`() {
            // Given
            val command = CreateLicenseCommand(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            given(licenseRepository.save(any())).willAnswer { it.arguments[0] }

            // When
            val result = licenseCommandService.create(command)

            // Then
            assertThat(result.status).isEqualTo(LicenseStatus.ACTIVE)
            assertThat(result.planId).isEqualTo(defaultPlanId)
            assertThat(result.owner).isEqualTo(defaultOwner)
            assertThat(result.period).isEqualTo(defaultPeriod)
            assertThat(result.deleted).isFalse()
            verify(licenseRepository).save(any())
        }

        @Test
        @DisplayName("Organization 소유의 라이선스를 생성할 수 있다")
        fun `create license for organization successfully`() {
            // Given
            val orgOwner = Owner.organization("org-456")
            val command = CreateLicenseCommand(
                planId = defaultPlanId,
                owner = orgOwner,
                period = defaultPeriod
            )
            given(licenseRepository.save(any())).willAnswer { it.arguments[0] }

            // When
            val result = licenseCommandService.create(command)

            // Then
            assertThat(result.owner.type).isEqualTo(Owner.Type.ORGANIZATION)
            assertThat(result.owner.id).isEqualTo("org-456")
            assertThat(result.status).isEqualTo(LicenseStatus.ACTIVE)
            verify(licenseRepository).save(any())
        }
    }

    @Nested
    @DisplayName("expire")
    inner class Expire {

        @Test
        @DisplayName("ACTIVE 라이선스를 만료시킬 수 있다")
        fun `expire active license successfully`() {
            // Given
            val activeLicense = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            val expiredLicense = activeLicense.expire()

            given(licenseRepository.loadById(activeLicense.id)).willReturn(activeLicense)
            given(licenseRepository.update(any())).willReturn(expiredLicense)

            // When
            val result = licenseCommandService.expire(activeLicense.id)

            // Then
            assertThat(result.status).isEqualTo(LicenseStatus.EXPIRED)
            assertThat(result.id).isEqualTo(activeLicense.id)
            assertThat(result.planId).isEqualTo(activeLicense.planId)
            assertThat(result.owner).isEqualTo(activeLicense.owner)
            assertThat(result.period).isEqualTo(activeLicense.period)
            verify(licenseRepository).loadById(activeLicense.id)
            verify(licenseRepository).update(any())
        }

        @Test
        @DisplayName("이미 만료된 라이선스를 만료시키면 예외가 발생한다")
        fun `throw exception when expire already expired license`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            val expiredLicense = license.expire()

            given(licenseRepository.loadById(expiredLicense.id)).willReturn(expiredLicense)

            // When & Then
            assertThatThrownBy {
                licenseCommandService.expire(expiredLicense.id)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.ALREADY_EXPIRED)
        }

        @Test
        @DisplayName("삭제된 라이선스를 만료시키면 예외가 발생한다")
        fun `throw exception when expire deleted license`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            val deletedLicense = license.delete()

            given(licenseRepository.loadById(deletedLicense.id)).willReturn(deletedLicense)

            // When & Then
            assertThatThrownBy {
                licenseCommandService.expire(deletedLicense.id)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_ALREADY_DELETED)
        }

        @Test
        @DisplayName("존재하지 않는 라이선스를 만료시키면 예외가 발생한다")
        fun `throw exception when license not found`() {
            // Given
            val licenseId = LicenseId(UUID.fromString("00000000-0000-0000-0000-000000000999"))
            given(licenseRepository.loadById(licenseId)).willThrow(LicenseException(LicenseErrorCode.LICENSE_NOT_FOUND))

            // When & Then
            assertThatThrownBy {
                licenseCommandService.expire(licenseId)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("delete")
    inner class Delete {

        @Test
        @DisplayName("라이선스를 삭제할 수 있다")
        fun `delete license successfully`() {
            // Given
            val activeLicense = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            val deletedLicense = activeLicense.delete()

            given(licenseRepository.loadById(activeLicense.id)).willReturn(activeLicense)
            given(licenseRepository.delete(any())).willReturn(deletedLicense)

            // When
            licenseCommandService.delete(activeLicense.id)

            // Then
            verify(licenseRepository).loadById(activeLicense.id)
            verify(licenseRepository).delete(any())
        }

        @Test
        @DisplayName("만료된 라이선스도 삭제할 수 있다")
        fun `delete expired license successfully`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            val expiredLicense = license.expire()

            given(licenseRepository.loadById(expiredLicense.id)).willReturn(expiredLicense)
            given(licenseRepository.delete(any())).willAnswer { it.arguments[0] }

            // When
            licenseCommandService.delete(expiredLicense.id)

            // Then
            verify(licenseRepository).loadById(expiredLicense.id)
            verify(licenseRepository).delete(any())
        }

        @Test
        @DisplayName("이미 삭제된 라이선스를 삭제하면 예외가 발생한다")
        fun `throw exception when delete already deleted license`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            val deletedLicense = license.delete()

            given(licenseRepository.loadById(deletedLicense.id)).willReturn(deletedLicense)

            // When & Then
            assertThatThrownBy {
                licenseCommandService.delete(deletedLicense.id)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_ALREADY_DELETED)
        }

        @Test
        @DisplayName("존재하지 않는 라이선스를 삭제하면 예외가 발생한다")
        fun `throw exception when license not found`() {
            // Given
            val licenseId = LicenseId(UUID.fromString("00000000-0000-0000-0000-000000000999"))
            given(licenseRepository.loadById(licenseId)).willThrow(LicenseException(LicenseErrorCode.LICENSE_NOT_FOUND))

            // When & Then
            assertThatThrownBy {
                licenseCommandService.delete(licenseId)
            }.isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_NOT_FOUND)
        }
    }
}
