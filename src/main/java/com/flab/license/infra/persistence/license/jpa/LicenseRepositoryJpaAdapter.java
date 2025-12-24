package com.flab.license.infra.persistence.license.jpa;

import org.springframework.stereotype.Repository;

import com.flab.license.domain.license.LicenseRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LicenseRepositoryJpaAdapter implements LicenseRepository {
	private final LicenseSpringDataJpaRepository springDataJpaRepository;
}
