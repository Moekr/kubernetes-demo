package com.moekr.kubernetes.demo.web.controller.api;

import com.moekr.kubernetes.demo.model.Namespace;
import com.moekr.kubernetes.demo.service.NamespaceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class NamespaceApiController {
	private final NamespaceService service;

	@PostMapping("/namespace")
	public Namespace createNamespace(@RequestBody Namespace namespace) {
		return service.createNamespace(namespace);
	}

	@DeleteMapping("/namespace/{namespaceName}")
	public void deleteNamespace(@PathVariable String namespaceName) {
		service.deleteNamespace(namespaceName);
	}
}
