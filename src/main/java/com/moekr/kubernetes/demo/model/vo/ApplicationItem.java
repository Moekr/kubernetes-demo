package com.moekr.kubernetes.demo.model.vo;

import io.fabric8.kubernetes.api.model.Service;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ApplicationItem {
	private String namespace;
	private String name;
	private String createdAt;
	private String internalIp;
	private String externalIp;
	private String ports;

	public ApplicationItem(Service service) {
		this.namespace = service.getMetadata().getNamespace();
		this.name = service.getMetadata().getName();
		this.createdAt = service.getMetadata().getCreationTimestamp();
		this.internalIp = service.getSpec().getClusterIP();
		this.externalIp = service.getSpec().getExternalIPs().toString();
		this.ports = service.getSpec().getPorts().stream()
				.map(sp -> sp.getProtocol() + "/" + sp.getPort())
				.sorted(String::compareTo)
				.collect(Collectors.toList()).toString();
	}
}
