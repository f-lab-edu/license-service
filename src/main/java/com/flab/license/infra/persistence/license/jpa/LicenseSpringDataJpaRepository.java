package com.flab.license.infra.persistence.license.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseSpringDataJpaRepository extends JpaRepository<LicenseJpaEntity, Long> {

}
