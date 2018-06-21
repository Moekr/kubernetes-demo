package com.moekr.kubernetes.demo.util;

public abstract class Constants {
	// 用户namespace的label
	public static final String USERSPACE_LABEL = "userspace";
	// pod的label
	public static final String SELECTOR_LABEL = "app";
	// 对外service的label
	public static final String EXTERNAL_LABEL = "external";
}
