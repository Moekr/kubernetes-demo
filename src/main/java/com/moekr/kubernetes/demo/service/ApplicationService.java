package com.moekr.kubernetes.demo.service;

import com.moekr.kubernetes.demo.model.CreationRequest;
import com.moekr.kubernetes.demo.model.DockerContainer;
import com.moekr.kubernetes.demo.model.ExposedPort;
import com.moekr.kubernetes.demo.model.vo.Application;
import com.moekr.kubernetes.demo.model.vo.ApplicationItem;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.AllArgsConstructor;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.moekr.kubernetes.demo.util.Constants.*;

@AllArgsConstructor
@org.springframework.stereotype.Service
public class ApplicationService {
	private final KubernetesClient client;

	public Application createApplication(CreationRequest request) {
		// Check namespace
		String namespaceName = request.getNamespace();
		checkNamespace(namespaceName);
		// Generate application name
		String applicationName = request.getName();
		// Check deployment
		Deployment deployment = client.extensions().deployments().inNamespace(namespaceName).withName(applicationName).get();
		Assert.isNull(deployment, "已有同名部署");
		// Create deployment
		Set<Container> containers = request.getContainers().stream().map(DockerContainer::toContainer).collect(Collectors.toSet());
		deployment = client.extensions().deployments().createNew()
				.withNewMetadata().withName(applicationName).withNamespace(namespaceName).endMetadata()
				.withNewSpec().withReplicas(1)
				.withNewSelector().addToMatchLabels(SELECTOR_LABEL, applicationName).endSelector()
				.withNewTemplate()
				.withNewMetadata().addToLabels(SELECTOR_LABEL, applicationName).endMetadata()
				.withNewSpec().addAllToContainers(containers).endSpec()
				.endTemplate()
				.endSpec()
				.done();
		Assert.notNull(deployment, "创建部署失败");
		// Check service
		Service service = client.services().inNamespace(namespaceName).withName(applicationName).get();
		Assert.isNull(service, "已有同名服务");
		// Create service
		Set<ServicePort> servicePorts = request.getPorts().stream().map(ExposedPort::toServicePort).collect(Collectors.toSet());
		service = client.services().createNew()
				.withNewMetadata().withName(applicationName).withNamespace(namespaceName).addToLabels(USERSPACE_LABEL, namespaceName).addToLabels(EXTERNAL_LABEL, applicationName).endMetadata()
				.withNewSpec().addToSelector(SELECTOR_LABEL, applicationName).addAllToPorts(servicePorts).endSpec()
				.done();
		Assert.notNull(service, "创建服务失败");
		return new Application(service, deployment);
	}

	public List<ApplicationItem> listApplication(String namespaceName) {
		ServiceList list;
		if (namespaceName == null) {
			list = client.services().withLabel(USERSPACE_LABEL).list();
		} else {
			checkNamespace(namespaceName);
			list = client.services().inNamespace(namespaceName).list();
		}
		List<Service> serviceList = list.getItems();
		return serviceList.stream()
				.map(ApplicationItem::new)
				.collect(Collectors.toList());
	}

	public Application getApplication(String namespaceName, String applicationName) {
		checkNamespace(namespaceName);
		Service service = client.services().inNamespace(namespaceName).withName(applicationName).get();
		Assert.notNull(service, "服务不存在");
		Deployment deployment = client.extensions().deployments().inNamespace(namespaceName).withName(applicationName).get();
		Assert.notNull(deployment, "部署不存在");
		return new Application(service, deployment);
	}

	public void deleteApplication(String namespaceName, String applicationName) {
		checkNamespace(namespaceName);
		boolean result;
		// Delete service
		result = client.services().inNamespace(namespaceName).withName(applicationName).delete();
		Assert.isTrue(result, "服务不存在");
		// Delete deployment
		result = client.extensions().deployments().inNamespace(namespaceName).withName(applicationName).delete();
		Assert.isTrue(result, "部署不存在");
	}

	private void checkNamespace(String namespaceName) {
		Namespace namespace = client.namespaces().withName(namespaceName).get();
		Assert.notNull(namespace, "名称空间不存在");
		String userspace = namespace.getMetadata().getLabels().get(USERSPACE_LABEL);
		Assert.isTrue(namespaceName.equals(userspace), "名称空间非法");
	}
}
