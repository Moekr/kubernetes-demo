package com.moekr.kubernetes.demo.model;

import io.fabric8.kubernetes.api.model.Container;
import lombok.Data;

@Data
public class DockerContainer {
	private String name;
	private String image;

	public Container toContainer() {
		Container container = new Container();
		container.setName(name);
		container.setImage(image);
		return container;
	}
}
