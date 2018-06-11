package com.moekr.kubernetes.demo.web.controller.view;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/node")
public class NodeController extends AbstractController {
	@Autowired
	public NodeController(KubernetesClient client) {
		super(client);
	}

	@GetMapping("/")
	public String node(Model model) {
		NodeList list = client.nodes().list();
		List<Node> nodeList = list.getItems();
		model.addAttribute("nodeList", nodeList);
		return "node";
	}
}
