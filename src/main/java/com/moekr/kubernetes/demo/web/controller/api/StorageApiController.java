package com.moekr.kubernetes.demo.web.controller.api;

import com.moekr.kubernetes.demo.model.Storage;
import com.moekr.kubernetes.demo.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class StorageApiController {
	private final StorageService service;

	@PostMapping("/storage")
	public Storage createStorage(@RequestBody Storage storage) {
		return service.createStorage(storage);
	}

	@DeleteMapping("/storage/{namespaceName}/{storageName}")
	public void deleteStorage(@PathVariable String namespaceName, @PathVariable String storageName) {
		service.deleteStorage(namespaceName, storageName);
	}
}
