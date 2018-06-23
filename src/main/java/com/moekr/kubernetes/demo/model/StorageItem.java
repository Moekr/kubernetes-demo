package com.moekr.kubernetes.demo.model;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StorageItem {
	private String namespace;
	private String name;
	@ApiModelProperty(readOnly = true)
	private String createdAt;

	public StorageItem(PersistentVolumeClaim claim) {
		this.namespace = claim.getMetadata().getNamespace();
		this.name = claim.getMetadata().getName();
		this.createdAt = claim.getMetadata().getCreationTimestamp();
	}
}
