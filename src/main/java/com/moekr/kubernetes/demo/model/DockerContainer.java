package com.moekr.kubernetes.demo.model;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvVar;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class DockerContainer {
	private String name;
	private String image;
	private Map<String, String> env = new HashMap<>();

	public Container toContainer() {
		Container container = new Container();
		container.setName(name);
		container.setImage(image);
		container.setEnv(env.entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null)).collect(Collectors.toList()));
		return container;
	}
}
