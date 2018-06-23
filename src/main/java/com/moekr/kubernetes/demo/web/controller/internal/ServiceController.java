package com.moekr.kubernetes.demo.web.controller.internal;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/internal/service")
public class ServiceController extends AbstractInternalController {
	@Autowired
	public ServiceController(KubernetesClient client) {
		super(client);
	}

	@GetMapping("/")
	public String service(@RequestParam(required = false) String namespace, Model model) {
		ServiceList list = (namespace == null ? client.services() : client.services().inNamespace(namespace)).list();
		List<Service> serviceList = list.getItems();
		model.addAttribute("serviceList", serviceList);
		return "internal/service";
	}
}
