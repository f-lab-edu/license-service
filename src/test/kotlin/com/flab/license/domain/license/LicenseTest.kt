package com.flab.license.domain.license

import com.flab.license.domain.license.exception.LicenseErrorCode
import com.flab.license.domain.license.exception.LicenseException
import com.flab.license.domain.plan.PlanId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LicenseTest {

    private val defaultPlanId = PlanId.generate()
    private val defaultOwner = Owner.user("user-123")
    private val defaultPeriod = Period.of(
        LocalDate.of(2025, 1, 1),
        LocalDate.of(2025, 12, 31)
    )

    @Nested
    @DisplayName("create")
    inner class Create {

        @Test
        @DisplayName("라이선스를 생성할 수 있다")
        fun `create license successfully`() {
            // When
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )

            // Then
            assertThat(license.id).isNotNull()
            assertThat(license.planId).isEqualTo(defaultPlanId)
            assertThat(license.owner).isEqualTo(defaultOwner)
            assertThat(license.status).isEqualTo(LicenseStatus.ACTIVE)
            assertThat(license.period).isEqualTo(defaultPeriod)
            assertThat(license.deleted).isFalse()
        }

        @Test
        @DisplayName("USER 타입 Owner로 라이선스를 생성할 수 있다")
        fun `create license with USER owner`() {
            // Given
            val userOwner = Owner.user("user-456")

            // When
            val license = License.create(
                planId = defaultPlanId,
                owner = userOwner,
                period = defaultPeriod
            )

            // Then
            assertThat(license.owner.type).isEqualTo(Owner.Type.USER)
        }

        @Test
        @DisplayName("ORGANIZATION 타입 Owner로 라이선스를 생성할 수 있다")
        fun `create license with ORGANIZATION owner`() {
            // Given
            val orgOwner = Owner.organization("org-123")

            // When
            val license = License.create(
                planId = defaultPlanId,
                owner = orgOwner,
                period = defaultPeriod
            )

            // Then
            assertThat(license.owner.type).isEqualTo(Owner.Type.ORGANIZATION)
        }

        @Test
        @DisplayName("생성된 라이선스는 ACTIVE 상태이다")
        fun `created license has ACTIVE status`() {
            // When
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )

            // Then
            assertThat(license.status).isEqualTo(LicenseStatus.ACTIVE)
            assertThat(license.status.isActive()).isTrue()
            assertThat(license.status.isExpired()).isFalse()
        }
    }

    @Nested
    @DisplayName("expire")
    inner class Expire {

        @Test
        @DisplayName("ACTIVE 라이선스를 만료시킬 수 있다")
        fun `expire active license successfully`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )

            // When
            val expiredLicense = license.expire()

            // Then
            assertThat(expiredLicense.id).isEqualTo(license.id)
            assertThat(expiredLicense.planId).isEqualTo(license.planId)
            assertThat(expiredLicense.owner).isEqualTo(license.owner)
            assertThat(expiredLicense.status).isEqualTo(LicenseStatus.EXPIRED)
            assertThat(expiredLicense.period).isEqualTo(license.period)
            assertThat(expiredLicense.deleted).isEqualTo(license.deleted)
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

            // When & Then
            assertThatThrownBy { expiredLicense.expire() }
                .isInstanceOf(LicenseException::class.java)
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

            // When & Then
            assertThatThrownBy { deletedLicense.expire() }
                .isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_ALREADY_DELETED)
        }
    }

    @Nested
    @DisplayName("delete")
    inner class Delete {

        @Test
        @DisplayName("라이선스를 삭제하면 deleted가 true가 된다")
        fun `delete license sets deleted to true`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )

            // When
            val deletedLicense = license.delete()

            // Then
            assertThat(deletedLicense.deleted).isTrue()
            assertThat(deletedLicense.id).isEqualTo(license.id)
            assertThat(deletedLicense.planId).isEqualTo(license.planId)
            assertThat(deletedLicense.owner).isEqualTo(license.owner)
            assertThat(deletedLicense.status).isEqualTo(license.status)
            assertThat(deletedLicense.period).isEqualTo(license.period)
        }

        @Test
        @DisplayName("이미 삭제된 라이선스를 다시 삭제하면 예외가 발생한다")
        fun `throw exception when delete already deleted license`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            val deletedLicense = license.delete()

            // When & Then
            assertThatThrownBy { deletedLicense.delete() }
                .isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_ALREADY_DELETED)
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

            // When
            val deletedLicense = expiredLicense.delete()

            // Then
            assertThat(deletedLicense.deleted).isTrue()
            assertThat(deletedLicense.status).isEqualTo(LicenseStatus.EXPIRED)
        }
    }

    @Nested
    @DisplayName("reconstitute")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 라이선스를 복원할 수 있다")
        fun `reconstitute license successfully`() {
            // Given
            val id = LicenseId.generate()
            val planId = PlanId.generate()
            val owner = Owner.user("user-789")
            val status = LicenseStatus.ACTIVE
            val period = Period.reconstitute(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
            )
            val deleted = false

            // When
            val license = License.reconstitute(
                id = id,
                planId = planId,
                owner = owner,
                status = status,
                period = period,
                deleted = deleted
            )

            // Then
            assertThat(license.id).isEqualTo(id)
            assertThat(license.planId).isEqualTo(planId)
            assertThat(license.owner).isEqualTo(owner)
            assertThat(license.status).isEqualTo(status)
            assertThat(license.period).isEqualTo(period)
            assertThat(license.deleted).isEqualTo(deleted)
        }

        @Test
        @DisplayName("삭제된 라이선스도 복원할 수 있다")
        fun `reconstitute deleted license`() {
            // Given
            val id = LicenseId.generate()

            // When
            val license = License.reconstitute(
                id = id,
                planId = defaultPlanId,
                owner = defaultOwner,
                status = LicenseStatus.EXPIRED,
                period = defaultPeriod,
                deleted = true
            )

            // Then
            assertThat(license.deleted).isTrue()
            assertThat(license.status).isEqualTo(LicenseStatus.EXPIRED)
        }

        @Test
        @DisplayName("만료된 라이선스도 복원할 수 있다")
        fun `reconstitute expired license`() {
            // Given
            val id = LicenseId.generate()

            // When
            val license = License.reconstitute(
                id = id,
                planId = defaultPlanId,
                owner = defaultOwner,
                status = LicenseStatus.EXPIRED,
                period = defaultPeriod,
                deleted = false
            )

            // Then
            assertThat(license.status).isEqualTo(LicenseStatus.EXPIRED)
            assertThat(license.status.isExpired()).isTrue()
        }
    }

    @Nested
    @DisplayName("상태 전이 시나리오")
    inner class StateTransition {

        @Test
        @DisplayName("create → expire → delete 전체 생명주기")
        fun `full lifecycle - create, expire, delete`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )

            // When
            val expiredLicense = license.expire()
            val deletedLicense = expiredLicense.delete()

            // Then
            assertThat(deletedLicense.id).isEqualTo(license.id)
            assertThat(deletedLicense.status).isEqualTo(LicenseStatus.EXPIRED)
            assertThat(deletedLicense.deleted).isTrue()
        }

        @Test
        @DisplayName("create → delete (만료 건너뛰기)")
        fun `create and delete without expire`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )

            // When
            val deletedLicense = license.delete()

            // Then
            assertThat(deletedLicense.status).isEqualTo(LicenseStatus.ACTIVE)
            assertThat(deletedLicense.deleted).isTrue()
        }

        @Test
        @DisplayName("삭제된 라이선스는 만료시킬 수 없다 (삭제 우선)")
        fun `deleted license cannot be expired`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            val deletedLicense = license.delete()

            // When & Then
            assertThatThrownBy { deletedLicense.expire() }
                .isInstanceOf(LicenseException::class.java)
                .extracting("errorCode")
                .isEqualTo(LicenseErrorCode.LICENSE_ALREADY_DELETED)
        }

        @Test
        @DisplayName("상태 전이 후에도 불변 속성은 유지된다")
        fun `immutable properties preserved after state transitions`() {
            // Given
            val license = License.create(
                planId = defaultPlanId,
                owner = defaultOwner,
                period = defaultPeriod
            )
            val originalId = license.id
            val originalPlanId = license.planId
            val originalOwner = license.owner
            val originalPeriod = license.period

            // When
            val expiredLicense = license.expire()
            val deletedLicense = expiredLicense.delete()

            // Then
            assertThat(deletedLicense.id).isEqualTo(originalId)
            assertThat(deletedLicense.planId).isEqualTo(originalPlanId)
            assertThat(deletedLicense.owner).isEqualTo(originalOwner)
            assertThat(deletedLicense.period).isEqualTo(originalPeriod)
        }
    }
}
