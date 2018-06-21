package com.moekr.kubernetes.demo.model.vo;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Application extends ApplicationItem {
	private Set<Container> containers;

	public Application(Service service, Deployment deployment) {
		super(service);
		this.containers = deployment.getSpec().getTemplate().getSpec().getContainers().stream()
				.map(Container::new)
				.collect(Collectors.toSet());
	}
}
