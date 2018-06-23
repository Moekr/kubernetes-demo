package com.moekr.kubernetes.demo.util;

public abstract class Constants {
	// 用户namespace的label
	public static final String USERSPACE_LABEL = "userspace";
	// pod的label
	public static final String SELECTOR_LABEL = "app";
	// 对外service的label
	public static final String EXTERNAL_LABEL = "external";
	// 持久卷访问模式
	public static final String STORAGE_ACCESS_MODE = "ReadWriteMany";
	// 持久卷存储类型
	public static final String STORAGE_CLASS = "glusterfs";

	public static final String RESOURCE_STORAGE = "storage";

	public static final String NAME_PATTERN = "^[0-9a-zA-Z-]{8,}$";
}
