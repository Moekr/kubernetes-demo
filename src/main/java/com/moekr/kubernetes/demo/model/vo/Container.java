package com.moekr.kubernetes.demo.model.vo;

import io.fabric8.kubernetes.api.model.EnvVar;
import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Container {
	private String name;
	private String image;
	private Map<String, String> env;

	public Container(io.fabric8.kubernetes.api.model.Container container) {
		this.name = container.getName();
		this.image = container.getImage();
		this.env = container.getEnv().stream()
				.collect(Collectors.toMap(EnvVar::getName, EnvVar::getValue));
	}
}
