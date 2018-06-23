package com.moekr.kubernetes.demo.service;

import com.moekr.kubernetes.demo.model.Storage;
import com.moekr.kubernetes.demo.model.StorageItem;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.api.model.extensions.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.moekr.kubernetes.demo.util.Constants.RESOURCE_STORAGE;
import static com.moekr.kubernetes.demo.util.Constants.USERSPACE_LABEL;

@Service
public class StorageService extends AbstractService {
	@Autowired
	public StorageService(KubernetesClient client) {
		super(client);
	}

	public Storage createStorage(Storage storage) {
		String namespaceName = storage.getNamespace();
		String storageName = storage.getName();
		checkNamespace(namespaceName);
		PersistentVolumeClaim claim = client.persistentVolumeClaims().inNamespace(namespaceName).withName(storageName).get();
		Assert.isNull(claim, "已有同名持久卷");
		claim = client.persistentVolumeClaims().create(storage.toPersistentVolumeClaim());
		Assert.notNull(claim, "创建持久卷声明失败");
		return new Storage(claim, Collections.emptyList());
	}

	public List<StorageItem> listStorage(String namespaceName) {
		PersistentVolumeClaimList list;
		if (namespaceName == null) {
			list = client.persistentVolumeClaims().withLabel(USERSPACE_LABEL).list();
		} else {
			checkNamespace(namespaceName);
			list = client.persistentVolumeClaims().inNamespace(namespaceName).list();
		}
		List<PersistentVolumeClaim> claimList = list.getItems();
		return claimList.stream().map(StorageItem::new).collect(Collectors.toList());
	}

	public Storage getStorage(String namespaceName, String storageName) {
		checkNamespace(namespaceName);
		PersistentVolumeClaim claim = client.persistentVolumeClaims().inNamespace(namespaceName).withName(storageName).get();
		Assert.notNull(claim, "持久卷声明不存在");
		DeploymentList list = client.extensions().deployments().inNamespace(namespaceName).withLabel(RESOURCE_STORAGE + "/" + storageName).list();
		return new Storage(claim, list.getItems());
	}

	public void deleteStorage(String namespaceName, String storageName) {
		checkNamespace(namespaceName);
		boolean result = client.persistentVolumeClaims().inNamespace(namespaceName).withName(storageName).delete();
		Assert.isTrue(result, "持久卷声明不存在");
	}
}
