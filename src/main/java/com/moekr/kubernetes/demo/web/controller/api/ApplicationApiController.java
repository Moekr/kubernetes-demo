package com.moekr.kubernetes.demo.web.controller.api;

import com.moekr.kubernetes.demo.model.CreationRequest;
import com.moekr.kubernetes.demo.model.vo.Application;
import com.moekr.kubernetes.demo.service.ApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class  ApplicationApiController {
	private final ApplicationService applicationService;

	@PostMapping("/application")
	public Application createApplication(@RequestBody CreationRequest request) {
		return applicationService.createApplication(request);
	}

	@DeleteMapping("/application/{namespaceName}/{applicationName}")
	public void deleteApplication(@PathVariable String namespaceName, @PathVariable String applicationName) {
		applicationService.deleteApplication(namespaceName, applicationName);
	}
}
