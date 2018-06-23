package com.moekr.kubernetes.demo.model;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ServicePort;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.moekr.kubernetes.demo.util.Constants.NAME_PATTERN;

@Data
@NoArgsConstructor
public class Port {
	@NotNull
	@Pattern(regexp = NAME_PATTERN)
	private String name;
	@NotNull
	@Range(min = 1, max = 65535)
	private Integer port;
	@NotNull
	@Pattern(regexp = "^(TCP|UDP)$")
	private String protocol;

	public Port(ServicePort servicePort) {
		this.name = servicePort.getName();
		this.port = servicePort.getPort();
		this.protocol = servicePort.getProtocol();
	}

	public ServicePort toServicePort() {
		ServicePort servicePort = new ServicePort();
		servicePort.setName(name);
		servicePort.setPort(port);
		servicePort.setProtocol(protocol);
		servicePort.setTargetPort(new IntOrString(port));
		return servicePort;
	}
}
