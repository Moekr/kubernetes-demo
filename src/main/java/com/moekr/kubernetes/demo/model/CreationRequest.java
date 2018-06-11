package com.moekr.kubernetes.demo.model;

import lombok.Data;

import java.util.Set;

@Data
public class CreationRequest {
	private String namespace;
	private String name;
	private Set<DockerContainer> containers;
	private Set<ExposedPort> ports;
}
