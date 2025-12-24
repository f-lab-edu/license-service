package com.flab.license.domain.license;

import lombok.Getter;

@Getter
public class License {
	private final String id;

	public License(String id) {
		this.id = id;
	}
}
