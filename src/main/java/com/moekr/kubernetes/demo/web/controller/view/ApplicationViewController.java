package com.moekr.kubernetes.demo.web.controller.view;

import com.moekr.kubernetes.demo.service.ApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/application")
public class ApplicationViewController {
	private final ApplicationService service;

	@GetMapping({"/", "/{namespaceName}/"})
	public String list(Model model, @PathVariable(required = false) String namespaceName) {
		model.addAttribute("list", service.listApplication(namespaceName));
		return "application/list";
	}

	@GetMapping("/{namespaceName}/{applicationName}/")
	public String detail(Model model, @PathVariable String namespaceName, @PathVariable String applicationName) {
		model.addAttribute("item", service.getApplication(namespaceName, applicationName));
		return "application/detail";
	}
}
