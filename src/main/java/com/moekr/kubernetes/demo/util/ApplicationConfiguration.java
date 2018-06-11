package com.moekr.kubernetes.demo.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties("kubernetes")
public class ApplicationConfiguration {
	private Map<String, String> externalIp = new HashMap<>();
}
