package com.moekr.kubernetes.demo.web.controller.view;

import com.moekr.kubernetes.demo.service.NamespaceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/namespace")
public class NamespaceViewController {
	private final NamespaceService service;

	@GetMapping("/")
	public String list(Model model) {
		model.addAttribute("list", service.listNamespace());
		return "namespace/list";
	}
}
