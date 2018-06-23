package com.moekr.kubernetes.demo.service;

import com.moekr.kubernetes.demo.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.extensions.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

import static com.moekr.kubernetes.demo.util.Constants.USERSPACE_LABEL;

@Service
public class NamespaceService extends AbstractService {
	@Autowired
	public NamespaceService(KubernetesClient client) {
		super(client);
	}

	public Namespace createNamespace(Namespace namespace) {
		String namespaceName = namespace.getName();
		io.fabric8.kubernetes.api.model.Namespace _namespace = client.namespaces().withName(namespaceName).get();
		Assert.isNull(_namespace, "已有同名名称空间");
		_namespace = client.namespaces().create(namespace.toNamespace());
		Assert.notNull(_namespace, "创建名称空间失败");
		return new Namespace(_namespace);
	}

	public List<Namespace> listNamespace() {
		NamespaceList list = client.namespaces().withLabel(USERSPACE_LABEL).list();
		List<io.fabric8.kubernetes.api.model.Namespace> namespaceList = list.getItems();
		return namespaceList.stream().map(Namespace::new).collect(Collectors.toList());
	}

	public void deleteNamespace(String namespaceName) {
		checkNamespace(namespaceName);
		ServiceList serviceList = client.services().inNamespace(namespaceName).withLabel(USERSPACE_LABEL).list();
		Assert.isTrue(serviceList.getItems().isEmpty(), "名称空间含有未被删除的服务");
		DeploymentList deploymentList = client.extensions().deployments().inNamespace(namespaceName).list();
		Assert.isTrue(deploymentList.getItems().isEmpty(), "名称空间含有未被删除的部署");
		PersistentVolumeClaimList claimList = client.persistentVolumeClaims().inNamespace(namespaceName).withLabel(USERSPACE_LABEL).list();
		Assert.isTrue(claimList.getItems().isEmpty(), "名称空间含有未被删除的持久卷");
		boolean result = client.namespaces().withName(namespaceName).delete();
		Assert.isTrue(result, "删除名称空间失败");
	}
}
