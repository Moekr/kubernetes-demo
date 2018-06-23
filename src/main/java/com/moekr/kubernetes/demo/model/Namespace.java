package com.moekr.kubernetes.demo.model;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Collections;

import static com.moekr.kubernetes.demo.util.Constants.NAME_PATTERN;
import static com.moekr.kubernetes.demo.util.Constants.USERSPACE_LABEL;

@Data
@NoArgsConstructor
public class Namespace {
	@NotNull
	@Pattern(regexp = NAME_PATTERN)
	private String name;
	@ApiModelProperty(readOnly = true)
	private String createdAt;
	@ApiModelProperty(readOnly = true)
	private String status;

	public Namespace(io.fabric8.kubernetes.api.model.Namespace namespace) {
		this.name = namespace.getMetadata().getName();
		this.createdAt = namespace.getMetadata().getCreationTimestamp();
		this.status = namespace.getStatus().getPhase();
	}

	public io.fabric8.kubernetes.api.model.Namespace toNamespace() {
		io.fabric8.kubernetes.api.model.Namespace namespace = new io.fabric8.kubernetes.api.model.Namespace();
		{
			ObjectMeta metadata = new ObjectMeta();
			metadata.setName(name);
			metadata.setLabels(Collections.singletonMap(USERSPACE_LABEL, name));
			namespace.setMetadata(metadata);
		}
		return namespace;
	}
}
