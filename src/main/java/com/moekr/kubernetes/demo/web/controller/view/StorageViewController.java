package com.moekr.kubernetes.demo.web.controller.view;

import com.moekr.kubernetes.demo.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/storage")
public class StorageViewController {
	private final StorageService service;

	@GetMapping({"/", "/{namespaceName}/"})
	public String list(Model model, @PathVariable(required = false) String namespaceName) {
		model.addAttribute("list", service.listStorage(namespaceName));
		return "storage/list";
	}

	@GetMapping("/{namespaceName}/{storageName}/")
	public String detail(Model model, @PathVariable String namespaceName, @PathVariable String storageName) {
		model.addAttribute("item", service.getStorage(namespaceName, storageName));
		return "storage/detail";
	}
}
