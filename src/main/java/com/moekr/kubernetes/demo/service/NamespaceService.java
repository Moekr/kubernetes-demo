package com.moekr.kubernetes.demo.service;

import com.moekr.kubernetes.demo.model.vo.NamespaceItem;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.moekr.kubernetes.demo.util.Constants.USERSPACE_LABEL;

@AllArgsConstructor
@Service
public class NamespaceService {
	private final KubernetesClient client;

	public List<NamespaceItem> listNamespace() {
		NamespaceList list = client.namespaces().withLabel(USERSPACE_LABEL).list();
		List<Namespace> namespaceList = list.getItems();
		return namespaceList.stream()
				.map(NamespaceItem::new)
				.collect(Collectors.toList());
	}
}
