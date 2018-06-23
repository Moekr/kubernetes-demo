package com.moekr.kubernetes.demo.service;

import com.moekr.kubernetes.demo.model.Application;
import com.moekr.kubernetes.demo.model.ApplicationItem;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

import static com.moekr.kubernetes.demo.util.Constants.USERSPACE_LABEL;

@org.springframework.stereotype.Service
public class ApplicationService extends AbstractService {
	@Autowired
	public ApplicationService(KubernetesClient client) {
		super(client);
	}

	public Application createApplication(Application application) {
		String namespaceName = application.getNamespace();
		checkNamespace(namespaceName);
		String applicationName = application.getName();
		Deployment deployment = client.extensions().deployments().inNamespace(namespaceName).withName(applicationName).get();
		Assert.isNull(deployment, "已有同名部署");
		deployment = client.extensions().deployments().create(application.toDeployment());
		Assert.notNull(deployment, "创建部署失败");
		Service service = client.services().inNamespace(namespaceName).withName(applicationName).get();
		Assert.isNull(service, "已有同名服务");
		service = client.services().create(application.toService());
		Assert.notNull(service, "创建服务失败");
		return new Application(service, deployment);
	}

	public List<ApplicationItem> listApplication(String namespaceName) {
		ServiceList list;
		if (namespaceName == null) {
			list = client.services().withLabel(USERSPACE_LABEL).list();
		} else {
			checkNamespace(namespaceName);
			list = client.services().inNamespace(namespaceName).withLabel(USERSPACE_LABEL).list();
		}
		List<Service> serviceList = list.getItems();
		return serviceList.stream().map(ApplicationItem::new).collect(Collectors.toList());
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
		result = client.services().inNamespace(namespaceName).withName(applicationName).delete();
		Assert.isTrue(result, "服务不存在");
		result = client.extensions().deployments().inNamespace(namespaceName).withName(applicationName).delete();
		Assert.isTrue(result, "部署不存在");
	}
}
