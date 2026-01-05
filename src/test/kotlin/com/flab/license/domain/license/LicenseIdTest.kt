package com.flab.license.domain.license

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class LicenseIdTest {

    @Nested
    @DisplayName("generate")
    inner class Generate {

        @Test
        @DisplayName("UUID를 생성할 수 있다")
        fun `generate creates valid UUID`() {
            // When
            val licenseId = LicenseId.generate()

            // Then
            assertThat(licenseId.value).isNotNull()
            assertThat(licenseId.value).isInstanceOf(UUID::class.java)
        }

        @Test
        @DisplayName("생성할 때마다 다른 UUID가 생성된다")
        fun `generate creates unique UUIDs`() {
            // When
            val licenseId1 = LicenseId.generate()
            val licenseId2 = LicenseId.generate()

            // Then
            assertThat(licenseId1).isNotEqualTo(licenseId2)
        }
    }

    @Nested
    @DisplayName("equality")
    inner class Equality {

        @Test
        @DisplayName("같은 UUID를 가진 LicenseId는 동등하다")
        fun `LicenseIds with same UUID are equal`() {
            // Given
            val uuid = UUID.randomUUID()

            // When
            val licenseId1 = LicenseId(uuid)
            val licenseId2 = LicenseId(uuid)

            // Then
            assertThat(licenseId1).isEqualTo(licenseId2)
        }

        @Test
        @DisplayName("다른 UUID를 가진 LicenseId는 동등하지 않다")
        fun `LicenseIds with different UUIDs are not equal`() {
            // When
            val licenseId1 = LicenseId(UUID.randomUUID())
            val licenseId2 = LicenseId(UUID.randomUUID())

            // Then
            assertThat(licenseId1).isNotEqualTo(licenseId2)
        }
    }
}
