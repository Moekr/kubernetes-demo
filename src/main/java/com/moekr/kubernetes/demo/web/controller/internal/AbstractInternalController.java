package com.moekr.kubernetes.demo.web.controller.internal;

import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractInternalController {
	protected final KubernetesClient client;
}
