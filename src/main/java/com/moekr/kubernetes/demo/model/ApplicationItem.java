package com.moekr.kubernetes.demo.model;

import io.fabric8.kubernetes.api.model.Service;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.moekr.kubernetes.demo.util.Constants.NAME_PATTERN;

@Data
@NoArgsConstructor
public class ApplicationItem {
	@NotNull
	@Pattern(regexp = NAME_PATTERN)
	private String namespace;
	@NotNull
	@Pattern(regexp = NAME_PATTERN)
	private String name;
	@ApiModelProperty(readOnly = true)
	private String createdAt;

	public ApplicationItem(Service service) {
		this.namespace = service.getMetadata().getNamespace();
		this.name = service.getMetadata().getName();
		this.createdAt = service.getMetadata().getCreationTimestamp();
	}
}
