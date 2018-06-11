package com.moekr.kubernetes.demo.service;

import com.moekr.kubernetes.demo.model.CreationRequest;
import com.moekr.kubernetes.demo.model.DockerContainer;
import com.moekr.kubernetes.demo.model.ExposedPort;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.stream.Collectors;

import static com.moekr.kubernetes.demo.util.Constants.*;

@Component
public class ApplicationService {
	private final KubernetesClient client;

	@Autowired
	public ApplicationService(KubernetesClient client) {
		this.client = client;
	}

	public void createApplication(CreationRequest request) {
		// Check namespace
		String namespaceName = request.getNamespace();
		checkNamespace(namespaceName);
		// Generate application name
		String applicationName = request.getName();
		// Check deployment
		String deploymentName = applicationName + DEPLOYMENT_SUFFIX;
		Deployment deployment = client.extensions().deployments().inNamespace(namespaceName).withName(deploymentName).get();
		Assert.isNull(deployment, "Deployment exists!");
		// Create deployment
		Set<Container> containers = request.getContainers().stream().map(DockerContainer::toContainer).collect(Collectors.toSet());
		deployment = client.extensions().deployments().createNew()
				.withNewMetadata().withName(deploymentName).withNamespace(namespaceName).endMetadata()
				.withNewSpec().withReplicas(1)
				.withNewSelector().addToMatchLabels(SELECTOR_LABEL, applicationName).endSelector()
				.withNewTemplate()
				.withNewMetadata().addToLabels(SELECTOR_LABEL, applicationName).endMetadata()
				.withNewSpec().addAllToContainers(containers).endSpec()
				.endTemplate()
				.endSpec()
				.done();
		Assert.notNull(deployment, "Fail to create deployment!");
		// Check service
		String serviceName = applicationName + SERVICE_SUFFIX;
		Service service = client.services().inNamespace(namespaceName).withName(serviceName).get();
		Assert.isNull(service, "Service exists!");
		// Create service
		Set<ServicePort> servicePorts = request.getPorts().stream().map(ExposedPort::toServicePort).collect(Collectors.toSet());
		service = client.services().createNew()
				.withNewMetadata().withName(serviceName).withNamespace(namespaceName).addToLabels(EXTERNAL_SERVICE_LABEL, serviceName).endMetadata()
				.withNewSpec().addToSelector(SELECTOR_LABEL, applicationName).addAllToPorts(servicePorts).endSpec()
				.done();
		Assert.notNull(service, "Fail to create service!");
	}

	public void deleteApplication(String namespaceName, String applicationName) {
		checkNamespace(namespaceName);
		// Delete service
		String serviceName = applicationName + SERVICE_SUFFIX;
		client.services().inNamespace(namespaceName).withName(serviceName).delete();
		// Delete deployment
		String deploymentName = applicationName + DEPLOYMENT_SUFFIX;
		client.extensions().deployments().inNamespace(namespaceName).withName(deploymentName).delete();
	}

	private void checkNamespace(String namespaceName) {
		Namespace namespace = client.namespaces().withName(namespaceName).get();
		Assert.notNull(namespace, "Namespace doesn't exist!");
		String userspace = namespace.getMetadata().getLabels().get(USERSPACE_LABEL);
		Assert.isTrue(namespaceName.equals(userspace), "Namespace isn't at userspace!");
	}
}
