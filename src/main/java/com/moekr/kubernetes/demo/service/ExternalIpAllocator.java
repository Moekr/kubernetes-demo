package com.moekr.kubernetes.demo.service;

import com.moekr.kubernetes.demo.util.ApplicationConfiguration;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.moekr.kubernetes.demo.util.Constants.EXTERNAL_LABEL;

@CommonsLog
@RequiredArgsConstructor
@Component
public class ExternalIpAllocator {
	private final KubernetesClient client;
	private final ApplicationConfiguration configuration;

	private Map<String, IPAddress> rangeMap = new HashMap<>();
	private Set<IPAddress> leaseSet = new HashSet<>();

	@PostConstruct
	private void initialize() {
		for (Map.Entry<String, String> entry : configuration.getExternalIp().entrySet()) {
			IPAddress range = new IPAddressString(entry.getValue()).getAddress();
			rangeMap.put(entry.getKey(), range);
		}
		log.debug(rangeMap);
		List<Service> serviceList = client.services().withLabel(EXTERNAL_LABEL).list().getItems();
		for (Service service : serviceList) {
			for (String externalIp : service.getSpec().getExternalIPs()) {
				leaseSet.add(new IPAddressString(externalIp).getAddress());
			}
		}
		log.debug(leaseSet);
	}

	private String allocate(String nodeName) {
		IPAddress range = rangeMap.get(nodeName);
		Assert.notNull(range, "Can't allocate an address for node " + nodeName + "!");
		Iterator<? extends IPAddress> iterator = range.iterator();
		while (iterator.hasNext()) {
			IPAddress address = iterator.next();
			if (address.includesZeroHost() || address.includesMaxHost() || leaseSet.contains(address)) {
				continue;
			}
			IPAddress allocated = address.toAddressString().getHostAddress();
			leaseSet.add(allocated);
			return allocated.toString();
		}
		throw new UnsupportedOperationException("No available address for node " + nodeName + "!");
	}

	private boolean match(String nodeName, String externalIp) {
		IPAddress range = rangeMap.get(nodeName);
		IPAddress address = new IPAddressString(externalIp).getAddress();
		return range.contains(address)
				&& !address.includesZeroHost(range.getNetworkPrefixLength())
				&& !address.includesMaxHost(range.getNetworkPrefixLength());
	}

	private void release(String externalIp) {
		IPAddress address = new IPAddressString(externalIp).getAddress();
		leaseSet.remove(address);
	}

	@Scheduled(cron = "0 * * * * *")
	protected void checkExternalIpAllocation() {
		List<Service> serviceList = client.services().withLabel(EXTERNAL_LABEL).list().getItems();
		for (Service service : serviceList) {
			String namespaceName = service.getMetadata().getNamespace();
			String serviceName = service.getMetadata().getName();
			List<Pod> podList = client.pods().inNamespace(namespaceName).withLabels(service.getSpec().getSelector()).list().getItems();
			if (podList.size() != 1) {
				log.error("Can't allocate external IP to external service " +
						"[" + namespaceName + "/" + serviceName + "]" +
						" because it has more than one backend pods!");
				continue;
			}
			String nodeName = podList.get(0).getSpec().getNodeName();
			List<String> externalIps = service.getSpec().getExternalIPs();
			boolean hasMatch = false;
			for (String externalIp : externalIps) {
				if ((!hasMatch) && (hasMatch = match(nodeName, externalIp))) {
					if (externalIps.size() != 1) {
						log.debug("Reuse external IP " + externalIp + " to external service " + serviceName);
						client.services().inNamespace(namespaceName).withName(serviceName).edit()
								.editSpec().withExternalIPs(externalIp).endSpec()
								.done();
					}
				} else {
					release(externalIp);
				}
			}
			if (!hasMatch) {
				String externalIp = allocate(nodeName);
				log.debug("Allocate external IP " + externalIp + " to external service " + serviceName);
				client.services().inNamespace(namespaceName).withName(serviceName).edit()
						.editSpec().withExternalIPs(externalIp).endSpec()
						.done();
			}
		}
		serviceList = client.services().withLabel(EXTERNAL_LABEL).list().getItems();
		leaseSet.clear();
		for (Service service : serviceList) {
			for (String externalIp : service.getSpec().getExternalIPs()) {
				leaseSet.add(new IPAddressString(externalIp).getAddress());
			}
		}
		log.debug(leaseSet);
	}
}
