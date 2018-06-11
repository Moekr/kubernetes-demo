package com.moekr.kubernetes.demo.web.controller.view;

import com.moekr.kubernetes.demo.util.Constants;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/namespace")
public class NamespaceController extends AbstractController {
	@Autowired
	public NamespaceController(KubernetesClient client) {
		super(client);
	}

	@GetMapping("/")
	public String namespace(Model model) {
		NamespaceList list = client.namespaces().withLabel(Constants.USERSPACE_LABEL).list();
		List<Namespace> namespaceList = list.getItems();
		model.addAttribute("namespaceList", namespaceList);
		return "namespace";
	}
}
