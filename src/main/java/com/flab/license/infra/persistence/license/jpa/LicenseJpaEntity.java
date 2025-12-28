package com.flab.license.infra.persistence.license.jpa;

import com.flab.license.domain.license.License;
import com.flab.license.infra.persistence.common.jpa.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "license")
public class LicenseJpaEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	public static License toDomain(LicenseJpaEntity licenseJpaEntity) {
		return new License(String.valueOf(licenseJpaEntity.id));
	}
}
