package com.moekr.kubernetes.demo.service;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.AllArgsConstructor;
import org.springframework.util.Assert;

import static com.moekr.kubernetes.demo.util.Constants.USERSPACE_LABEL;

@AllArgsConstructor
public abstract class AbstractService {
	protected final KubernetesClient client;

	protected void checkNamespace(String namespaceName) {
		Namespace namespace = client.namespaces().withName(namespaceName).get();
		Assert.notNull(namespace, "名称空间不存在");
		String userspace = namespace.getMetadata().getLabels().get(USERSPACE_LABEL);
		Assert.isTrue(namespaceName.equals(userspace), "名称空间非法");
	}
}
