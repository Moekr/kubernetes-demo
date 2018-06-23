package com.moekr.kubernetes.demo.model;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.moekr.kubernetes.demo.util.Constants.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Storage extends StorageItem {
	@NotNull
	@Pattern(regexp = "^[1-9][0-9]*Mi$")
	private String size;
	@ApiModelProperty(readOnly = true)
	private Map<String, String> applications = new HashMap<>();

	public Storage(PersistentVolumeClaim claim, List<Deployment> deploymentList) {
		super(claim);
		this.size = claim.getSpec().getResources().getRequests().get(RESOURCE_STORAGE).getAmount();
		this.applications = deploymentList.stream()
				.collect(Collectors.toMap(d -> d.getMetadata().getName(), d -> d.getMetadata().getLabels().get(RESOURCE_STORAGE + "/" + super.getName())));
	}

	public PersistentVolumeClaim toPersistentVolumeClaim() {
		String namespace = super.getNamespace();
		String name = super.getName();
		PersistentVolumeClaim claim = new PersistentVolumeClaim();
		{
			ObjectMeta metadata = new ObjectMeta();
			metadata.setNamespace(namespace);
			metadata.setName(name);
			metadata.setLabels(Collections.singletonMap(USERSPACE_LABEL, namespace));
			claim.setMetadata(metadata);
		}
		{
			PersistentVolumeClaimSpec spec = new PersistentVolumeClaimSpec();
			spec.setAccessModes(Collections.singletonList(STORAGE_ACCESS_MODE));
			spec.setStorageClassName(STORAGE_CLASS);
			{
				ResourceRequirements resource = new ResourceRequirements();
				resource.setRequests(Collections.singletonMap(RESOURCE_STORAGE, new Quantity(size)));
				spec.setResources(resource);
			}
			claim.setSpec(spec);
		}
		return claim;
	}
}
