package com.moekr.kubernetes.demo.model;

import io.fabric8.kubernetes.api.model.EnvVar;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.moekr.kubernetes.demo.util.Constants.NAME_PATTERN;

@Data
@NoArgsConstructor
public class Container {
	@NotNull
	@Pattern(regexp = NAME_PATTERN)
	private String name;
	@NotBlank
	private String image;
	private Map<String, String> env = new HashMap<>();

	public Container(io.fabric8.kubernetes.api.model.Container container) {
		this.name = container.getName();
		this.image = container.getImage();
		this.env = container.getEnv().stream().collect(Collectors.toMap(EnvVar::getName, EnvVar::getValue));
	}

	public io.fabric8.kubernetes.api.model.Container toContainer() {
		io.fabric8.kubernetes.api.model.Container container = new io.fabric8.kubernetes.api.model.Container();
		container.setName(name);
		container.setImage(image);
		container.setEnv(env.entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null)).collect(Collectors.toList()));
		return container;
	}
}
