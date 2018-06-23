package com.moekr.kubernetes.demo.web.controller.api;

import com.moekr.kubernetes.demo.model.Application;
import com.moekr.kubernetes.demo.service.ApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ApplicationApiController {
	private final ApplicationService service;

	@PostMapping("/application")
	public Application createApplication(@RequestBody Application application) {
		return service.createApplication(application);
	}

	@DeleteMapping("/application/{namespaceName}/{applicationName}")
	public void deleteApplication(@PathVariable String namespaceName, @PathVariable String applicationName) {
		service.deleteApplication(namespaceName, applicationName);
	}
}
