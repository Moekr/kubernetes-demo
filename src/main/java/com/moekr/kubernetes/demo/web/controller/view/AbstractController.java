package com.moekr.kubernetes.demo.web.controller.view;

import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract class AbstractController {
	protected final KubernetesClient client;
}
