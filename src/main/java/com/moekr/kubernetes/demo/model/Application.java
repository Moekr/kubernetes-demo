package com.moekr.kubernetes.demo.model;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentSpec;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;

import static com.moekr.kubernetes.demo.util.Constants.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Application extends ApplicationItem {
	@ApiModelProperty(readOnly = true)
	private String internalIp;
	@ApiModelProperty(readOnly = true)
	private String externalIp;
	private Set<Port> ports = new HashSet<>();
	@NotEmpty
	private Set<Container> containers = new HashSet<>();
	private Set<Volume> volumes = new HashSet<>();

	public Application(Service service, Deployment deployment) {
		super(service);
		this.internalIp = service.getSpec().getClusterIP();
		this.externalIp = service.getSpec().getExternalIPs().toString();
		this.ports = service.getSpec().getPorts().stream().map(Port::new).collect(Collectors.toSet());
		this.containers = deployment.getSpec().getTemplate().getSpec().getContainers().stream().map(Container::new).collect(Collectors.toSet());
		Map<String, io.fabric8.kubernetes.api.model.Volume> volumeMap
				= deployment.getSpec().getTemplate().getSpec().getVolumes().stream().collect(Collectors.toMap(io.fabric8.kubernetes.api.model.Volume::getName, v -> v));
		Map<String, VolumeMount> volumeMountMap
				= deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getVolumeMounts().stream().collect(Collectors.toMap(VolumeMount::getName, v -> v));
		this.volumes = volumeMap.entrySet().stream().map(e -> new Volume(e.getValue(), volumeMountMap.get(e.getKey()))).collect(Collectors.toSet());
	}

	public Deployment toDeployment() {
		String namespaceName = super.getNamespace();
		String applicationName = super.getName();
		Deployment deployment = new Deployment();
		{
			ObjectMeta metadata = new ObjectMeta();
			metadata.setNamespace(namespaceName);
			metadata.setName(applicationName);
			metadata.setLabels(volumes.stream().collect(Collectors.toMap(v -> RESOURCE_STORAGE + "/" + v.getStorage(), Volume::getName)));
			deployment.setMetadata(metadata);
		}
		{
			DeploymentSpec spec = new DeploymentSpec();
			spec.setReplicas(1);
			{
				LabelSelector selector = new LabelSelector();
				selector.setMatchLabels(Collections.singletonMap(SELECTOR_LABEL, applicationName));
				spec.setSelector(selector);
			}
			{
				PodTemplateSpec template = new PodTemplateSpec();
				{
					ObjectMeta metadata = new ObjectMeta();
					metadata.setLabels(Collections.singletonMap(SELECTOR_LABEL, applicationName));
					template.setMetadata(metadata);
				}
				{
					PodSpec podSpec = new PodSpec();
					List<VolumeMount> mounts = volumes.stream().map(Volume::toVolumeMount).collect(Collectors.toList());
					podSpec.setContainers(containers.stream().map(Container::toContainer).peek(c -> c.setVolumeMounts(mounts)).collect(Collectors.toList()));
					podSpec.setVolumes(volumes.stream().map(Volume::toVolume).collect(Collectors.toList()));
					template.setSpec(podSpec);
				}
				spec.setTemplate(template);
			}
			deployment.setSpec(spec);
		}
		return deployment;
	}

	public Service toService() {
		String namespace = super.getNamespace();
		String name = super.getName();
		Service service = new Service();
		{
			ObjectMeta metadata = new ObjectMeta();
			metadata.setNamespace(namespace);
			metadata.setName(name);
			{
				Map<String, String> labels = new HashMap<>();
				labels.put(USERSPACE_LABEL, namespace);
				labels.put(EXTERNAL_LABEL, name);
				metadata.setLabels(labels);
			}
			service.setMetadata(metadata);
		}
		{
			ServiceSpec spec = new ServiceSpec();
			spec.setSelector(Collections.singletonMap(SELECTOR_LABEL, name));
			spec.setPorts(ports.stream().map(Port::toServicePort).collect(Collectors.toList()));
			service.setSpec(spec);
		}
		return service;
	}
}
