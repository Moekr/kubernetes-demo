package com.moekr.kubernetes.demo.model.vo;

import io.fabric8.kubernetes.api.model.Namespace;
import lombok.Data;

@Data
public class NamespaceItem {
	private String name;
	private String createdAt;
	private String status;

	public NamespaceItem(Namespace namespace) {
		this.name = namespace.getMetadata().getName();
		this.createdAt = namespace.getMetadata().getCreationTimestamp();
		this.status = namespace.getStatus().getPhase();
	}
}
