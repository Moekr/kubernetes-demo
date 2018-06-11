package com.moekr.kubernetes.demo.web.controller.api;

import com.moekr.kubernetes.demo.model.CreationRequest;
import com.moekr.kubernetes.demo.service.ApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ApplicationController {
	private final ApplicationService applicationService;

	@PostMapping("/application")
	public void createApplication(@RequestBody CreationRequest request) {
		applicationService.createApplication(request);
	}

	@DeleteMapping("/application/{namespace}/{name}")
	public void deleteApplication(@PathVariable String namespace, @PathVariable String name) {
		applicationService.deleteApplication(namespace, name);
	}
}
