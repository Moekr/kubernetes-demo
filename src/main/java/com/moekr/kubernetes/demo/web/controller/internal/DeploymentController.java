package com.moekr.kubernetes.demo.web.controller.internal;

import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/internal/deployment")
public class DeploymentController extends AbstractInternalController {
	@Autowired
	public DeploymentController(KubernetesClient client) {
		super(client);
	}

	@GetMapping("/")
	public String deployment(@RequestParam(required = false) String namespace, Model model) {
		DeploymentList list = (namespace == null ? client.extensions().deployments() : client.extensions().deployments().inNamespace(namespace)).list();
		List<Deployment> deploymentList = list.getItems();
		model.addAttribute("deploymentList", deploymentList);
		return "internal/deployment";
	}
}
