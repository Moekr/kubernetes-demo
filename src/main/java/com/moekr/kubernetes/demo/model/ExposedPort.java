package com.moekr.kubernetes.demo.model;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ServicePort;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExposedPort {
	private String name;
	private int port;
	private String protocol;

	public ServicePort toServicePort() {
		ServicePort servicePort = new ServicePort();
		servicePort.setName(name);
		servicePort.setPort(port);
		servicePort.setProtocol(protocol);
		servicePort.setTargetPort(new IntOrString(port));
		return servicePort;
	}
}
